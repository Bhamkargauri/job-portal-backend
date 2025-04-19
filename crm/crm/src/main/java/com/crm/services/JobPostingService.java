package com.crm.services;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crm.models.JobPosting;
import com.crm.repositories.JobPostingRepository;

@Service
public class JobPostingService {

    @Autowired
    private JobPostingRepository jobRepository;


    // Method to save a job
    public boolean saveJob(JobPosting jobPosting){
    	try {
    		 jobRepository.save(jobPosting);
    		  return true;
		} catch (Exception e) {
			return false;
		}
    }

    public List<JobPosting> getAllJobs() {
    	try {
            return jobRepository.findAll();
		} catch (Exception e) {
			return null;
		}
    }

    public JobPosting getJobById(Long id) {
    	try {
    		 Optional<JobPosting> jobPosting = jobRepository.findById(id);
    		 return jobPosting.orElse(null);
		} catch (Exception e) {
			return null;
		}
    }

    public List<JobPosting> getJobsByJobType(String jobType) {
    	try {
    		return jobRepository.findByJobType(jobType);
		} catch (Exception e) {
			return null;
		}  
    }
    
    public List<JobPosting> getJobsByLocation(String location){
    	try {
        	return jobRepository.findByLocation(location , LocalDate.now());
		} catch (Exception e) {
			return null;
		}
    }
    

    // Get jobs where the deadline has not passed
    public List<JobPosting> getActiveJobs() {
    	try {
            return jobRepository.findActiveJobs(LocalDate.now());
		} catch (Exception e) {
			return null;
		}
    }
    
    //Get jobs where the deadline has passed
    public List<JobPosting> getExpiredJobs() {
    	try {
            return jobRepository.findExpiredJobs(LocalDate.now());
		} catch (Exception e) {
			return null;
		}
    }
}

