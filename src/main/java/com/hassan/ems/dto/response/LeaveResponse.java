package com.hassan.ems.dto.response;

import com.hassan.ems.enums.LeaveStatus;
import com.hassan.ems.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveResponse {

    private Long id;
    private EmployeeSummaryResponse employee;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalDays;
    private String reason;
    private LeaveStatus status;
    private String reviewComment;
    private Instant reviewedAt;
    private Instant createdAt;
    private Instant updatedAt;
}