package com.arul.complaint_management.controller;


import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.arul.complaint_management.dtos.ComplaintHistoryResponse;
import com.arul.complaint_management.dtos.ComplaintRequest;
import com.arul.complaint_management.dtos.ComplaintResponse;
import com.arul.complaint_management.enums.ComplaintStatus;
import com.arul.complaint_management.enums.Priority;
import com.arul.complaint_management.service.ComplaintEscalationScheduler;
import com.arul.complaint_management.service.ComplaintService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/Complaint")
@Validated
public class ComplaintController {

	@Autowired 
	private	ComplaintService complaintService;
	
	@Autowired
	private ComplaintEscalationScheduler escalationScheduler;
	
	
	//http://localhost:8080/Complaint/TriggerEscalation
	@GetMapping("/TriggerEscalation")
	@PreAuthorize("hasRole('ADMIN')") //Only ADMIN to trigger this manully
	public ResponseEntity<String> triggerEscalationManully() {
		
		//Force-run the background Escalation Verification logic
		escalationScheduler.escalateUnresolvedComplaints();
		
		return new ResponseEntity<String>("Escalation Process Executed Successfully.... \n \t\t check  Eclipse  console", HttpStatus.OK);
		
	}
	
	
	//http://localhost:8080/Complaint/Save
	@PostMapping(value = "/Save",consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, ComplaintResponse>> SaveComplaint(@Valid @ModelAttribute 
			ComplaintRequest complaint,@RequestParam("file") MultipartFile file ) {
		
		String logedinuserEmail=SecurityContextHolder.getContext().getAuthentication().getName();
		
		return complaintService.saveComplaint(complaint,logedinuserEmail,file);
	}
	//http://localhost:8080/Complaint/AllComplaints
	@GetMapping("/AllComplaints")
	public ResponseEntity<List<ComplaintResponse>> getAllComplaint(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size,
			@RequestParam(defaultValue = "complaintid") String sortby,
			@RequestParam(defaultValue = "asc") String direction ) 
	{
		return complaintService.getAllComplaint( page, size, sortby, direction);
	}
	//http://localhost:8080/Complaint/GetComplaintById
	@GetMapping("/GetComplaintById")
	public ResponseEntity<ComplaintResponse > getComplaintById( @RequestParam Long id) {
		return complaintService.getComplaintById(id);
	}
	
	//http://localhost:8080/Complaint/DeleteComplaint
	@DeleteMapping("/DeleteComplaint")
	public ResponseEntity<String> DeleteComplaint(@RequestParam long cid) {
		return complaintService.DeleteComplaint(cid);
	}
	//http://localhost:8080/Complaint/UpdateComplaint
	@PutMapping("/UpdateComplaint")
	public ResponseEntity<Map<String, ComplaintResponse>> UpdateComplaint(@RequestParam long id, @RequestBody ComplaintRequest request ,
			@RequestParam ComplaintStatus status) {
		return complaintService.UpdateComplaint(id, request, status);
	}
	//http://localhost:8080/Complaint/Start
	@PutMapping("/Start/{Complaintid}")
	@PreAuthorize("hasAnyRole('TEAM_LEAD','MANAGER')")
	public ResponseEntity<Map<String,ComplaintStatus>> startComplaint(@PathVariable long Complaintid){
		return complaintService.startComplaint(Complaintid);
	}
	//http://localhost:8080/Complaint/Resolve
	@PutMapping("/Resolve/{Complaintid}")
	@PreAuthorize("hasAnyRole('TEAM_LEAD','MANAGER')")
	public ResponseEntity<Map<String,ComplaintStatus>> resolveComplaint(@PathVariable long Complaintid){
		return complaintService.resolveComplaint(Complaintid);
	}
	//http://localhost:8080/Complaint/Close
	@PutMapping("/Close/{Complaintid}")
	@PreAuthorize("hasAnyRole('CUSTOMER','MANAGER')")
	public ResponseEntity<Map<String,ComplaintStatus>> closeComplaint(@PathVariable long Complaintid){
		return complaintService.closeComplaint(Complaintid);
	}
	
	//http://localhost:8080/Complaint/{complaintId}/attachment
	@GetMapping("/{complaintId}/attachment")
	public ResponseEntity<Resource> downloadAttachment(
	        @PathVariable Long complaintId) throws IOException {

	    return complaintService.downloadAttachment(complaintId);
	}
	//http://localhost:8080/Complaint/{complaintId}/history
	@GetMapping("/{complaintid}/history")
	public ResponseEntity<List<ComplaintHistoryResponse>> getComplaintHistory(
	        @PathVariable Long complaintid) {
						
	    return complaintService.getComplaintHistory(complaintid);
	            
	}
	
	//http://localhost:8080/Complaint/Filter
	@GetMapping("/Filter")
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER','TEAMM_LEAD')")
	public ResponseEntity<List<ComplaintResponse>> findByPriorityAndStatus
	(@RequestParam(required =false) Priority priority, @RequestParam(required = false) ComplaintStatus status ) {
		return complaintService.findByPriorityAndStatus(priority, status);
	}
	//http://localhost:8080/Complaint/Search
	@GetMapping("/Search")
	ResponseEntity<List<ComplaintResponse>> searchComplaints(@RequestParam(defaultValue = "") String keyword){
		return complaintService.searchComplaints(keyword);
	}
}
