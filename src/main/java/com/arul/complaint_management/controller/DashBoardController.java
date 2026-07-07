package com.arul.complaint_management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arul.complaint_management.dtos.DashboardResponse;
import com.arul.complaint_management.service.Dashboardservice;

@RestController
public class DashBoardController {

	@Autowired
	private	Dashboardservice dashboardservice;
	
	@GetMapping("/DashBoard")
	public ResponseEntity<DashboardResponse> getDashBoard() {
		
		return dashboardservice.getDashboard();
	}
}
