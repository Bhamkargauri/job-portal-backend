package com.crm.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crm.exceptions.ResourceNotFoundException;
import com.crm.models.Candidate;
import com.crm.models.JobPosting;
import com.crm.models.User;
import com.crm.repositories.CandidateRepository;
import com.crm.services.CandidateService;
import com.crm.services.JobPostingService;
import com.crm.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/candidate")
public class CandidateController {

	@Autowired
	private CandidateService candidateService;

	@Autowired
	private JobPostingService jobPostingService;

	@Autowired
	private CandidateRepository candidateRepository;

	@Autowired
	private UserService userService;

	private final String errorMessage = "Something went wrong. please try again!";

	@PostMapping("/create")
	@PreAuthorize("hasAuthority('FRANCHISOR')")
	public ResponseEntity<?> createCandidate(@ModelAttribute @Valid Candidate candidate,
			@RequestParam MultipartFile resumeFile) throws IOException {

		try {
			if (candidateRepository.findByEmail(candidate.getEmail()).isPresent()) {
				return ResponseEntity.badRequest().body("this user already exists :" + candidate.getEmail());
			}
			User loginUser = userService.getAuthenticateUser();

			candidate.setJobPost(candidate.getJobPost().replaceAll("\\s+", "").toLowerCase());
			candidate.setCreated(loginUser);
			candidate.setModified(loginUser);
			candidate.setCreatedDate(LocalDate.now());
			candidate.setModifiedDate(LocalDate.now());
			candidate.setStatus(1);

			if (resumeFile != null && !resumeFile.isEmpty()) {
				candidate.setResume(resumeFile.getBytes());
				candidate.setResumeOriginalFileName(resumeFile.getOriginalFilename());
				candidate.setResumeContentType(resumeFile.getContentType());
			}
			boolean isSaved = candidateService.saveCandidate(candidate);
			if (isSaved) {
				return ResponseEntity.ok("Candidate saved success!");
			} else {
				return ResponseEntity.badRequest().body("Candidate not saved! please try again");
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(errorMessage);
		}

	}

	@PostMapping("/viaExcel")
	@PreAuthorize("hasAuthority('FRANCHISOR')")
	public ResponseEntity<?> createCandidateByExcel(@RequestParam MultipartFile file) throws IOException {
		try {

			List<Candidate> insertedRecords = new ArrayList<>();
			List<String> skippedRecords = new ArrayList<>();

			User authenticateUser = userService.getAuthenticateUser();

			try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {

				Sheet sheet = workbook.getSheetAt(0); // Read first sheet
				Row headerRow = sheet.getRow(0); // Get header row

				// Map to store column index positions dynamically
				Map<String, Integer> columnIndexMap = new HashMap<>();
				for (Cell cell : headerRow) {
					columnIndexMap.put(cell.getStringCellValue().trim().toLowerCase(), cell.getColumnIndex());
				}
				// Iterate over rows (starting from row 1)
				for (int i = 1; i <= sheet.getLastRowNum(); i++) {
					Row row = sheet.getRow(i);
					if (row == null)
						continue;

					String name = getCellValue(row, columnIndexMap.get("name"));
					String email = getCellValue(row, columnIndexMap.get("email"));
					String qualification = getCellValue(row, columnIndexMap.get("qualification"));
					String jobPost = getCellValue(row, columnIndexMap.get("job post")).toLowerCase();
					String experience = getCellValue(row, columnIndexMap.get("experience"));
					String address = getCellValue(row, columnIndexMap.get("address"));
					String phoneNo = getCellValue(row, columnIndexMap.get("phone no"));
					// Check if the record exists
					if (candidateRepository.findByEmail(email).isPresent()) {
						skippedRecords.add(name + " (" + phoneNo + ")");
					} else {
						// Insert new record
						Candidate candidate = new Candidate(null, name, email, qualification, jobPost, experience,
								address, phoneNo, 0, null, null, null, 1, authenticateUser, authenticateUser,
								LocalDate.now(), LocalDate.now());
						candidateService.saveCandidate(candidate);
						insertedRecords.add(candidate);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.badRequest().body(errorMessage);
			}

			// Return response
			Map<String, Object> response = new HashMap<>();
			response.put("insertedRecords", insertedRecords.size());
			response.put("skippedRecords", skippedRecords.size());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	@PutMapping("/update/{id}")
	@PreAuthorize("hasAuthority('FRANCHISOR')")
	public ResponseEntity<?> updateCandidate(@PathVariable Long id, @ModelAttribute @Valid Candidate updatedCandidate,
			@RequestParam MultipartFile resumeFile) throws IOException {

		try {

			User loginUser = userService.getAuthenticateUser();

			Candidate candidate = candidateService.getCandidateById(id);
			if (candidate == null) {
				return ResponseEntity.badRequest().body("Candidate not found with this id " + id);
			}
			candidate.setName(updatedCandidate.getName());
			candidate.setQualification(updatedCandidate.getQualification());
			candidate.setJobPost(updatedCandidate.getJobPost().replaceAll("\\s+", "").toLowerCase());
			candidate.setExperience(updatedCandidate.getExperience());
			candidate.setPlacementStatus(updatedCandidate.getPlacementStatus());
			candidate.setAddress(updatedCandidate.getAddress());
			candidate.setPhoneNo(updatedCandidate.getPhoneNo());
			candidate.setModified(loginUser);
			candidate.setModifiedDate(LocalDate.now());
			if (updatedCandidate.getResume() != null) {
				candidate.setResume(resumeFile.getBytes());
				candidate.setResumeOriginalFileName(resumeFile.getOriginalFilename());
				candidate.setResumeContentType(resumeFile.getContentType());
			}

			boolean isSaved = candidateService.saveCandidate(candidate);
			if (isSaved) {
				return ResponseEntity.ok("Candidate update success");
			} else {
				return ResponseEntity.badRequest().body("Candidate not updated. try again!");
			}

		} catch (Exception e) {
			return ResponseEntity.badRequest().body(errorMessage);
		}

	}

	@PatchMapping("/{id}/uploadResume")
	@PreAuthorize("hasAuthority('FRANCHISOR')")
	public ResponseEntity<?> uploadResume(@PathVariable Long id, @RequestParam MultipartFile resume) {
		try {
			Candidate candidate = candidateService.getCandidateById(id);
			if (candidate == null) {
				return ResponseEntity.badRequest().body("Candidate not found with this Id " + id);
			}
			candidate.setResume(resume.getBytes());
			candidateService.saveCandidate(candidate);
			return ResponseEntity.ok("Resume added success.");
		} catch (IOException e) {
			return ResponseEntity.badRequest().body(errorMessage);
		}

	}

	@GetMapping("/getAll")
	@PreAuthorize("hasAuthority('FRANCHISOR')")
	public ResponseEntity<?> getAllCandidates() {
		try {
			List<Candidate> candidates = candidateService.getAllCandidates();
			if (candidates.isEmpty()) {
				return ResponseEntity.ok("No Candidate found");
			}
			return ResponseEntity.ok(candidates);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	@GetMapping("/getById/{id}")
	@PreAuthorize("hasAuthority('FRANCHISOR')")
	public ResponseEntity<?> getCandidateById(@PathVariable Long id) {
		try {
			Candidate candidate = candidateService.getCandidateById(id);
			if (candidate == null) {
				return ResponseEntity.badRequest().body("Candidate not found with this id " + id);
			}
			return ResponseEntity.ok(candidate);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(errorMessage);
		}

	}

	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasAuthority('FRANCHISOR')")
	public ResponseEntity<String> deleteCandidate(@PathVariable Long id) {
		try {
			User loginUser = userService.getAuthenticateUser();

			Candidate candidate = candidateService.getCandidateById(id);
			if (candidate == null) {
				return ResponseEntity.badRequest().body("Candidate not found with this id " + id);
			}
			candidate.setStatus(0);
			candidate.setModified(loginUser);
			candidate.setModifiedDate(LocalDate.now());
			candidateService.saveCandidate(candidate);
			return ResponseEntity.ok("Candidate Delete Success");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	@GetMapping("/getByJobPost/{jobPost}")
	@PreAuthorize("hasAuthority('FRANCHISOR')")
	public ResponseEntity<?> getCandidateByJobType(@PathVariable String jobPost) {
		try {
			List<Candidate> candidates = candidateService.getCandidatesByJobPost(jobPost);

			if (candidates.isEmpty()) {
				return ResponseEntity.ok("No Candidate Found");
			}
			return ResponseEntity.ok(candidates);

		} catch (Exception e) {
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

//	@GetMapping("/downloadResume/{id}")
//	@PreAuthorize("hasAuthority('FRANCHISOR')")
//	public ResponseEntity<?> downloadResume(@PathVariable Long id) {
//		Optional<Candidate> candidateOptional = candidateService.getCandidateById(id);
//
//		if (candidateOptional.isEmpty()) {
//			throw new ResourceNotFoundException("Candidate not found with this id " + id);
//		}
//
//		if (candidateOptional.isPresent() && candidateOptional.get().getResume() != null) {
//			Candidate candidate = candidateOptional.get();
//
//			// Set response headers
//			HttpHeaders headers = new HttpHeaders();
//			headers.add(HttpHeaders.CONTENT_DISPOSITION,
//					"attachment; filename=\"" + candidate.getResumeOriginalFileName() + "\"");
//			headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
//
//			return new ResponseEntity<>(candidate.getResume(), headers, HttpStatus.OK);
//		} else {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//		}
//	}

	@GetMapping("/{id}/downloadResume")
	@PreAuthorize("hasAuthority('FRANCHISOR')")
	public ResponseEntity<?> downloadResumeOriginalForm(@PathVariable Long id) {
		try {
			Candidate candidate = candidateService.getCandidateById(id);

			if (candidate != null && candidate.getResume() != null) {
				HttpHeaders headers = new HttpHeaders();
				headers.add(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"" + candidate.getResumeOriginalFileName() + "\"");

				// Use the stored MIME type (default to application/octet-stream if missing)
				String contentType = candidate.getResumeContentType() != null ? candidate.getResumeContentType()
						: MediaType.APPLICATION_OCTET_STREAM_VALUE;
				headers.setContentType(MediaType.parseMediaType(contentType));

				return new ResponseEntity<>(candidate.getResume(), headers, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Candidate resume not found. please try again!");
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(errorMessage);
		}

	}
//
//	@GetMapping("/{id}/saveResume")
//	@PreAuthorize("hasAuthority('FRANCHISOR')")
//	public ResponseEntity<?> saveResume(@PathVariable Long id) {
//		Optional<Candidate> candidateOptional = candidateService.getCandidateById(id);
//
//		Map<String, String> map = new HashMap<>();
//
//		if (candidateOptional.isPresent() && candidateOptional.get().getResume() != null) {
//			Candidate candidate = candidateOptional.get();
//
//			// Get system default Downloads folder
//			String defaultDownloadPath = getDefaultDownloadPath();
//			if (defaultDownloadPath == null) {
//				return ResponseEntity.internalServerError().body("Could not determine the default download folder.");
//			}
//
//			// Ensure the directory exists
//			File directory = new File(defaultDownloadPath);
//			if (!directory.exists()) {
//				directory.mkdirs(); // Create the directory if it doesn't exist
//			}
//
//			// Save the file
//			String filePath = Paths.get(defaultDownloadPath, candidate.getResumeOriginalFileName()).toString();
//			try (FileOutputStream fos = new FileOutputStream(filePath)) {
//				fos.write(candidate.getResume());
//
//				// Return a clickable file:// URL
//				String fileUrl = getFileUrl(filePath);
//				return ResponseEntity.ok("Resume saved successfully. Click to open: " + fileUrl);
//			} catch (IOException e) {
//				return ResponseEntity.internalServerError().body("Error saving file: " + e.getMessage());
//			}
//		} else {
//			return ResponseEntity.status(404).body("Resume not found for candidate ID: " + id);
//		}
//	}

	@GetMapping("/placed")
	@PreAuthorize("hasAuthority('FRANCHISOR')")
	public ResponseEntity<?> getPlacedCandidates() {
		try {
			List<Candidate> candidates = candidateService.getPlacedCandidates();
			if (candidates.isEmpty()) {
				return ResponseEntity.ok("Candidates not found");
			}
			return ResponseEntity.ok(candidates);

		} catch (Exception e) {
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	@GetMapping("/unplaced")
	@PreAuthorize("hasAuthority('FRANCHISOR')")
	public ResponseEntity<?> getUnplacedCandidates() {
		try {
			List<Candidate> candidates = candidateService.getUnplacedCandidates();
			if (candidates.isEmpty()) {
				return ResponseEntity.ok("Candidates Not Found");
			}
			return ResponseEntity.ok(candidates);

		} catch (Exception e) {
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	@GetMapping("/inProcess")
	@PreAuthorize("hasAuthority('FRANCHISOR')")
	public ResponseEntity<?> getInProcessCandidates() {
		try {
			List<Candidate> candidates = candidateService.getInProcessCandidates();
			if (candidates.isEmpty()) {
				return ResponseEntity.ok("Candidates Not found");
			}
			
			return ResponseEntity.ok(candidates);

		} catch (Exception e) {
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

//	private String getFileUrl(String filePath) {
//		try {
//			File file = new File(filePath);
//			return "file:///" + file.getAbsolutePath().replace("\\", "/");
//		} catch (Exception e) {
//			return null;
//		}
//		
//	}

	private String getCellValue(Row row, Integer colIndex) {
		if (colIndex == null)
			return "";
		Cell cell = row.getCell(colIndex);
		if (cell == null)
			return "";
		return switch (cell.getCellType()) {
		case STRING -> cell.getStringCellValue().trim();
		case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
		case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
		default -> "";
		};
	}

//	private String getDefaultDownloadPath() {
//		String os = System.getProperty("os.name").toLowerCase();
//		String home = System.getProperty("user.home");
//
//		if (os.contains("win")) {
//			return Paths.get(home, "Downloads").toString();
//		} else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
//			return Paths.get(home, "Downloads").toString();
//		} else {
//			return null; // Unknown OS
//		}
//	}
}
