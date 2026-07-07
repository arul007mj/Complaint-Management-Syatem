package com.arul.complaint_management.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

	@Email(message = "Invalid Email...")
	@NotBlank(message = "Email is Required...")
	private String email;
	
	@NotBlank(message = "password is Required...")
	private String password;
	
	

	public LoginRequest(@Email(message = "Invalid Email...") @NotBlank(message = "Email is Required...") String email,
			@NotBlank(message = "password is Required...") String password) {
		super();
		this.email = email;
		this.password = password;
	}
	
	public LoginRequest() {
		
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	} 
	
	
}
