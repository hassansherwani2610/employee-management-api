package com.hassan.ems.mapper;

import com.hassan.ems.dto.request.DepartmentRequest;
import com.hassan.ems.dto.response.DepartmentResponse;
import com.hassan.ems.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    private String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }

    private String trimOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    public Department toEntity(DepartmentRequest request) {
        return Department.builder()
                .name(request.getName().trim())
                .code(normalizeCode(request.getCode()))
                .description(trimOrNull(request.getDescription()))
                .build();
    }

    public void updateEntity(Department department, DepartmentRequest request) {
        department.setName(request.getName().trim());
        department.setCode(normalizeCode(request.getCode()));
        department.setDescription(trimOrNull(request.getDescription()));
    }

    public DepartmentResponse toResponse(Department department) {
        return new DepartmentResponse(
                department.getId(),
                department.getName(),
                department.getCode(),
                department.getDescription(),
                department.getCreatedAt(),
                department.getUpdatedAt()
        );
    }
}
