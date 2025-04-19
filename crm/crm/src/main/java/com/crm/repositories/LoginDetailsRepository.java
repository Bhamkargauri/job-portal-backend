package com.crm.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crm.models.LoginDetails;

public interface LoginDetailsRepository extends JpaRepository<LoginDetails, Long>{
	
	Optional<LoginDetails> findByUsername(String username);
	
	Optional<LoginDetails> findByToken(String token);

}
