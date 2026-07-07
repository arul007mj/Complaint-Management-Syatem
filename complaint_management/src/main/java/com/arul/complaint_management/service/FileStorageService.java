package com.arul.complaint_management.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

	String uploadFile(MultipartFile file);
}
