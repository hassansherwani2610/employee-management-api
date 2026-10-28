package com.hassan.ems.dto.response;

import com.hassan.ems.enums.EmploymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private String jobTitle;
    private BigDecimal salary;
    private LocalDate hireDate;
    private EmploymentStatus status;
    private DepartmentSummaryResponse department;
    private Instant createdAt;
    private Instant updatedAt;
}
