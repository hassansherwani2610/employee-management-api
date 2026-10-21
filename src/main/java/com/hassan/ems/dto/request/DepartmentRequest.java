package com.hassan.ems.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentRequest {

    @NotBlank(message = "Department name is required")
    @Size(min = 2, max = 100, message = "Department name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Department code is required")
    @Size(min = 2, max = 10, message = "Department code must be between 2 and 10 characters")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Department code may contain only letters, digits, '-' and '_'")
    private String code;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
