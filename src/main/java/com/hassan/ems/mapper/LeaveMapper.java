package com.hassan.ems.mapper;

import com.hassan.ems.dto.request.LeaveCreateRequest;
import com.hassan.ems.dto.request.LeaveUpdateRequest;
import com.hassan.ems.dto.response.EmployeeSummaryResponse;
import com.hassan.ems.dto.response.LeaveResponse;
import com.hassan.ems.entity.Employee;
import com.hassan.ems.entity.LeaveRequest;
import com.hassan.ems.enums.LeaveStatus;
import org.springframework.stereotype.Component;

@Component
public class LeaveMapper {

    public String trimOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    public LeaveRequest toEntity(LeaveCreateRequest request, Employee employee, int totalDays) {
        return LeaveRequest.builder()
                .employee(employee)
                .leaveType(request.getLeaveType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalDays(totalDays)
                .reason(request.getReason().trim())
                .status(LeaveStatus.PENDING)
                .build();
    }

    public void updateEntity(LeaveRequest leave, LeaveUpdateRequest request, int totalDays) {
        leave.setLeaveType(request.getLeaveType());
        leave.setStartDate(request.getStartDate());
        leave.setEndDate(request.getEndDate());
        leave.setTotalDays(totalDays);
        leave.setReason(request.getReason().trim());
    }

    public LeaveResponse toResponse(LeaveRequest leave) {
        Employee employee = leave.getEmployee();
        return new LeaveResponse(
                leave.getId(),
                new EmployeeSummaryResponse(employee.getId(),employee.getFirstName() + " " + employee.getLastName(), employee.getEmail()),
                leave.getLeaveType(),
                leave.getStartDate(),
                leave.getEndDate(),
                leave.getTotalDays(),
                leave.getReason(),
                leave.getStatus(),
                leave.getReviewComment(),
                leave.getReviewedAt(),
                leave.getCreatedAt(),
                leave.getUpdatedAt()
        );
    }
}