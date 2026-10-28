package com.hassan.ems.service;

import com.hassan.ems.dto.request.EmployeeRequest;
import com.hassan.ems.dto.response.EmployeeResponse;
import com.hassan.ems.dto.response.PageResponse;
import com.hassan.ems.enums.EmploymentStatus;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    EmployeeResponse create(EmployeeRequest request);

    EmployeeResponse getById(Long id);

    PageResponse<EmployeeResponse> getAll(String search, Long departmentId, EmploymentStatus status, Pageable pageable);

    EmployeeResponse update(Long id, EmployeeRequest request);

    void delete(Long id);
}
