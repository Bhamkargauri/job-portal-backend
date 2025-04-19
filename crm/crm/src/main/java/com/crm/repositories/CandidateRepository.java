package com.crm.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crm.models.Candidate;
import com.crm.models.User;


public interface CandidateRepository extends JpaRepository<Candidate, Long> {
	
	Optional<Candidate> findByEmail(String email);
	
	List<Candidate> findByJobPost(String jobPost);
	

	List<Candidate> findByPlacementStatus(Integer placementStatus);

    List<Candidate> findByPlacementStatusIn(List<Integer> placementStatuses);

    List<Candidate> findByJobPostAndCreated(String jobPost, User created);
    
}
