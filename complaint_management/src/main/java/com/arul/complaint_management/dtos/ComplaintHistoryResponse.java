package com.arul.complaint_management.dtos;

import java.time.LocalDateTime;

import com.arul.complaint_management.enums.ComplaintStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;


public class ComplaintHistoryResponse {
	
		private Long id;

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
	    
	    

}
