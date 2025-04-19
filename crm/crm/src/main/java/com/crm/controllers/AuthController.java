package com.crm.controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.AuthRequest;
import com.crm.models.LoginDetails;
import com.crm.models.LoginResponse;
import com.crm.models.User;
import com.crm.services.JwtService;
import com.crm.services.LoginDetailService;
import com.crm.services.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private UserService userService;
	
    @Autowired
    private JwtService jwtService;
	
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private LoginDetailService loginDetailService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
    	 try {
         	
        	 // Retrieve the user by username
            User user = userService.findByUsername(authRequest.getUsername());
            
            if (user == null) {
                return ResponseEntity.badRequest().body("Invalid username or password!");
            }
        
            // Check if the user is not active
            if (user.getStatus()==0) {
            	return ResponseEntity.badRequest().body("User is not active. Contact Super Admin.");

			}
        
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            if (authentication.isAuthenticated()) {
            	
            	LoginDetails preLoginDetails = loginDetailService.findByUsername(user.getUsername()); 
            	LoginResponse response = new LoginResponse();
            	if (preLoginDetails == null) {
                	String token = jwtService.generateToken(user.getUsername());
                	LoginDetails loginDetails = new LoginDetails();
					loginDetails.setToken(token);
					loginDetails.setCreated(user);
					loginDetails.setCreatedDate(LocalDate.now());
					loginDetails.setUsername(user.getUsername());
					boolean isSaved = loginDetailService.addLoginDetails(loginDetails);
					if (isSaved) {
						response.setId(user.getId());
						response.setToken(token);
						response.setType(user.getType());
						return ResponseEntity.ok(response);
					}else {
						return ResponseEntity.badRequest().body("Something went wrong please try again");
					}		
				}else {
					response.setId(user.getId());
	            	response.setType(user.getType());
	            	response.setToken(preLoginDetails.getToken());
	       return ResponseEntity.ok(response);
				}
            
            }
//            return null;
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed. Please check your credentials.");
        }catch (BadCredentialsException exception){
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password!");
        }catch (Exception e) {
			return ResponseEntity.badRequest().body("Server Down please try again.");
		}
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token){
    	try {
    		if (token.startsWith("Bearer ")) {
    			token = token.substring(7);
    		}else {
    		return ResponseEntity.badRequest().body("Something went wrong. ");
    		}
        	String username = jwtService.extractUsername(token);
        	LoginDetails loginDetails = loginDetailService.findByUsername(username);
        	boolean isDeleted = loginDetailService.deleteLoginDetails(loginDetails);
        	if (isDeleted) {
            	return ResponseEntity.ok("User Logged out successfully");
			}else {
				return ResponseEntity.badRequest().body("Something went wrong please try again!");
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Server Down. please try after some time");
		}
    	
    }
	
	
}
