package com.arul.complaint_management.serviceImp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.arul.complaint_management.Exception.ResourceNotFoundException;
import com.arul.complaint_management.Repository.ComplaintRepository;
import com.arul.complaint_management.Repository.UserRepository;
import com.arul.complaint_management.dtos.UserRequest;
import com.arul.complaint_management.dtos.UserResponse;
import com.arul.complaint_management.entity.User;
import com.arul.complaint_management.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ComplaintRepository complaintRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public ResponseEntity<UserResponse> Save_User(UserRequest request) {
		// TODO Auto-generated method stub
		
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new DataIntegrityViolationException("EMAIL IS ALREDY EXISTS...");
		}
		User u=new User();
		u.setName(request.getName());
		u.setEmail(request.getEmail());
		u.setRole(request.getRole());
		u.setPassword(passwordEncoder.encode(request.getPassword()));
		
		//save into database
		User saveduser=userRepository.save(u);
		//send it in response 
		System.out.println("request role -> "+request.getRole());
		System.out.println("request role -> "+request.getEmail());
		System.out.println("request role -> "+request.getPassword());
		System.out.println("request role -> "+request.getName());
		System.out.println("____________________------_____-_----___________________");
		System.out.println("db responce role -> "+saveduser.getRole());
		System.out.println("db responce role -> "+saveduser.getEmail());
		System.out.println("db responce role -> "+saveduser.getPassword());
		System.out.println("db responce role -> "+saveduser.getName());
		UserResponse response=new UserResponse();
		response.setUserid(saveduser.getId());
		response.setName(saveduser.getName());
		response.setEmail(saveduser.getEmail());
		response.setRole(saveduser.getRole());
		return new ResponseEntity<UserResponse>(response, HttpStatus.CREATED);
	}
	
	@Override
	public ResponseEntity<List<UserResponse>> FetchAllUsers() {
		// TODO Auto-generated method stub
		List<User> li=userRepository.findAll();
		List<UserResponse> responses=new ArrayList<UserResponse>();
		for (User u : li) {
			UserResponse ur=new UserResponse();
			ur.setEmail(u.getEmail());
			ur.setName(u.getName());
			ur.setUserid(u.getId());
			ur.setRole(u.getRole());
			responses.add(ur);
		}
		return  new ResponseEntity<List<UserResponse>>(responses, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<LinkedHashMap<String, UserResponse>> UpdateUser(long id, UserRequest request) {
		// TODO Auto-generated method stub
		User u=userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User", "User_id", Long.toString(id)));
		u.setName(request.getName());
		u.setEmail(request.getEmail());
		u.setRole(request.getRole());
		u.setPassword(passwordEncoder.encode(request.getPassword()));
		
		User saveduser=userRepository.save(u);
		UserResponse response=new UserResponse();
		response.setEmail(saveduser.getEmail());
		response.setName(saveduser.getName());
		response.setRole(saveduser.getRole());
		response.setUserid(saveduser.getId());
		LinkedHashMap<String, UserResponse> map=new LinkedHashMap<String, UserResponse>();
		map.put("UserData Updated Successfully.....", response);
		return new ResponseEntity<LinkedHashMap<String,UserResponse>>(map, HttpStatus.OK);
	}
	
	
	@Override
	public ResponseEntity<String> DeleteUser(long uid) {
		// TODO Auto-generated method stub
		User u=userRepository.findById(uid).orElseThrow(()->new ResourceNotFoundException("User", "User_Id", Long.toString(uid)));
		complaintRepository.deleteAll(u.getComplaints());
		userRepository.delete(u);
		
		return new ResponseEntity<String>("User Deleted Successfully..", HttpStatus.OK);
	}
}
