package com.hassan.ems.service.impl;

import com.hassan.ems.config.LeaveProperties;
import com.hassan.ems.dto.request.LeaveCreateRequest;
import com.hassan.ems.dto.request.LeaveReviewRequest;
import com.hassan.ems.dto.request.LeaveUpdateRequest;
import com.hassan.ems.dto.response.LeaveBalanceResponse;
import com.hassan.ems.dto.response.LeaveResponse;
import com.hassan.ems.dto.response.PageResponse;
import com.hassan.ems.entity.LeaveRequest;
import com.hassan.ems.enums.LeaveStatus;
import com.hassan.ems.enums.LeaveType;
import com.hassan.ems.exception.BadRequestException;
import com.hassan.ems.exception.ResourceNotFoundException;
import com.hassan.ems.mapper.LeaveMapper;
import com.hassan.ems.repository.EmployeeRepository;
import com.hassan.ems.repository.LeaveRequestRepository;
import com.hassan.ems.service.LeaveRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private static final List<LeaveStatus> ACTIVE_STATUSES = List.of(LeaveStatus.PENDING, LeaveStatus.APPROVED);
    private static final long NO_EXCLUSION = -1L;
    private static final Set<String> SORTABLE_FIELDS = Set.of("id", "leaveType", "startDate", "endDate", "totalDays", "status", "createdAt", "updatedAt");

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveMapper leaveMapper;
    private final LeaveProperties leaveProperties;

    public LeaveRequestServiceImpl(LeaveRequestRepository leaveRequestRepository, EmployeeRepository employeeRepository, LeaveMapper leaveMapper, LeaveProperties leaveProperties) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
        this.leaveMapper = leaveMapper;
        this.leaveProperties = leaveProperties;
    }

    private LeaveRequest findOrThrow(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));
    }

    private void validateSort(Pageable pageable) {
        pageable.getSort().forEach(order -> {
            if (!SORTABLE_FIELDS.contains(order.getProperty())) {
                throw new BadRequestException("Invalid sort field: " + order.getProperty() + ". Allowed: " + SORTABLE_FIELDS);
            }
        });
    }


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
