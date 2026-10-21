package com.hassan.ems.service;

import com.hassan.ems.dto.request.DepartmentRequest;
import com.hassan.ems.dto.response.DepartmentResponse;
import com.hassan.ems.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {

    DepartmentResponse create(DepartmentRequest request);
    DepartmentResponse getById(Long id);
    PageResponse<DepartmentResponse> getAll(String search, Pageable pageable);
    DepartmentResponse update(Long id, DepartmentRequest request);
    void delete(Long id);
}
