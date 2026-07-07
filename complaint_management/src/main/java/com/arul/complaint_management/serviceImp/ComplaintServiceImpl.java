package com.arul.complaint_management.serviceImp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.arul.complaint_management.Exception.ResourceNotFoundException;
import com.arul.complaint_management.Repository.ComplaintHistoryRepository;
import com.arul.complaint_management.Repository.ComplaintRepository;
import com.arul.complaint_management.Repository.UserRepository;
import com.arul.complaint_management.dtos.ComplaintHistoryResponse;
import com.arul.complaint_management.dtos.ComplaintRequest;
import com.arul.complaint_management.dtos.ComplaintResponse;
import com.arul.complaint_management.entity.Complaint;
import com.arul.complaint_management.entity.ComplaintHistory;
import com.arul.complaint_management.entity.User;
import com.arul.complaint_management.enums.ComplaintStatus;
import com.arul.complaint_management.enums.Priority;
import com.arul.complaint_management.enums.Role;
import com.arul.complaint_management.service.ComplaintService;
import com.arul.complaint_management.service.EmailService;
import com.arul.complaint_management.service.FileStorageService;

@Service
public class ComplaintServiceImpl implements ComplaintService {

	@Autowired
	private ComplaintRepository complaintRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private FileStorageService fileStorageService;
	
	@Autowired
	private ComplaintHistoryRepository complaintHistoryRepository;

	private List<ComplaintHistory> histories; 
	
	public static final Logger LOGGER=LoggerFactory.getLogger(ComplaintServiceImpl.class);
	
	
	
	@Override
	public ResponseEntity<Map<String, ComplaintResponse>> saveComplaint
	(ComplaintRequest request,String email ,MultipartFile file) {
		// TODO Auto-generated method stub
		User u=userRepository.findByEmail(email).orElseThrow(()->new 
				ResourceNotFoundException("User", "EMAIL_ID", email));
		String fileName=fileStorageService.uploadFile(file);
		Complaint complaint=new Complaint();
		complaint.setDescription(request.getDescription());
		complaint.setPriority(request.getPriority());
		complaint.setTitle(request.getTitle());
		complaint.setUser(u);
		complaint.setFileName(fileName);
		complaint.setFileType(file.getContentType());
		complaint.setFilePath("uploads/"+fileName);
		String logedinEmail=SecurityContextHolder.getContext().getAuthentication().getName();
		complaint.setCreatedby(logedinEmail);
		u.AddComplaint(complaint);
		Complaint c=complaintRepository.save(complaint);
		
		emailService.sendEmail(
		        u.getEmail(),
		        "Complaint Registered Successfully",
		        "Hello " + u.getName()
		                + ",\n\n"
		                + "Your complaint has been registered successfully.\n"
		                + "Complaint ID : " + complaint.getComplaintid()
		                + "\nStatus : " + complaint.getStatus()
		);
		
		System.out.println("======= -- *** EMAIL SENDIND DONE *** -- =======");	
		LOGGER.info("Creating Complaint Title With :" +complaint.getTitle()+
				" \n\n\t\t Complaint Created Successfully ,Compliant Id :"+complaint.getComplaintid());
		ComplaintResponse response=new ComplaintResponse();
		response.setDescription(c.getDescription());
		response.setPriority(c.getPriority());
		response.setStatus(c.getStatus());
		response.setTitle(c.getTitle());
		response.setUserid(c.getUser().getId());
		response.setComplaintid(c.getComplaintid());
		Map<String, ComplaintResponse> map=new LinkedHashMap<String, ComplaintResponse>();
		map.put("Complaint Raised Successfully ...", response);
		return new ResponseEntity<Map<String,ComplaintResponse>>(map, HttpStatus.CREATED);
	}
	
