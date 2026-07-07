package com.arul.complaint_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ComplaintManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComplaintManagementApplication.class, args);
	}

}
