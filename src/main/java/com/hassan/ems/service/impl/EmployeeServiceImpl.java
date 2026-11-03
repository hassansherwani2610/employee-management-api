package com.hassan.ems.service.impl;

import com.hassan.ems.dto.request.EmployeeRequest;
import com.hassan.ems.dto.response.EmployeeResponse;
import com.hassan.ems.dto.response.PageResponse;
import com.hassan.ems.entity.Department;
import com.hassan.ems.entity.Employee;
import com.hassan.ems.enums.EmploymentStatus;
import com.hassan.ems.exception.BadRequestException;
import com.hassan.ems.exception.DuplicateResourceException;
import com.hassan.ems.exception.ResourceNotFoundException;
import com.hassan.ems.mapper.EmployeeMapper;
import com.hassan.ems.repository.DepartmentRepository;
import com.hassan.ems.repository.EmployeeRepository;
import com.hassan.ems.service.EmployeeService;
import com.hassan.ems.specification.EmployeeSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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

    private Employee findOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    private Department findDepartmentOrThrow(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
    }

    private void assertUniqueEmail(String email, Long id) {
        boolean emailTaken = (id == null) ? employeeRepository.existsByEmail(email) : employeeRepository.existsByEmailAndIdNot(email, id);

        if (emailTaken) {
            throw new DuplicateResourceException("Employee with email '%s' already exists".formatted(email));
        }
    }

    private void validateSort(Pageable pageable) {
        pageable.getSort().forEach(order -> {
            if (!SORTABLE_FIELDS.contains(order.getProperty())) {
                throw new BadRequestException("Invalid sort field: " + order.getProperty() + ". Allowed: " + SORTABLE_FIELDS);
            }
        });
    }


    @Override
    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        assertUniqueEmail(employeeMapper.normalizeEmail(request.getEmail()), null);

        Department department = findDepartmentOrThrow(request.getDepartmentId());

        Employee savedEmployee = employeeRepository.save(employeeMapper.toEntity(request, department));
        log.info("Employee created: id={}, departmentId={}", savedEmployee.getId(), department.getId());
        return employeeMapper.toResponse(savedEmployee);
    }

    @Override
    public EmployeeResponse getById(Long id) {
        return employeeMapper.toResponse(findOrThrow(id));
    }

    @Override
    public PageResponse<EmployeeResponse> getAll(String search, Long departmentId, EmploymentStatus status, Pageable pageable) {
        validateSort(pageable);

        List<Specification<Employee>> specs = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            specs.add(EmployeeSpecification.search(search.trim()));
        }

        if (departmentId != null) {
            specs.add(EmployeeSpecification.hasDepartment(departmentId));
        }

        if (status != null) {
            specs.add(EmployeeSpecification.hasStatus(status));
        }

        return PageResponse.from(
                employeeRepository
                        .findAll(Specification.allOf(specs), pageable)
                        .map(employeeMapper::toResponse)
        );
    }

    @Override
    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = findOrThrow(id);

        assertUniqueEmail(employeeMapper.normalizeEmail(request.getEmail()), id);

        Department department = findDepartmentOrThrow(request.getDepartmentId());

        employeeMapper.updateEntity(employee, request, department);
        Employee savedEmployee = employeeRepository.saveAndFlush(employee);
        log.info("Employee updated: id={}", id);
        return employeeMapper.toResponse(savedEmployee);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Employee employee = findOrThrow(id);
        employeeRepository.delete(employee);
        log.info("Employee deleted: id={}", id);
    }
}
