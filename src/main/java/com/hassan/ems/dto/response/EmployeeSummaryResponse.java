package com.hassan.ems.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSummaryResponse {

    private Long id;
    private String fullName;
    private String email;
}
