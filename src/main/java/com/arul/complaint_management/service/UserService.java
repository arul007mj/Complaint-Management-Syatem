package com.arul.complaint_management.service;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.arul.complaint_management.dtos.UserRequest;
import com.arul.complaint_management.dtos.UserResponse;
import com.arul.complaint_management.entity.User;


public interface UserService {

	public ResponseEntity<UserResponse> Save_User(UserRequest request); 
		
	public ResponseEntity<List<UserResponse>> FetchAllUsers(); 
	
	ResponseEntity<LinkedHashMap<String,UserResponse>> UpdateUser(long id,UserRequest request);
	
	ResponseEntity<String> DeleteUser(long uid);
}
