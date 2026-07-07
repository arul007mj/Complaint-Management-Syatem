package com.arul.complaint_management.entity;

import java.time.LocalDateTime;

import com.arul.complaint_management.enums.ComplaintStatus;

import jakarta.persistence.*;

@Entity
public class ComplaintHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus oldStatus;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus newStatus;

    private String updatedBy;

    private LocalDateTime updatedAt;

    private String remarks;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Complaint getComplaint() {
		return complaint;
	}

	public void setComplaint(Complaint complaint) {
		this.complaint = complaint;
	}

	public ComplaintStatus getOldStatus() {
		return oldStatus;
	}

	public void setOldStatus(ComplaintStatus oldStatus) {
		this.oldStatus = oldStatus;
	}

	public ComplaintStatus getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(ComplaintStatus newStatus) {
		this.newStatus = newStatus;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
 
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
    
    // Generate Getters and Setters
   
}