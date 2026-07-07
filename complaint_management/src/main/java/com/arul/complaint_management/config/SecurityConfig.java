package com.arul.complaint_management.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.arul.complaint_management.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {
	
	@Autowired 
	private JwtAuthenticationFilter jwtAuthenticationFilter; 
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)throws Exception  {
		return configuration.getAuthenticationManager();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.csrf(csrf->csrf.disable()).sessionManagement(session -> 
		session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		
		.authorizeHttpRequests(auth->auth
				.requestMatchers("/Auth/**").permitAll()
				.requestMatchers("/arul/Users/UpdateUser").hasAnyRole("ADMIN","MANAGER")
				.requestMatchers("/arul/Users/DeleteUser").hasRole("ADMIN")
				.requestMatchers("/arul/Users/GetAll").hasAnyRole("ADMIN","TEAM_LEAD","MANAGER")
				.requestMatchers("/Complaint/Save").hasRole("CUSTOMER")
				.requestMatchers("/Complaint/UpdateComplaint").hasAnyRole("TEAM_LEAD","MANAGER")
				.requestMatchers("/Complaint/AllComplaints").hasAnyRole("ADMIN","TEAM_LEAD","MANAGER")
				.requestMatchers("/Complaint/DeleteComplaint").hasRole("ADMIN")
				.anyRequest().authenticated())
		
		.addFilterBefore(jwtAuthenticationFilter,UsernamePasswordAuthenticationFilter.class);
		
		
		
		 return http.build();
	}
}
