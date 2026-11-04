package com.hassan.ems.service.impl;

import com.hassan.ems.config.LeaveProperties;
import com.hassan.ems.dto.request.LeaveCreateRequest;
import com.hassan.ems.dto.request.LeaveReviewRequest;
import com.hassan.ems.dto.request.LeaveUpdateRequest;
import com.hassan.ems.dto.response.LeaveBalanceResponse;
import com.hassan.ems.dto.response.LeaveResponse;
import com.hassan.ems.dto.response.PageResponse;
import com.hassan.ems.entity.Employee;
import com.hassan.ems.entity.LeaveRequest;
import com.hassan.ems.enums.EmploymentStatus;
import com.hassan.ems.enums.LeaveStatus;
import com.hassan.ems.enums.LeaveType;
import com.hassan.ems.exception.BadRequestException;
import com.hassan.ems.exception.ConflictException;
import com.hassan.ems.exception.ResourceNotFoundException;
import com.hassan.ems.mapper.LeaveMapper;
import com.hassan.ems.repository.EmployeeRepository;
import com.hassan.ems.repository.LeaveRequestRepository;
import com.hassan.ems.service.LeaveRequestService;
import com.hassan.ems.specification.LeaveRequestSpecification;
import com.hassan.ems.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
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


    private int validateDatesAndCountDays(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            throw new BadRequestException("End date must be on or after start date");
        }

        if (start.getYear() != end.getYear()) {
            throw new BadRequestException("A leave request cannot span two calendar years. Please submit separate requests.");
        }

        int days = DateUtils.countWorkingDays(start, end);
        if (days == 0) {
            throw new BadRequestException("The selected dates contain no working days (Saturday and Sunday are excluded)");
        }

        return days;
    }

    private void assertNoOverlap(Long employeeId, LocalDate start, LocalDate end, long excludeId) {
        if (leaveRequestRepository.existsOverlapping(employeeId, ACTIVE_STATUSES, start, end, excludeId)) {
            throw new ConflictException("This employee already has a pending or approved leave overlapping these dates");
        }
    }

    private void assertBalance(Long employeeId, LeaveType type, LocalDate start, int requestedDays, long excludeId) {
        if (type != LeaveType.ANNUAL) {
            return;
        }

        LocalDate yearStart = LocalDate.of(start.getYear(), 1, 1);
        LocalDate yearEnd = LocalDate.of(start.getYear(), 12, 31);

        long used = leaveRequestRepository.sumDays(employeeId, LeaveType.ANNUAL, ACTIVE_STATUSES, yearStart, yearEnd, excludeId);
        int allowance = leaveProperties.getAnnualAllowanceDays();

        if (used + requestedDays > allowance) {
            throw new BadRequestException("Insufficient annual leave balance for %d. Allowance: %d, already used or pending: %d, requested: %d"
                            .formatted(start.getYear(), allowance, used, requestedDays));
        }
    }

    private void assertStatus(LeaveRequest leave, LeaveStatus expected, String message) {
        if (leave.getStatus() != expected) {
            throw new ConflictException(message + " (current status: " + leave.getStatus() + ")");
        }
    }

    private void applyReview(LeaveRequest leave, LeaveStatus newStatus, String comment) {
        leave.setStatus(newStatus);
        leave.setReviewComment(comment);
        leave.setReviewedAt(Instant.now());
    }

    private String commentOf(LeaveReviewRequest request) {
        if (request == null) {
            return null;
        }

        return leaveMapper.trimOrNull(request.getComment());
    }


    @Override
    @Transactional
    public LeaveResponse create(LeaveCreateRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + request.getEmployeeId()));

        if (employee.getStatus() == EmploymentStatus.TERMINATED) {
            throw new BadRequestException("Terminated employees cannot request leave");
        }

        int days = validateDatesAndCountDays(request.getStartDate(), request.getEndDate());
        assertNoOverlap(employee.getId(), request.getStartDate(), request.getEndDate(), NO_EXCLUSION);
        assertBalance(employee.getId(), request.getLeaveType(), request.getStartDate(), days, NO_EXCLUSION);

        LeaveRequest saved = leaveRequestRepository.save(leaveMapper.toEntity(request, employee, days));
        log.info("Leave request created: id={}, employeeId={}, days={}", saved.getId(), employee.getId(), days);
        return leaveMapper.toResponse(saved);
    }

    @Override
    public LeaveResponse getById(Long id) {
        return leaveMapper.toResponse(findOrThrow(id));
    }

    @Override
    public PageResponse<LeaveResponse> getAll(Long employeeId, LeaveStatus status, LeaveType leaveType, LocalDate from, LocalDate to, Pageable pageable) {
        validateSort(pageable);
        if (from != null && to != null && from.isAfter(to)) {
            throw new BadRequestException("'from' date must be on or before 'to' date");
        }

        List<Specification<LeaveRequest>> specs = new ArrayList<>();
        if (employeeId != null) specs.add(LeaveRequestSpecification.hasEmployee(employeeId));
        if (status != null) specs.add(LeaveRequestSpecification.hasStatus(status));
        if (leaveType != null) specs.add(LeaveRequestSpecification.hasType(leaveType));
        if (from != null) specs.add(LeaveRequestSpecification.endsOnOrAfter(from));
        if (to != null) specs.add(LeaveRequestSpecification.startsOnOrBefore(to));

        return PageResponse.from(
                leaveRequestRepository.findAll(Specification.allOf(specs), pageable)
                        .map(leaveMapper::toResponse)
        );
    }

    @Override
    @Transactional
    public LeaveResponse update(Long id, LeaveUpdateRequest request) {
        LeaveRequest leave = findOrThrow(id);
        assertStatus(leave, LeaveStatus.PENDING, "Only PENDING leave requests can be updated");

        int days = validateDatesAndCountDays(request.getStartDate(), request.getEndDate());
        Long employeeId = leave.getEmployee().getId();
        assertNoOverlap(employeeId, request.getStartDate(), request.getEndDate(), id);
        assertBalance(employeeId, request.getLeaveType(), request.getStartDate(), days, id);

        leaveMapper.updateEntity(leave, request, days);
        LeaveRequest saved = leaveRequestRepository.saveAndFlush(leave);
        log.info("Leave request updated: id={}", id);
        return leaveMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public LeaveResponse approve(Long id, LeaveReviewRequest request) {
        LeaveRequest leave = findOrThrow(id);
        assertStatus(leave, LeaveStatus.PENDING, "Only PENDING leave requests can be approved");

        applyReview(leave, LeaveStatus.APPROVED, commentOf(request));
        LeaveRequest saved = leaveRequestRepository.saveAndFlush(leave);
        log.info("Leave request approved: id={}", id);
        return leaveMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public LeaveResponse reject(Long id, LeaveReviewRequest request) {
        LeaveRequest leave = findOrThrow(id);
        assertStatus(leave, LeaveStatus.PENDING, "Only PENDING leave requests can be rejected");

        String comment = commentOf(request);
        if (comment == null) {
            throw new BadRequestException("A comment is required when rejecting a leave request");
        }

        applyReview(leave, LeaveStatus.REJECTED, comment);
        LeaveRequest saved = leaveRequestRepository.saveAndFlush(leave);
        log.info("Leave request rejected: id={}", id);
        return leaveMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public LeaveResponse cancel(Long id) {
        LeaveRequest leave = findOrThrow(id);

        if (leave.getStatus() != LeaveStatus.PENDING && leave.getStatus() != LeaveStatus.APPROVED) {
            throw new ConflictException("Only PENDING or APPROVED leave requests can be cancelled");
        }
        if (leave.getStatus() == LeaveStatus.APPROVED && !leave.getStartDate().isAfter(LocalDate.now())) {
            throw new ConflictException("An approved leave that has already started cannot be cancelled");
        }

        leave.setStatus(LeaveStatus.CANCELLED);
        LeaveRequest saved = leaveRequestRepository.saveAndFlush(leave);
        log.info("Leave request cancelled: id={}", id);
        return leaveMapper.toResponse(saved);
    }

    @Override
    public LeaveBalanceResponse getAnnualBalance(Long employeeId, Integer year) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }

        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        LocalDate yearStart = LocalDate.of(targetYear, 1, 1);
        LocalDate yearEnd = LocalDate.of(targetYear, 12, 31);

        long approved = leaveRequestRepository.sumDays(employeeId, LeaveType.ANNUAL, List.of(LeaveStatus.APPROVED), yearStart, yearEnd, NO_EXCLUSION);
        long pending = leaveRequestRepository.sumDays(employeeId, LeaveType.ANNUAL, List.of(LeaveStatus.PENDING), yearStart, yearEnd, NO_EXCLUSION);

        int allowance = leaveProperties.getAnnualAllowanceDays();
        long remaining = Math.max(0, allowance - approved - pending);
        return new LeaveBalanceResponse(employeeId, targetYear, allowance, approved, pending, remaining);
    }
}
