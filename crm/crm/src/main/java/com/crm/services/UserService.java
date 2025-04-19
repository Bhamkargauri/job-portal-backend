package com.crm.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crm.exceptions.ResourceNotFoundException;
import com.crm.models.User;
import com.crm.repositories.UserRepository;

import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public boolean saveUser(User user) {
		try {
			userRepository.save(user);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public List<User> getAllUsers() {
		try {
			return userRepository.findAll();
		} catch (Exception e) {
			return null;
		}
	}

	public User findByUsername(String username) {
		try {
			Optional<User> user = userRepository.findByUsername(username);
			return user.orElse(null);
		} catch (Exception e) {
			return null;
		}
	}

	public User getUserById(Long id) {
		try {
			Optional<User> user = userRepository.findById(id);
			return user.orElse(null);
		} catch (Exception e) {
			return null;
		}

	}

	public User getAuthenticateUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return userRepository.findByUsername(authentication.getName()).get();
	}
}
