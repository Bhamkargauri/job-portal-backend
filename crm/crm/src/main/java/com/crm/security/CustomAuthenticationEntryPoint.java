package com.crm.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		
		String tokenError = (String) request.getAttribute("tokenError");
		String logout = (String) request.getAttribute("logout");
		
		Throwable cause = authException.getCause();	
		response.setContentType("application/json");
		
		try {
			
	        if ("Invalid Token".equals(tokenError)) {
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            response.setContentType("application/json");
	            response.getWriter().write("{\"error\": \"Invalid Token\"}");
	        }else if ("Invalid Token".equals(logout)) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType("application/json");
	            response.getWriter().write("{\"error\": \"please log in medico healthare, token is expired.\"}");
			}else if (cause instanceof org.springframework.security.core.userdetails.UsernameNotFoundException) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	            response.getWriter().write("{\"error\": \"User not found\"}");
			}
	        else {
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            response.setContentType("application/json");
	            response.getWriter().write("{\"error\": \""+authException.getMessage()+"\"}");
	        }
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
		 		
	}
}
