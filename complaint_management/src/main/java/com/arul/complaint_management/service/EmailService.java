package com.arul.complaint_management.service;

public interface EmailService {

	void sendEmail(String to,
            String subject,
            String body);
}
