package com.hassan.ems.controller;

import com.hassan.ems.dto.request.DepartmentRequest;
import com.hassan.ems.dto.response.ApiResponse;
import com.hassan.ems.dto.response.DepartmentResponse;
import com.hassan.ems.dto.response.PageResponse;
import com.hassan.ems.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Departments", description = "Department management")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping("/departments")
    @Operation(summary = "Create a department")
    public ResponseEntity<ApiResponse<DepartmentResponse>> create(@Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse response = departmentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Department created successfully", response));
    }

    @GetMapping("/departments/{id}")
    @Operation(summary = "Get a department by id")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getById(@PathVariable Long id) {
        DepartmentResponse response = departmentService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Department fetched successfully", response));
    }

    @GetMapping("/departments")
    @Operation(summary = "List departments (search by name or code, paginated, sortable)")
    public ResponseEntity<ApiResponse<PageResponse<DepartmentResponse>>> getAll(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "name") Pageable pageable
    ) {
        PageResponse<DepartmentResponse> response = departmentService.getAll(search, pageable);
        return ResponseEntity.ok(ApiResponse.success("Departments fetched successfully", response));
    }

    @PutMapping("/departments/{id}")
    @Operation(summary = "Update a department")
    public ResponseEntity<ApiResponse<DepartmentResponse>> update(@PathVariable Long id, @Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse response = departmentService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Department updated successfully", response));
    }

    @DeleteMapping("/departments/{id}")
    @Operation(summary = "Delete a department")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Department deleted successfully", null));
    }
}
