package com.arul.complaint_management.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arul.complaint_management.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	//save(),findById(),findAll(),Delete() -->built-in methods

	public Optional<User> findByEmail(String email);
	
	public boolean existsByEmail(String email);
		
	
}
