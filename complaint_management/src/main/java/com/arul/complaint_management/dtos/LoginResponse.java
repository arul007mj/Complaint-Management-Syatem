package com.arul.complaint_management.dtos;

public class LoginResponse {

	private String token;

	public LoginResponse(String token) {
		super();
		this.token = token;
	}
	
	public LoginResponse() {
		
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	
}
