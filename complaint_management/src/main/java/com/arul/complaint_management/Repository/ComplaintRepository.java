package com.arul.complaint_management.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arul.complaint_management.entity.Complaint;
import com.arul.complaint_management.enums.ComplaintStatus;
import com.arul.complaint_management.enums.Priority;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

	//4-Built-in methods  .....
	
	public List<Complaint> findByStatusAndCreatedDateBefore(ComplaintStatus status,LocalDateTime dateTime); 
	
	List<Complaint> findBystatusNot(ComplaintStatus status);
	
	long count();

	long countBystatus(ComplaintStatus status);

	long countBypriority(Priority priority);
	
	List<Complaint> findBypriority(Priority priority);
	
	List<Complaint> findBystatus(ComplaintStatus status);
	
	List<Complaint> findByPriorityAndStatus(Priority priority,ComplaintStatus status);
	
	List<Complaint> findBytitleContainingIgnoreCase(String title);
	
	List<Complaint> findBydescriptionContainingIgnoreCase(String description);
	
	List<Complaint> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title,String description);
  
}
