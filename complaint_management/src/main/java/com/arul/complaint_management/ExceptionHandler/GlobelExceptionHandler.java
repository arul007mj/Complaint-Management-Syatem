package com.arul.complaint_management.ExceptionHandler;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.arul.complaint_management.Exception.ResourceNotFoundException;

@RestControllerAdvice
public class GlobelExceptionHandler {
	
	@ExceptionHandler(value = ResourceNotFoundException.class)
	public ResponseEntity<String> handleResourceNotFoundException (ResourceNotFoundException e) {
		
		return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public ResponseEntity<LinkedHashMap<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		
		LinkedHashMap<String , String > errors=new LinkedHashMap<String, String>();
		List<FieldError> l=ex.getBindingResult().getFieldErrors();
		
		for (FieldError fieldError : l) {
			String field=fieldError.getField();
			String message=fieldError.getDefaultMessage();
			errors.put(field+ ":", message);
		}
		
		return new ResponseEntity<LinkedHashMap<String,String>>(errors, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value =DataIntegrityViolationException.class )
	public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
		
		return new ResponseEntity<String>("EMAIL IS ALREDY EXISTS...", HttpStatus.BAD_REQUEST);
	}
	
//	@ExceptionHandler(value = HttpMessageNotReadableException.class)
//	public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
//		return new ResponseEntity<String>("Your Missing Some Field Data Plese Enter .... ", HttpStatus.BAD_REQUEST);
//	}
	
}
