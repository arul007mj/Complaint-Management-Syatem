package com.arul.complaint_management.service;

import com.arul.complaint_management.dtos.LoginRequest;
import com.arul.complaint_management.dtos.LoginResponse;

public interface AuthService {

	LoginResponse login(LoginRequest request); 
}
