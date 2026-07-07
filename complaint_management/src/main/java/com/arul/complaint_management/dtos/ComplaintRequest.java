package com.arul.complaint_management.dtos;

import com.arul.complaint_management.enums.Priority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ComplaintRequest {

	@NotBlank(message = "Title is Required ")
	private String title;
	
	@NotBlank(message = "Description is Required ")
	private String description;
	
	private Priority priority;
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	
	
	
	
}
