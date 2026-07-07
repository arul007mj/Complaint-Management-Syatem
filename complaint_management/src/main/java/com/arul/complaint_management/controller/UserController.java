package com.arul.complaint_management.controller;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arul.complaint_management.dtos.UserRequest;
import com.arul.complaint_management.dtos.UserResponse;
import com.arul.complaint_management.entity.User;
import com.arul.complaint_management.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/arul/Users")
@Validated
public class UserController {

	@Autowired
	private UserService userService;
	
	//http://localhost:8080/arul/Users/Save
	@PostMapping("/Save")
	public ResponseEntity<UserResponse> saveUser(@Valid @RequestBody UserRequest request ) {
		return userService.Save_User(request);
	}
	
	//http://localhost:8080/arul/Users/Getall
	@GetMapping("/Getall")
	public ResponseEntity<List<UserResponse>> FetchAll() {
		return userService.FetchAllUsers();
	}
	
	//http://localhost:8080/arul/Users/UpdateUser
	@PutMapping("/UpdateUser")
	public ResponseEntity<LinkedHashMap<String, UserResponse>> UpdateUser(@RequestParam long uid,@RequestBody UserRequest request ) {
	return	userService.UpdateUser(uid, request);
	}
	
	//http://localhost:8080/arul/Users/DeleteUser
	@DeleteMapping("/DeleteUser")
	public ResponseEntity<String> DeleteUser(long uid) {
		return userService.DeleteUser(uid);
	}
	
}
