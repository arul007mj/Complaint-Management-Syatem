package com.arul.complaint_management.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.arul.complaint_management.Repository.ComplaintRepository;
import com.arul.complaint_management.dtos.DashboardResponse;
import com.arul.complaint_management.enums.ComplaintStatus;
import com.arul.complaint_management.enums.Priority;
import com.arul.complaint_management.service.Dashboardservice;

@Service
public class DashBoardServiceImpl implements Dashboardservice{

	@Autowired
	ComplaintRepository complaintRepository;
	
	@Override
	public ResponseEntity<DashboardResponse> getDashboard() {
		// TODO Auto-generated method stub
		
		DashboardResponse Response=new DashboardResponse();
		
		Response.setTotalComplaints(complaintRepository.count());
		
		Response.setOpenComplaints(complaintRepository.countBystatus(ComplaintStatus.OPEN));
		Response.setClosedComplaints(complaintRepository.countBystatus(ComplaintStatus.CLOSED));
		Response.setResolvedComplaints(complaintRepository.countBystatus(ComplaintStatus.RESOLVED));
		Response.setInProgressComplaints(complaintRepository.countBystatus(ComplaintStatus.IN_PROGRESS));
		Response.setEscalatedComplaints(complaintRepository.countBystatus(ComplaintStatus.ESCALATED));
		
		Response.setLowPriority(complaintRepository.countBypriority(Priority.LOW));
		Response.setMediumPriority(complaintRepository.countBypriority(Priority.MEDIUM));
		Response.setHighPriority(complaintRepository.countBypriority(Priority.HIGH));
		Response.setCriticalPriority(complaintRepository.countBypriority(Priority.CRITICAL));
		
		return new ResponseEntity<DashboardResponse>(Response, HttpStatus.OK);
	}
}