	@Override
	public ResponseEntity<List<ComplaintResponse>> getAllComplaint(int page,int size,String sortby,String direction) {
		// TODO Auto-generated method stub
		
		Sort sort=direction.equalsIgnoreCase("desc")?Sort.by(sortby).descending()
				:Sort.by(sortby).ascending();
		
		Pageable pageable=PageRequest.of(page, size,sort);
		
		Page<Complaint> complaints=complaintRepository.findAll(pageable);
		
		
		List<ComplaintResponse> listOfComplaints=complaints.getContent().stream().map(complaint ->{
			ComplaintResponse response=new ComplaintResponse();
			response.setDescription(complaint.getDescription()); 
			response.setPriority(complaint.getPriority());
			response.setStatus(complaint.getStatus());
			response.setTitle(complaint.getTitle());
			response.setUserid(complaint.getUser().getId());
			response.setComplaintid(complaint.getComplaintid());
			
			return response;
		}).toList();
			
		
			
		return new ResponseEntity<List<ComplaintResponse>>(listOfComplaints, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<ComplaintResponse> getComplaintById(Long id) {
		// TODO Auto-generated method stub
		
		Complaint c=complaintRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Complaint", "Complaint_Id", Long.toString(id)));
		ComplaintResponse response=new ComplaintResponse();
		response.setDescription(c.getDescription());
		response.setPriority(c.getPriority());
		response.setStatus(c.getStatus());
		response.setTitle(c.getTitle());
		response.setUserid(c.getUser().getId());
		response.setComplaintid(c.getComplaintid()); 
		LOGGER.error("Error While Geting Complaint {}"+c.getComplaintid());
		return new ResponseEntity<ComplaintResponse>(response, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<Map<String, ComplaintResponse>> UpdateComplaint(long id, ComplaintRequest request , ComplaintStatus status) {
		// TODO Auto-generated method stub
		Complaint c=complaintRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Complaint", "Complaint_id", Long.toString(id)));
		Map<String, ComplaintResponse> map=new LinkedHashMap<String, ComplaintResponse>();
		ComplaintResponse response=new ComplaintResponse();
		c.setDescription(request.getDescription());
		c.setPriority(request.getPriority());
		c.setStatus(status);
		c.setTitle(request.getTitle());
		String loggedinemail=SecurityContextHolder.getContext().getAuthentication().getName();
		c.setUpdatedby(loggedinemail);
		Complaint updatedcomplaint=complaintRepository.save(c);
		response.setDescription(updatedcomplaint.getDescription());
		response.setPriority(updatedcomplaint.getPriority());
		response.setStatus(updatedcomplaint.getStatus());
		response.setTitle(updatedcomplaint.getTitle());
		response.setUserid(updatedcomplaint.getUser().getId());
		response.setComplaintid(updatedcomplaint.getComplaintid());
		map.put("Complaint Updated successfully....", response);
		return new ResponseEntity<Map<String,ComplaintResponse>>(map, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<String> DeleteComplaint(long cid) {
		// TODO Auto-generated method stub
		
		Complaint c=complaintRepository.findById(cid).orElseThrow(()-> new ResourceNotFoundException("Complaint", "Complaint_id",Long.toString(cid) ));
		complaintRepository.delete(c);
		
		return new ResponseEntity<String>("Complaint Deleted Successfully....", HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<Map<String, ComplaintStatus>> startComplaint(long Complaintid) {
		// TODO Auto-generated method stub
		Map<String, ComplaintStatus> map=new LinkedHashMap<String, ComplaintStatus>();
		Complaint c=complaintRepository.findById(Complaintid).orElseThrow(()->new ResourceNotFoundException
				("Compliant", "Complaint_id", Long.toString(Complaintid)));
			ComplaintStatus oldStatus = c.getStatus();
		if (c.getStatus().equals(ComplaintStatus.OPEN) ) {
			String logedinemail=SecurityContextHolder.getContext().getAuthentication().getName();
			c.setStatus(ComplaintStatus.IN_PROGRESS);
			c.setUpdatedby(logedinemail);
			c.setUpdatedAt(LocalDateTime.now());
			complaintRepository.save(c);
			saveHistory(c, oldStatus, "Complaint moved from " + oldStatus + " to " + c.getStatus());
			map.put("Complaint Status Changed To", ComplaintStatus.IN_PROGRESS);
		}else if (c.getStatus().equals(ComplaintStatus.ESCALATED)){
			String logedinemail=SecurityContextHolder.getContext().getAuthentication().getName();
			c.setStatus(ComplaintStatus.IN_PROGRESS);
			c.setUpdatedby(logedinemail);
			c.setUpdatedAt(LocalDateTime.now());
			complaintRepository.save(c);
			saveHistory(c, oldStatus, "Complaint moved from " + oldStatus + " to " + c.getStatus());
			map.put("Complaint Status Changed To", ComplaintStatus.IN_PROGRESS);
		}
		if (c.getStatus().equals(ComplaintStatus.IN_PROGRESS)){
			//Send Email code
			LOGGER.info("Complaint { } mmoved From OPEN TO IN_PROGRESS "+c.getComplaintid() );
			emailService.sendEmail(
			        c.getUser().getEmail(),
			        "Complaint  Move to IN_PROGRESS Stage",
			        "Hello " + c.getUser().getName()
			                + ",\n\n"
			                + "Your complaint is now Being handle by our support team .\n"
			                + "Complaint ID : " + c.getComplaintid()
			                + "\nStatus : " + c.getStatus()
			);
		}
		return new ResponseEntity<Map<String,ComplaintStatus>>(map, HttpStatus.OK) ;
	}
	
	@Override
	public ResponseEntity<Map<String, ComplaintStatus>> resolveComplaint(long Complaintid) {
		// TODO Auto-generated method stub
		Map<String, ComplaintStatus> map=new LinkedHashMap<String, ComplaintStatus>();
		Complaint c=complaintRepository.findById(Complaintid).orElseThrow(()->new ResourceNotFoundException
				("Compliant", "Complaint_id", Long.toString(Complaintid)));
		
				ComplaintStatus oldStatus = c.getStatus();
			if (c.getStatus().equals(ComplaintStatus.IN_PROGRESS)) {
				String logedinemail=SecurityContextHolder.getContext().getAuthentication().getName();
				c.setStatus(ComplaintStatus.RESOLVED);
				c.setUpdatedby(logedinemail);	
				c.setUpdatedAt(LocalDateTime.now());
				complaintRepository.save(c);
				saveHistory(c, oldStatus, "Complaint moved from " + oldStatus + " to " + c.getStatus());
				if (c.getStatus().equals(ComplaintStatus.RESOLVED)) {
					LOGGER.info("Complaint { } mmoved From IN_PROGRESS To RESOLVED  "+c.getComplaintid() );
					emailService.sendEmail(
					        c.getUser().getEmail(),
					        "Complaint Resolved Successfully",
					        "Hello " + c.getUser().getName()
					                + ",\n\n"
					                + "Your complaint has been Resolved successfully.\n"
					                + "Complaint ID : " + c.getComplaintid()
					                + "\nStatus : " + c.getStatus()
					);
				}
				
				map.put("Complaint Status Changed To", ComplaintStatus.RESOLVED);
			}
		return new ResponseEntity<Map<String,ComplaintStatus>>(map, HttpStatus.OK) ;
	}
	
	@Override
	public ResponseEntity<Map<String, ComplaintStatus>> closeComplaint(long Complaintid) {
		// TODO Auto-generated method stub
		Map<String, ComplaintStatus> map=new LinkedHashMap<String, ComplaintStatus>();
		Complaint c=complaintRepository.findById(Complaintid).orElseThrow(()->new ResourceNotFoundException
				("Compliant", "Complaint_id", Long.toString(Complaintid)));
			ComplaintStatus oldStatus=c.getStatus();
		if (c.getStatus().equals(ComplaintStatus.RESOLVED)) {
			String logedinemail=SecurityContextHolder.getContext().getAuthentication().getName();
			c.setStatus(ComplaintStatus.CLOSED);
			c.setUpdatedAt(LocalDateTime.now());
			c.setUpdatedby(logedinemail);
			complaintRepository.save(c);
			saveHistory(c, oldStatus, "Complaint moved from " + oldStatus + " to " + c.getStatus());
			if (c.getStatus().equals(ComplaintStatus.CLOSED)) {
				LOGGER.info("Complaint { } mmoved From RESOLVED TO CLOSED "+c.getComplaintid() );
				emailService.sendEmail(
				        c.getUser().getEmail(),
				        "Complaint closed Successfully",
				        "Hello " + c.getUser().getName()
				                + ",\n\n"
				                + "Your complaint has been Closed successfully.\n"
				                + "Complaint ID : " + c.getComplaintid()
				                + "\nStatus : " + c.getStatus()
				);
			}
			
			map.put("Complaint Status Changed To", ComplaintStatus.CLOSED);
		}
		return new ResponseEntity<Map<String,ComplaintStatus>>(map, HttpStatus.OK) ;
	}
	
	@Override
	public ResponseEntity<Resource> downloadAttachment(Long complaintId) throws IOException {

	    // Logged-in user's email
	    String loggedInEmail = SecurityContextHolder.getContext()
	            .getAuthentication()
	            .getName();

	    // Fetch logged-in user
	    User loggedInUser = userRepository.findByEmail(loggedInEmail)
	            .orElseThrow(() -> new ResourceNotFoundException("User", "Email_Id", loggedInEmail));

	    // Fetch complaint
	    Complaint complaint = complaintRepository.findById(complaintId)
	            .orElseThrow(() -> new ResourceNotFoundException("Complaint", "Commplaint_ID", Long.toString(complaintId)));

	    // CUSTOMER can download only their own attachment
	    if (loggedInUser.getRole() == Role.CUSTOMER &&
	            !complaint.getUser().getEmail().equals(loggedInEmail)) {

	        throw new AccessDeniedException(
	                "You are not authorized to access this attachment.");
	    }

	    // Read file
	    Path path = Paths.get(complaint.getFilePath());

	    Resource resource = new UrlResource(path.toUri());

	    if (!resource.exists()) {
	        throw new ResourceNotFoundException("Resource","", "This Resource");
	    }
	    
	    String contentType = Files.probeContentType(path);

	    if (contentType == null) {
	        contentType = "application/octet-stream";
	    }

	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION,
	                    "attachment; filename=\"" + complaint.getFileName() + "\"")
	            .body(resource);
	}
	
	
	@Override
	public ResponseEntity<List<ComplaintHistoryResponse>> getComplaintHistory(long complaintId) {
		List<ComplaintHistory> complaintHistories=complaintHistoryRepository.findByComplaintComplaintid(complaintId);
		List<ComplaintHistoryResponse> complaintHistoryResponses=new ArrayList<ComplaintHistoryResponse>();
		for (ComplaintHistory complaintHistory : complaintHistories) {
			ComplaintHistoryResponse responce=new ComplaintHistoryResponse();
			responce.setId(complaintHistory.getId());
			responce.setNewStatus(complaintHistory.getNewStatus());
			responce.setOldStatus(complaintHistory.getOldStatus());
			responce.setRemarks(complaintHistory.getRemarks());
			responce.setUpdatedAt(complaintHistory.getUpdatedAt());
			responce.setUpdatedBy(complaintHistory.getUpdatedBy());
			complaintHistoryResponses.add(responce);
		}
		return  new ResponseEntity<List<ComplaintHistoryResponse>>(complaintHistoryResponses, HttpStatus.OK);
	}
	
			private void saveHistory(Complaint complaint, ComplaintStatus oldStatus, String remarks) {

				ComplaintHistory history = new ComplaintHistory();

				history.setComplaint(complaint);
				history.setOldStatus(oldStatus);
				history.setNewStatus(complaint.getStatus());
				history.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
				history.setUpdatedAt(LocalDateTime.now());
				history.setRemarks(remarks);

				complaintHistoryRepository.save(history);
			}
			
		@Override
		public ResponseEntity<List<ComplaintResponse>> findByPriorityAndStatus(Priority priority , ComplaintStatus status) {
			// TODO Auto-generated method stub
			if (priority != null && status != null) {
				List<Complaint> list=complaintRepository.findByPriorityAndStatus(priority, status);
				List<ComplaintResponse> responseList= new ArrayList<ComplaintResponse>();
				for (Complaint complaint : list) {
					ComplaintResponse response =convertResponse(complaint);
					responseList.add(response);
				}
				return new ResponseEntity<List<ComplaintResponse>>(responseList, HttpStatus.OK);
			}else if (status != null) {
				List<Complaint> list=complaintRepository.findBystatus(status);
				List<ComplaintResponse> responseList= new ArrayList<ComplaintResponse>();
				for (Complaint complaint : list) {
					ComplaintResponse response =convertResponse(complaint);
					responseList.add(response);
				}
				return new ResponseEntity<List<ComplaintResponse>>(responseList, HttpStatus.OK);
			}else if (priority != null) {
				List<Complaint> list=complaintRepository.findBypriority(priority);
				List<ComplaintResponse> responseList= new ArrayList<ComplaintResponse>();
				for (Complaint complaint : list) {
					ComplaintResponse response =convertResponse(complaint);
					responseList.add(response);
				}
				return new ResponseEntity<List<ComplaintResponse>>(responseList, HttpStatus.OK);
			} 
			List<Complaint> li=complaintRepository.findAll();
			List<ComplaintResponse> responses=new ArrayList<ComplaintResponse>();
			for (Complaint complaint : li) {
				ComplaintResponse response=convertResponse(complaint);
				responses.add(response);
			}
			
			return new ResponseEntity<List<ComplaintResponse>>(responses, HttpStatus.OK);
		}
		
		
		@Override
		public ResponseEntity<List<ComplaintResponse>> searchComplaints(String keyword) {
			// TODO Auto-generated method stub
					List<Complaint> complaints=complaintRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
					List<ComplaintResponse> responses=new ArrayList<ComplaintResponse>();
					for (Complaint complaint : complaints) {
						responses.add(convertResponse(complaint));
					}
				
			return new ResponseEntity<List<ComplaintResponse>>(responses, HttpStatus.OK);
		}
			
		private ComplaintResponse convertResponse(Complaint complaint) {
			ComplaintResponse response=new ComplaintResponse();
			response.setDescription(complaint.getDescription()); 
			response.setPriority(complaint.getPriority());
			response.setStatus(complaint.getStatus());
			response.setTitle(complaint.getTitle());
			response.setUserid(complaint.getUser().getId());
			response.setComplaintid(complaint.getComplaintid());
			
			return response;
		}
	
}
