package com.arul.complaint_management.service;

import org.springframework.http.ResponseEntity;

import com.arul.complaint_management.dtos.DashboardResponse;

public interface Dashboardservice {
	
	ResponseEntity<DashboardResponse> getDashboard();
}
