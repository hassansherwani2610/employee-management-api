package com.hassan.ems.mapper;

import com.hassan.ems.dto.request.EmployeeRequest;
import com.hassan.ems.dto.response.DepartmentSummaryResponse;
import com.hassan.ems.dto.response.EmployeeResponse;
import com.hassan.ems.entity.Department;
import com.hassan.ems.entity.Employee;
import com.hassan.ems.enums.EmploymentStatus;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String trimOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    public Employee toEntity(EmployeeRequest request, Department department) {
        return Employee.builder()
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .email(normalizeEmail(request.getEmail()))
                .phone(trimOrNull(request.getPhone()))
                .jobTitle(request.getJobTitle().trim())
                .salary(request.getSalary())
                .hireDate(request.getHireDate())
                .status(request.getStatus() != null ? request.getStatus() : EmploymentStatus.ACTIVE)
                .department(department)
                .build();
    }

    public void updateEntity(Employee employee, EmployeeRequest request, Department department) {
        employee.setFirstName(request.getFirstName().trim());
        employee.setLastName(request.getLastName().trim());
        employee.setEmail(normalizeEmail(request.getEmail()));
        employee.setPhone(trimOrNull(request.getPhone()));
        employee.setJobTitle(request.getJobTitle().trim());
        employee.setSalary(request.getSalary());
        employee.setHireDate(request.getHireDate());
        if (request.getStatus() != null) {
            employee.setStatus(request.getStatus());
        }
        employee.setDepartment(department);
    }

    public EmployeeResponse toResponse(Employee employee) {
        Department department = employee.getDepartment();
        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getFirstName() + " " + employee.getLastName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getJobTitle(),
                employee.getSalary(),
                employee.getHireDate(),
                employee.getStatus(),
                new DepartmentSummaryResponse(department.getId(), department.getName(), department.getCode()),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }
}