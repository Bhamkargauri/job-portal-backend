package com.crm.models;

import lombok.Data;

@Data
public class LoginResponse {
	private Long id;
	private String type;
	private String token;
}
