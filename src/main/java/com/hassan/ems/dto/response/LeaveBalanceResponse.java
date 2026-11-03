package com.hassan.ems.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalanceResponse {

    private Long employeeId;
    private int year;
    private int annualAllowance;
    private long approvedDays;
    private long pendingDays;
    private long remainingDays;
}
