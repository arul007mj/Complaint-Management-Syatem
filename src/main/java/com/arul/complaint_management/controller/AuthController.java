package com.arul.complaint_management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arul.complaint_management.dtos.LoginRequest;
import com.arul.complaint_management.dtos.LoginResponse;
import com.arul.complaint_management.service.AuthService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/Auth")
@Validated
public class AuthController {

	@Autowired
	private AuthService authService;
	
	//http://localhost:8080/Auth/Login
	@PostMapping("/Login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		
		return new ResponseEntity<LoginResponse>(authService.login(request), HttpStatus.OK); 
	}
}
