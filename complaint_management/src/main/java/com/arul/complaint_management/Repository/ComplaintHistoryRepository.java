package com.arul.complaint_management.Repository;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

import com.arul.complaint_management.entity.ComplaintHistory;

public interface ComplaintHistoryRepository
        extends JpaRepository<ComplaintHistory, Long> {

    List<ComplaintHistory> findByComplaintComplaintid(long complaintid);

}
