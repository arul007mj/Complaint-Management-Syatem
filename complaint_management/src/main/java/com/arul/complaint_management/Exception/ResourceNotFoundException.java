package com.arul.complaint_management.Exception;

public class ResourceNotFoundException extends RuntimeException {

	String fieldresource;
	String fieldname;
	String id;
	
	public ResourceNotFoundException(String fieldresource, String fieldname, String id) {
		super();
		this.fieldresource = fieldresource;
		this.fieldname = fieldname;
		this.id = id;
	}
	
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return fieldresource +" Not Found For "+fieldname+" -> "+id;
	}
	
	
}

