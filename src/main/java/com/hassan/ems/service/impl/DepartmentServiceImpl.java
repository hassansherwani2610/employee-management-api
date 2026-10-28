package com.hassan.ems.service.impl;

import com.hassan.ems.dto.request.DepartmentRequest;
import com.hassan.ems.dto.response.DepartmentResponse;
import com.hassan.ems.dto.response.PageResponse;
import com.hassan.ems.entity.Department;
import com.hassan.ems.exception.BadRequestException;
import com.hassan.ems.exception.ConflictException;
import com.hassan.ems.exception.DuplicateResourceException;
import com.hassan.ems.exception.ResourceNotFoundException;
import com.hassan.ems.mapper.DepartmentMapper;
import com.hassan.ems.repository.DepartmentRepository;
import com.hassan.ems.repository.EmployeeRepository;
import com.hassan.ems.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
public class DepartmentServiceImpl implements DepartmentService {

    private static final String RESOURCE = "Department";
    private static final Set<String> SORTABLE_FIELDS = Set.of("id", "name", "code", "createdAt", "updatedAt");

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository, DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.departmentMapper = departmentMapper;
    }

    private Department findOrThrow(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("%s not found with id: %d".formatted(RESOURCE, id)));
    }

    private void validateSort(Sort sort) {
        sort.forEach(order -> {
            if (!SORTABLE_FIELDS.contains(order.getProperty())) {
                throw new BadRequestException("Invalid sort field: " + order.getProperty() + ". Allowed fields are: " + SORTABLE_FIELDS);
            }
        });
    }

    private void checkFieldsUniqueness(String name, String code, Long id) {
        boolean nameTaken = (id == null ? departmentRepository.existsByNameIgnoreCase(name) : departmentRepository.existsByNameIgnoreCaseAndIdNot(name, id));

        if (nameTaken) {
            throw new DuplicateResourceException("Department with name '%s' already exists".formatted(name));
        }

        boolean codeTaken = (id == null ? departmentRepository.existsByCode(code) : departmentRepository.existsByCodeAndIdNot(code, id));

        if (codeTaken) {
            throw new DuplicateResourceException("Department with code '%s' already exists".formatted(code));
        }
    }


    @Override
    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        Department department = departmentMapper.toEntity(request);
        checkFieldsUniqueness(department.getName(), department.getCode(), null);

        Department savedRequest = departmentRepository.save(department);
        log.info("Department created: id={}, code={}", savedRequest.getId(), savedRequest.getCode());
        return departmentMapper.toResponse(savedRequest);
    }

    @Override
    public DepartmentResponse getById(Long id) {
        return departmentMapper.toResponse(findOrThrow(id));
    }

    @Override
    public PageResponse<DepartmentResponse> getAll(String search, Pageable pageable) {
        validateSort(pageable.getSort());

        String normalizedSearch = (search == null ? "" : search.trim());

        return PageResponse.from(departmentRepository.search(normalizedSearch, pageable).map(departmentMapper::toResponse));
    }

    @Override
    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department department = findOrThrow(id);

        String name = departmentMapper.normalizeName(request.getName());
        String code = departmentMapper.normalizeCode(request.getCode());
        checkFieldsUniqueness(name, code, id);

        departmentMapper.updateEntity(department, request);
        Department savedRequest = departmentRepository.saveAndFlush(department);
        log.info("Department updated: id={}", id);
        return departmentMapper.toResponse(savedRequest);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Department department = findOrThrow(id);
        if (employeeRepository.existsByDepartmentId(id)) {
            throw new ConflictException("Cannot delete department '%s' because it still has employees".formatted(department.getName()));
        }

        departmentRepository.delete(department);
        log.info("Department deleted: id={}", id);
    }
}
