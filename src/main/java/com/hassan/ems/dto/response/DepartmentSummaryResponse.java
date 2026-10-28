package com.hassan.ems.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentSummaryResponse {
    private Long id;
    private String name;
    private String code;
}
