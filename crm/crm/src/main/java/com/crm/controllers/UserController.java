package com.crm.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.exceptions.ResourceNotFoundException;
import com.crm.models.User;
import com.crm.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// Create User
	@PostMapping("/create")
	@PreAuthorize("hasAuthority('SUPER_ADMIN')")
	public ResponseEntity<?> createUser(@Valid @RequestBody User user) {

		try {

			User existingUser = userService.findByUsername(user.getUsername());

			if (existingUser != null) {
				return ResponseEntity.badRequest()
						.body("User with this email " + existingUser.getUsername() + " already exist.");
			}

			User authenticateUser = userService.getAuthenticateUser();
			user.setLocation(user.getLocation().replaceAll("\\s+", "").toLowerCase());
			user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password
			user.setCreated(authenticateUser);
			user.setModified(authenticateUser);
			user.setCreatedDate(LocalDate.now());
			user.setModifiedDate(LocalDate.now());
			user.setStatus(1);
			boolean isSaved = userService.saveUser(user);
			if (isSaved) {
				return ResponseEntity.ok("User saved success");
			} else {
				return ResponseEntity.badRequest().body("User not saved please try again");
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Server down! please try after some time");
		}
	}

	@PutMapping("/update/{id}")
	@PreAuthorize("hasAnyAuthority('SUPER_ADMIN','COMPANY','FRANCHISOR')")
	public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody User updatedUser) {
		try {
			User user = userService.getUserById(id);
			if (user == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with this id " + id);
			}
			User authenticateUser = userService.getAuthenticateUser();

			if (!user.getId().equals(authenticateUser.getId())) {
				return ResponseEntity.badRequest().body("You are trying to access another resource");
			}

			user.setName(updatedUser.getName());
			user.setUsername(updatedUser.getUsername());
			user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
			user.setCompanyName(updatedUser.getCompanyName());
			user.setModified(authenticateUser);
			user.setModifiedDate(LocalDate.now());
			boolean isSaved = userService.saveUser(user);
			if (isSaved) {
				return ResponseEntity.ok("user update success");
			} else {
				return ResponseEntity.badRequest().body("User not updated please try again.");
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Server down! please try after some time");
		}
	}

	// Get All Users
	@GetMapping("/getAll")
	@PreAuthorize("hasAuthority('SUPER_ADMIN')")
	public ResponseEntity<?> getAllUsers() {
		try {
			List<User> users = userService.getAllUsers();
			if (users.isEmpty()) {
				return ResponseEntity.ok("Users not found");
			}
			return ResponseEntity.ok(users);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Server down! please try after some time");
		}
	}

	// Get User By ID
	@GetMapping("/get/{id}")
	@PreAuthorize("hasAuthority('SUPER_ADMIN')")
	public ResponseEntity<?> getUserById(@PathVariable Long id) {
		try {
			User user = userService.getUserById(id);
			if (user == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with this id "+id);
			}
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Server down! please try after some time");
		}
		
	}

	// Delete User
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasAnyAuthority('SUPER_ADMIN','COMPANY','FRANCHISOR')")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {

		try {
			User user = userService.getUserById(id);
			User loginUser = userService.getAuthenticateUser();
			if (!user.getId().equals(loginUser.getId())) {
				throw new ResourceNotFoundException("You are trying to access another resource");
			}
			user.setStatus(0);
			user.setModified(loginUser);
			user.setModifiedDate(LocalDate.now());
			userService.saveUser(user);
			return ResponseEntity.ok("User deleted successfully");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Server down! please try after some time");
		}
	}

}
