package com.arul.complaint_management.entity;

import java.time.LocalDateTime;

import com.arul.complaint_management.enums.ComplaintStatus;
import com.arul.complaint_management.enums.Priority;
import com.arul.complaint_management.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "complaints")
public class Complaint {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long complaintid;
	
	private String title;
	
	private String description;
	
	@Enumerated(EnumType.STRING)
	private ComplaintStatus status;
	
	@Enumerated(EnumType.STRING)
	private Priority priority;
	
	@Enumerated(EnumType.STRING)
	private Role assignedToRole;
	
	private LocalDateTime createdDate;
	
	private LocalDateTime updatedAt;
	
	private String createdby;
	
	private String updatedby;
	
	private String fileName;
	private String fileType;
	private String filePath;
	
	@ManyToOne
	@JoinColumn(name = "User_id")
	private User user;
	

	@PrePersist
	public void prePersist() {
		createdDate=LocalDateTime.now();
		updatedAt=LocalDateTime.now();
		assignedToRole=Role.TEAM_LEAD;
		status=ComplaintStatus.OPEN;
	}
	
	@PreUpdate
	public void preUpdate() {
		updatedAt=LocalDateTime.now();
	}
	
	public Role getAssignedToRole() {
		return assignedToRole;
	}

	public void setAssignedToRole(Role assignedToRole) {
		this.assignedToRole = assignedToRole;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public long getComplaintid() {
		return complaintid;
	}

	public void setComplaintid(long complaintid) {
		this.complaintid = complaintid;
	}

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

	public ComplaintStatus getStatus() {
		return status;
	}

	public void setStatus(ComplaintStatus status) {
		this.status = status;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String getCreatedby() {
		return createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	public String getUpdatedby() {
		return updatedby;
	}

	public void setUpdatedby(String updatedby) {
		this.updatedby = updatedby;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	
	
}
