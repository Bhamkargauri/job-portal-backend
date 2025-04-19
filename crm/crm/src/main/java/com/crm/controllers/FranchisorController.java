package com.crm.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.exceptions.ResourceNotFoundException;
import com.crm.models.Candidate;
import com.crm.models.JobPosting;
import com.crm.models.User;
import com.crm.services.CandidateService;
import com.crm.services.JobPostingService;
import com.crm.services.UserService;

@RestController
@RequestMapping("/franchisor")
public class FranchisorController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JobPostingService jobPostingService;
	
	@Autowired
	private CandidateService candidateService;
	
	private final String errorMessage = "Something went wrong. please try again!";
	
    @GetMapping("/jobPosts")
    @PreAuthorize("hasAuthority('FRANCHISOR')")
    public ResponseEntity<?> getJopPostingByFranchisorLocation(){
    	try {
    	   	User user = userService.getAuthenticateUser();
    	   	List<JobPosting> jobPostings = jobPostingService.getJobsByLocation(user.getLocation().replaceAll("\\s+", "").toLowerCase());
        	if (jobPostings.isEmpty()) {
				return ResponseEntity.ok("No Job post found in your location");
			}
    	   	return ResponseEntity.ok(jobPostings);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(errorMessage);
		}
 
    }
    
    @GetMapping("/matchCandidate/{jobPostingId}")
    @PreAuthorize("hasAuthority('FRANCHISOR')")
    public ResponseEntity<?> getCandidatesByJobPostingAndUser(@PathVariable Long jobPostingId){
    	try {
    		JobPosting jobPosting = jobPostingService.getJobById(jobPostingId);
        	
        	if (jobPosting == null) {
    		 return ResponseEntity.badRequest().body("job post not foud with this id "+jobPostingId);
    		}
        	
        	User user = userService.getAuthenticateUser();
        	List<Candidate> candidates = candidateService.findCandidatesByJobPostingAndUser(jobPosting.getJobTitle(), user);
        	if (candidates.isEmpty()) {
				return ResponseEntity.ok("no candidate match for this job post");
			}
        	return ResponseEntity.ok(candidates);
        
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(errorMessage);
		}
    	
    	}

}
