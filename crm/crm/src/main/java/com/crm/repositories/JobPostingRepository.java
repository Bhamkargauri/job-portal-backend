package com.crm.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.crm.models.JobPosting;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    List<JobPosting> findByJobType(String jobType);
    
    
    @Query("SELECT j FROM JobPosting j WHERE j.location = :location AND j.deadline >= :currentDate")
    List<JobPosting> findByLocation(String location, LocalDate currentDate);
    

    @Query("SELECT j FROM JobPosting j WHERE j.deadline >= :currentDate")
    List<JobPosting> findActiveJobs(LocalDate currentDate);
    
    //Fetch jobs where the deadline has passed
    @Query("SELECT j FROM JobPosting j WHERE j.deadline < :currentDate")
    List<JobPosting> findExpiredJobs(LocalDate currentDate);

    
}
