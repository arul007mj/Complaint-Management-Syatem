package com.arul.complaint_management.dtos;

import com.arul.complaint_management.enums.Role;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class UserRequest {

	@NotBlank(message = "Name Is Required")
	private String name;
	
	@Email(message = "Invalid Email")
	@NotBlank(message = "Email is Required")
	private String email;
	
	@NotBlank(message = "Password is Required")
	@Size(min = 6,message = "Password Must Be At Least 6 Characters")
	private String password;
		
	@NotNull(message = "Role is Required")
	private Role role;

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	
	
}
