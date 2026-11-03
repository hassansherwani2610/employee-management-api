package com.hassan.ems.service.impl;

import com.hassan.ems.dto.request.LeaveCreateRequest;
import com.hassan.ems.dto.request.LeaveReviewRequest;
import com.hassan.ems.dto.request.LeaveUpdateRequest;
import com.hassan.ems.dto.response.LeaveBalanceResponse;
import com.hassan.ems.dto.response.LeaveResponse;
import com.hassan.ems.dto.response.PageResponse;
import com.hassan.ems.enums.LeaveStatus;
import com.hassan.ems.enums.LeaveType;
import com.hassan.ems.service.LeaveRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    @Override
    public LeaveResponse create(LeaveCreateRequest request) {
        return null;
    }

    @Override
    public LeaveResponse getById(Long id) {
        return null;
    }

    @Override
    public PageResponse<LeaveResponse> getAll(Long employeeId, LeaveStatus status, LeaveType leaveType, LocalDate from, LocalDate to, Pageable pageable) {
        return null;
    }

    @Override
    public LeaveResponse update(Long id, LeaveUpdateRequest request) {
        return null;
    }

    @Override
    public LeaveResponse approve(Long id, LeaveReviewRequest request) {
        return null;
    }

    @Override
    public LeaveResponse reject(Long id, LeaveReviewRequest request) {
        return null;
    }

    @Override
    public LeaveResponse cancel(Long id) {
        return null;
    }

    @Override
    public LeaveBalanceResponse getAnnualBalance(Long employeeId, Integer year) {
        return null;
    }
}
