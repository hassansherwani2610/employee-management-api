package com.hassan.ems.service.impl;

import com.hassan.ems.dto.request.EmployeeRequest;
import com.hassan.ems.dto.response.EmployeeResponse;
import com.hassan.ems.dto.response.PageResponse;
import com.hassan.ems.enums.EmploymentStatus;
import com.hassan.ems.mapper.EmployeeMapper;
import com.hassan.ems.repository.DepartmentRepository;
import com.hassan.ems.repository.EmployeeRepository;
import com.hassan.ems.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Set<String> SORTABLE_FIELDS = Set.of("id", "firstName", "lastName", "email", "jobTitle", "salary", "hireDate", "status", "createdAt", "updatedAt");

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public EmployeeResponse create(EmployeeRequest request) {
        return null;
    }

    @Override
    public EmployeeResponse getById(Long id) {
        return null;
    }

    @Override
    public PageResponse<EmployeeResponse> getAll(String search, Long departmentId, EmploymentStatus status, Pageable pageable) {
        return null;
    }

    @Override
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
