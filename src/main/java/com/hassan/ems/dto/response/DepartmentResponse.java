package com.hassan.ems.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
@Builder
public class DepartmentResponse {
    Long id;
    String name;
    String code;
    String description;
    Instant createdAt;
    Instant updatedAt;
}
