package com.arul.complaint_management.dtos;

import com.arul.complaint_management.enums.ComplaintStatus;
import com.arul.complaint_management.enums.Priority;

public class ComplaintResponse {

	private long userid;
	private String title;
	private String description;
	private ComplaintStatus status;
	private Priority  priority;
	private long complaintid;
	
	
	public long getComplaintid() {
		return complaintid;
	}
	
	public void setComplaintid(long complaintid) {
		this.complaintid = complaintid;
	}

	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
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
	
	
}
