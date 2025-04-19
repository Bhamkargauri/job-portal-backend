package com.crm.services;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crm.models.Candidate;
import com.crm.models.JobPosting;
import com.crm.models.User;
import com.crm.repositories.CandidateRepository;

@Service
public class CandidateService {

	@Autowired
	private CandidateRepository candidateRepository;

	@Autowired
	private UserService userService;

	public boolean saveCandidate(Candidate candidate) {
		try {
			candidateRepository.save(candidate);
			return true;
		} catch (Exception e) {
			return false;
		}
		
	}

	public List<Candidate> getAllCandidates() {
		try {
			return candidateRepository.findAll();
		} catch (Exception e) {
			
			return null;
		}
		
	}

	public Candidate getCandidateById(Long id) {
		try {
			Optional<Candidate> candidate = candidateRepository.findById(id);
			return candidate.orElse(null);
		} catch (Exception e) {
			return null;
		}
	}

	public List<Candidate> getCandidatesByJobPost(String jobPost) {
		try {
			return candidateRepository.findByJobPost(jobPost);
		} catch (Exception e) {
			return null;
		}
		
	}

	// Get Placed Candidates (Status = 5)
	public List<Candidate> getPlacedCandidates() {
		try {
			return candidateRepository.findByPlacementStatus(5);
		} catch (Exception e) {
			return null;
		}
	}

	// Get Unplaced Candidates (Status = 0 or 6)
	public List<Candidate> getUnplacedCandidates() {
		try {
			return candidateRepository.findByPlacementStatusIn(Arrays.asList(0, 6));
		} catch (Exception e) {
			return null;
		}
	}

	// Get In-Process Candidates (Status = 1, 2, 3, 4)
	public List<Candidate> getInProcessCandidates() {
		try {
			return candidateRepository.findByPlacementStatusIn(Arrays.asList(1, 2, 3, 4));
		} catch (Exception e) {
			return null;
		}
	}

	public List<Candidate> findCandidatesByJobPostingAndUser(String jobPost, User created) {
		try {
			return candidateRepository.findByJobPostAndCreated(jobPost, created);
		} catch (Exception e) {
			return null;
		}
	}

}
