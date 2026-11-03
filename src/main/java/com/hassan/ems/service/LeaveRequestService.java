package com.hassan.ems.service;

import com.hassan.ems.dto.request.LeaveCreateRequest;
import com.hassan.ems.dto.request.LeaveReviewRequest;
import com.hassan.ems.dto.request.LeaveUpdateRequest;
import com.hassan.ems.dto.response.LeaveBalanceResponse;
import com.hassan.ems.dto.response.LeaveResponse;
import com.hassan.ems.dto.response.PageResponse;
import com.hassan.ems.enums.LeaveStatus;
import com.hassan.ems.enums.LeaveType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface LeaveRequestService {

    LeaveResponse create(LeaveCreateRequest request);

    LeaveResponse getById(Long id);

    PageResponse<LeaveResponse> getAll(Long employeeId, LeaveStatus status, LeaveType leaveType, LocalDate from, LocalDate to, Pageable pageable);

    LeaveResponse update(Long id, LeaveUpdateRequest request);

    LeaveResponse approve(Long id, LeaveReviewRequest request);

    LeaveResponse reject(Long id, LeaveReviewRequest request);

    LeaveResponse cancel(Long id);

    LeaveBalanceResponse getAnnualBalance(Long employeeId, Integer year);
}