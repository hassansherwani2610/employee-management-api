package com.hassan.ems.controller;

import com.hassan.ems.dto.request.EmployeeRequest;
import com.hassan.ems.dto.response.ApiResponse;
import com.hassan.ems.dto.response.EmployeeResponse;
import com.hassan.ems.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Employees", description = "Employee management")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/employees")
    @Operation(summary = "Create an employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> create(@Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Employee created successfully", employeeService.create(request)));
    }

    @GetMapping("/employees/{id}")
    @Operation(summary = "Get an employee by id")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Employee fetched successfully", employeeService.getById(id)));
    }

    // Specs one

    @PutMapping("/employees/{id}")
    @Operation(summary = "Update an employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", employeeService.update(id, request)));
    }

    @DeleteMapping("/employees/{id}")
    @Operation(summary = "Delete an employee")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Employee deleted successfully", null));
    }
}
