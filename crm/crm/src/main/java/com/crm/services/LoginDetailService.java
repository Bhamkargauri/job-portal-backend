package com.crm.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.models.LoginDetails;
import com.crm.repositories.LoginDetailsRepository;

@Service
public class LoginDetailService {
	
	@Autowired
	private LoginDetailsRepository loginDetailsRepository;
	
	
	public boolean addLoginDetails(LoginDetails loginDetails) {
		try {
			loginDetailsRepository.save(loginDetails);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public LoginDetails findByUsername(String username) {
		try {
			Optional<LoginDetails> loginDetails = loginDetailsRepository.findByUsername(username);
			return loginDetails.orElse(null);
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean deleteLoginDetails(LoginDetails loginDetails) {
		try {
			loginDetailsRepository.delete(loginDetails);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	

}
