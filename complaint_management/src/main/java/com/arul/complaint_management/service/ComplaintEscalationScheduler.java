package com.arul.complaint_management.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.arul.complaint_management.Repository.ComplaintHistoryRepository;
import com.arul.complaint_management.Repository.ComplaintRepository;
import com.arul.complaint_management.entity.Complaint;
import com.arul.complaint_management.entity.ComplaintHistory;
import com.arul.complaint_management.enums.ComplaintStatus;
import com.arul.complaint_management.enums.Priority;
import com.arul.complaint_management.enums.Role;

@Service
public class ComplaintEscalationScheduler {
	
	@Autowired
	private ComplaintRepository complaintRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private ComplaintHistoryRepository complaintHistoryRepository;
	
	
	//this cron Expression makes the job run automatically every hour 
	@Scheduled(cron = "0 * * * * *")
	public void escalateUnresolvedComplaints() {
		
		//1.calculate time threshold (24 hours ago)
		LocalDateTime now=LocalDateTime.now().minusHours(24);
		
		//2.fetch all complaints open older than 24 hours
		List<Complaint> complaints=complaintRepository.findBystatusNot(ComplaintStatus.RESOLVED);
		
		Logger logger=LoggerFactory.getLogger(ComplaintService.class);
		
		//3.Upgrade Assignments Systematically
		for (Complaint complaint : complaints) {
			long hours=Duration.between(complaint.getCreatedDate(), now).toHours(); 
			Role currentRole=complaint.getAssignedToRole();
			
				if ((complaint.getStatus().equals(ComplaintStatus.OPEN)) || (complaint.getStatus().equals(ComplaintStatus.IN_PROGRESS))) {
					if (hours >= 72 && currentRole != Role.ADMIN) {
						complaint.setAssignedToRole(Role.ADMIN);
						complaint.setStatus(ComplaintStatus.ESCALATED);
						complaint.setPriority(Priority.CRITICAL);
						Complaint c=complaintRepository.save(complaint);
						saveHistory(complaint, "Complaint Escalated To Admin");
						logger.warn("Complaint {}"+c.getComplaintid()+" ESCALATED To {} "+c.getAssignedToRole());
						
						
					}else if (hours >=48 && currentRole == Role.TEAM_LEAD) {
						complaint.setAssignedToRole(Role.MANAGER);
						complaint.setStatus(ComplaintStatus.ESCALATED);
						complaint.setPriority(Priority.HIGH);
						Complaint c=complaintRepository.save(complaint);
						saveHistory(complaint, "Complaint Escalated To MANAGR");
						logger.warn("Complaint {}"+c.getComplaintid()+" ESCALATED To {} "+c.getAssignedToRole());
					}else if (hours >=24 ) {
						
						complaint.setAssignedToRole(Role.TEAM_LEAD);
						complaint.setStatus(ComplaintStatus.ESCALATED );
						complaint.setPriority(Priority.MEDIUM);
						Complaint c=complaintRepository.save(complaint);
						saveHistory(complaint, "Complaint Escalated To TEAM_LEAD");
						logger.warn("Complaint {}"+c.getComplaintid()+" ESCALATED To {} "+c.getAssignedToRole());
					}
					if (complaint.getStatus().equals(ComplaintStatus.ESCALATED)) {
						
						emailService.sendEmail(
						        complaint.getUser().getEmail(),
						        "Complaint  Move to ESCALATION Stage",
						        "Hello " + complaint.getUser().getName()
						                + ",\n\n"
						                + "Your complaint has Been Escalated to our "+complaint.getAssignedToRole() +"for Further investication .\n"
						                + "Complaint ID : " + complaint.getComplaintid()
						                + "\nStatus : " + complaint.getStatus()
						);
					}
				}
				
				 
			}
		}
	
		public void saveHistory(Complaint complaint,String remmark) {
			
			ComplaintHistory history=new ComplaintHistory();
			history.setComplaint(complaint);
			history.setId(complaint.getComplaintid());
			history.setNewStatus(complaint.getStatus());
			history.setOldStatus(ComplaintStatus.OPEN);
			history.setRemarks(remmark);
			history.setUpdatedAt(LocalDateTime.now());
			history.setUpdatedBy(complaint.getUpdatedby());
			
			complaintHistoryRepository.save(history);
		}
		
	}
	

