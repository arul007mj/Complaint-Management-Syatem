package com.arul.complaint_management.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.arul.complaint_management.Exception.ResourceNotFoundException;
import com.arul.complaint_management.Repository.UserRepository;
import com.arul.complaint_management.dtos.LoginRequest;
import com.arul.complaint_management.dtos.LoginResponse;
import com.arul.complaint_management.entity.User;
import com.arul.complaint_management.security.JwtUtil;
import com.arul.complaint_management.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private PasswordEncoder encoder;

	@Override
	public LoginResponse login(LoginRequest request) {
		// TODO Auto-generated method stub
		
		User u=userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new 
				RuntimeException("Invaild Email And Password"));
				
		if (! encoder.matches(request.getPassword(), u.getPassword())) {
			throw new RuntimeException("Invaild Email And Password");
		}
		
		String token=jwtUtil.generateToken(u.getEmail());
		
		return new LoginResponse(token);
	}
}
