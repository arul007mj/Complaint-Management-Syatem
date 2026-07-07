package com.arul.complaint_management.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.arul.complaint_management.dtos.ComplaintHistoryResponse;
import com.arul.complaint_management.dtos.ComplaintRequest;
import com.arul.complaint_management.dtos.ComplaintResponse;
import com.arul.complaint_management.entity.Complaint;
import com.arul.complaint_management.enums.ComplaintStatus;
import com.arul.complaint_management.enums.Priority;

public interface ComplaintService {
	
		ResponseEntity<List<ComplaintResponse>> getAllComplaint(int page,int size,String sortby,String direction);
	 
		ResponseEntity<ComplaintResponse> getComplaintById(Long id);
		
		ResponseEntity<String> DeleteComplaint(long cid);

		ResponseEntity<Map<String, ComplaintResponse>> UpdateComplaint(long id, ComplaintRequest request,
				ComplaintStatus status);
		
		ResponseEntity<Map<String,ComplaintStatus>> startComplaint(long Complaintid);
		ResponseEntity<Map<String,ComplaintStatus>> resolveComplaint(long Complaintid);
		ResponseEntity<Map<String,ComplaintStatus>> closeComplaint(long Complaintid);

		ResponseEntity<Map<String, ComplaintResponse>> saveComplaint(ComplaintRequest request, String email,
				MultipartFile file);
		
		
		ResponseEntity<Resource> downloadAttachment(Long complaintId) throws IOException;
		
		ResponseEntity<List<ComplaintHistoryResponse>> getComplaintHistory(long complaintId);

		ResponseEntity<List<ComplaintResponse>> findByPriorityAndStatus(Priority priority, ComplaintStatus status);
	
		ResponseEntity<List<ComplaintResponse>> searchComplaints(String keyword);
}
