package com.crm.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import com.crm.services.JobPostingService;
import com.crm.services.UserService;

@RestController
@RequestMapping("/jobPosting")
public class JobPostingController {

	@Autowired
	private JobPostingService jobPostingService;

	@Autowired
	private UserService userService;

	private final String errorMessage = "Something went wrong. please try again!";

	@PostMapping("/create")
	@PreAuthorize("hasAuthority('COMPANY')")
	public synchronized ResponseEntity<?> createJob(@ModelAttribute JobPosting jobPosting, @RequestParam("docc") MultipartFile doc)
			throws IOException {

		try {
			if (doc != null && !doc.isEmpty()) {
				jobPosting.setDoc(doc.getBytes());
				jobPosting.setDocOriginalFileName(doc.getOriginalFilename());
				jobPosting.setDocContentType(doc.getContentType());
			}
			 User loginUser = userService.getAuthenticateUser();
			jobPosting.setLocation(jobPosting.getLocation().replaceAll("\\s+", "").toLowerCase());
			jobPosting.setJobType(jobPosting.getJobType().toLowerCase());
			jobPosting.setJobTitle(jobPosting.getJobTitle().replaceAll("\\s+", "").toLowerCase());
			jobPosting.setCreated(loginUser);
			jobPosting.setModified(loginUser);
			jobPosting.setCreatedDate(LocalDate.now());
			jobPosting.setModifiedDate(LocalDate.now());
			jobPosting.setStatus(1);
			boolean isSaved = jobPostingService.saveJob(jobPosting);
			if (isSaved) {
				return ResponseEntity.ok("Job post saved successfully.");
			} else {
				return ResponseEntity.badRequest().body("Job post not saved. try again!");
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	@PutMapping("/update/{id}")
	@PreAuthorize("hasAuthority('COMPANY')")
	public synchronized ResponseEntity<?> updateJob(@PathVariable Long id, @ModelAttribute JobPosting updatedJobPosting,
			@RequestParam("docc") MultipartFile doc) throws IOException {

		try {
			JobPosting jobPosting = jobPostingService.getJobById(id);

			if (jobPosting == null) {
				return ResponseEntity.badRequest().body("Job Posting Not found with this Id " + id);
			}
			if (doc != null && !doc.isEmpty()) {
				jobPosting.setDoc(doc.getBytes());
				jobPosting.setDocOriginalFileName(doc.getOriginalFilename());
				jobPosting.setDocContentType(doc.getContentType());
			}

			 User loginUser = userService.getAuthenticateUser();

			jobPosting.setCompanyName(updatedJobPosting.getCompanyName());
			jobPosting.setContactDetails(updatedJobPosting.getContactDetails());
			jobPosting.setDeadline(updatedJobPosting.getDeadline());
			jobPosting.setEmail(updatedJobPosting.getEmail());
			jobPosting.setExperience(updatedJobPosting.getExperience());
			jobPosting.setHours(updatedJobPosting.getHours());
			jobPosting.setLocation(updatedJobPosting.getLocation().replaceAll("\\s+", "").toLowerCase());
			jobPosting.setJobType(updatedJobPosting.getJobType().toLowerCase());
			jobPosting.setJobTitle(updatedJobPosting.getJobTitle().replaceAll("\\s+", "").toLowerCase());
			jobPosting.setMaxSalary(updatedJobPosting.getMaxSalary());
			jobPosting.setMinSalary(updatedJobPosting.getMinSalary());
			jobPosting.setNoOfPosition(updatedJobPosting.getNoOfPosition());
			jobPosting.setPositionPerDepartment(updatedJobPosting.getPositionPerDepartment());
			jobPosting.setShift(updatedJobPosting.getShift());
			jobPosting.setModified(loginUser);
			jobPosting.setModifiedDate(LocalDate.now());
			jobPosting.setStatus(1);
			boolean isSaved = jobPostingService.saveJob(jobPosting);
			if (isSaved) {
				return ResponseEntity.ok("Job Post Updated!");
			} else {
				return ResponseEntity.badRequest().body("Job post Not updated. try again!");
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(errorMessage);
		}

		
	}

	// Get Active Jobs (Where Deadline Has Not Passed)
	@GetMapping("/activeJobs")
	@PreAuthorize("hasAuthority('COMPANY')")
	public synchronized ResponseEntity<?> getActiveJobs() {
		try {
			List<JobPosting> jobPostings = jobPostingService.getActiveJobs();
			if (jobPostings.isEmpty()) {
				return ResponseEntity.ok("No active jobs found");
			}
			return ResponseEntity.ok(jobPostings);
		} catch (Exception e) {
		return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	// Get expired jobs (where deadline has passed)
	@GetMapping("/expiredJobs")
	@PreAuthorize("hasAuthority('COMPANY')")
	public synchronized ResponseEntity<?> getExpiredJobs() {
		try {
			List<JobPosting> jobPostings = jobPostingService.getExpiredJobs();
			if (jobPostings.isEmpty()) {
				return ResponseEntity.ok("No Jobs Found");
			}
			return ResponseEntity.ok(jobPostings);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(errorMessage);
		}
		
	}

	@GetMapping("/getAllJobs")
	@PreAuthorize("hasAuthority('COMPANY')")
	public synchronized ResponseEntity<?> getAllJobs() {
		try {
			List<JobPosting> jobPostings = jobPostingService.getAllJobs();
			if (jobPostings.isEmpty()) {
				return ResponseEntity.ok("No Job found");
			}
			return ResponseEntity.ok(jobPostings);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	@GetMapping("/getJobById/{id}")
	@PreAuthorize("hasAuthority('COMPANY')")
	public synchronized ResponseEntity<?> getJobById(@PathVariable Long id) {
		try {
			JobPosting jobPosting = jobPostingService.getJobById(id);
			if (jobPosting == null) {
				return ResponseEntity.badRequest().body("No job post found for this id "+id);
			}
			return ResponseEntity.ok(jobPosting);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(errorMessage);
		}
		
	}
	

	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasAuthority('COMPANY')")
	public synchronized ResponseEntity<?> deleteJob(@PathVariable Long id) throws IOException {
		try {
			JobPosting jobPosting = jobPostingService.getJobById(id);
			if (jobPosting == null) {
				return ResponseEntity.badRequest().body("Job Posting Not Found for this ID " + id);
			}
			 User loginUser = userService.getAuthenticateUser();

			jobPosting.setStatus(0);
			jobPosting.setModified(loginUser);
			jobPosting.setModifiedDate(LocalDate.now());
			jobPostingService.saveJob(jobPosting);
			return ResponseEntity.ok("Job Posting Delete Success.");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(errorMessage);
		}
		
	}

	@GetMapping("/getByType/{jobType}")
	@PreAuthorize("hasAuthority('COMPANY')")
	public synchronized ResponseEntity<?> getJobsByJobType(@PathVariable String jobType) {
		try {
			List<JobPosting> jobPostings= jobPostingService.getJobsByJobType(jobType.replaceAll("\\s+", "").toLowerCase());
			if (jobPostings.isEmpty()) {
				return ResponseEntity.ok("No Job Post found.");
			}
			return ResponseEntity.ok(jobPostings);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	@GetMapping("/getByLocation/{location}")
	@PreAuthorize("hasAuthority('COMPANY')")
	public synchronized ResponseEntity<?> getJobsByLocation(@PathVariable String location) {
		try {
			List<JobPosting> jobPostings = jobPostingService.getJobsByLocation(location.replaceAll("\\s+", "").toLowerCase());
			if (jobPostings.isEmpty()) {
				return ResponseEntity.ok("No Job Post found");
			}
			return ResponseEntity.ok(jobPostings);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	@GetMapping("/{id}/downloadJobDocument")
	@PreAuthorize("hasAuthority('COMPANY')")
	public synchronized ResponseEntity<?> downloadResumeOriginalForm(@PathVariable Long id) {
		try {
			JobPosting jobPosting = jobPostingService.getJobById(id);

			if (jobPosting != null && jobPosting.getDoc() != null) {
				HttpHeaders headers = new HttpHeaders();
				headers.add(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"" + jobPosting.getDocOriginalFileName() + "\"");

				// Use the stored MIME type (default to application/octet-stream if missing)
				String contentType = jobPosting.getDocContentType() != null ? jobPosting.getDocContentType()
						: MediaType.APPLICATION_OCTET_STREAM_VALUE;
				headers.setContentType(MediaType.parseMediaType(contentType));

				return new ResponseEntity<>(jobPosting.getDoc(), headers, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not downloaded. please try again");
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(errorMessage);
		}
		
	}

}
