package com.hassan.ems.controller;


import com.hassan.ems.dto.request.LeaveCreateRequest;
import com.hassan.ems.dto.request.LeaveReviewRequest;
import com.hassan.ems.dto.request.LeaveUpdateRequest;
import com.hassan.ems.dto.response.ApiResponse;
import com.hassan.ems.dto.response.LeaveBalanceResponse;
import com.hassan.ems.dto.response.LeaveResponse;
import com.hassan.ems.dto.response.PageResponse;
import com.hassan.ems.enums.LeaveStatus;
import com.hassan.ems.enums.LeaveType;
import com.hassan.ems.service.LeaveRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/api/v1")
@Tag(name = "Leave Requests", description = "Leave request management")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }


    @PostMapping("/leave-requests")
    @Operation(summary = "Submit a leave request")
    public ResponseEntity<ApiResponse<LeaveResponse>> create(@Valid @RequestBody LeaveCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Leave request submitted successfully", leaveRequestService.create(request)));
    }

    @GetMapping("/leave-requests/{id}")
    @Operation(summary = "Get a leave request by id")
    public ResponseEntity<ApiResponse<LeaveResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Leave request fetched successfully", leaveRequestService.getById(id)));
    }

    @GetMapping("/leave-requests")
    @Operation(summary = "List leave requests (filter by employee, status, type and date window)")
    public ResponseEntity<ApiResponse<PageResponse<LeaveResponse>>> getAll(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) LeaveStatus status,
            @RequestParam(required = false) LeaveType leaveType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Leave requests fetched successfully", leaveRequestService.getAll(employeeId, status, leaveType, from, to, pageable)));
    }

    @GetMapping("/leave-requests/balance")
    @Operation(summary = "Annual leave balance of an employee for a year (defaults to current year)")
    public ResponseEntity<ApiResponse<LeaveBalanceResponse>> getBalance(@RequestParam Long employeeId, @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(ApiResponse.success("Leave balance fetched successfully", leaveRequestService.getAnnualBalance(employeeId, year)));
    }

    @PutMapping("/leave-requests/{id}")
    @Operation(summary = "Update a PENDING leave request")
    public ResponseEntity<ApiResponse<LeaveResponse>> update(@PathVariable Long id, @Valid @RequestBody LeaveUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Leave request updated successfully", leaveRequestService.update(id, request)));
    }

    @PatchMapping("/leave-requests/{id}/approve")
    @Operation(summary = "Approve a PENDING leave request (comment optional)")
    public ResponseEntity<ApiResponse<LeaveResponse>> approve(@PathVariable Long id, @Valid @RequestBody(required = false) LeaveReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Leave request approved", leaveRequestService.approve(id, request)));
    }

    @PatchMapping("/leave-requests/{id}/reject")
    @Operation(summary = "Reject a PENDING leave request (comment required)")
    public ResponseEntity<ApiResponse<LeaveResponse>> reject(@PathVariable Long id, @Valid @RequestBody(required = false) LeaveReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Leave request rejected", leaveRequestService.reject(id, request)));
    }

    @PatchMapping("/leave-requests/{id}/cancel")
    @Operation(summary = "Cancel a PENDING or not-yet-started APPROVED leave request")
    public ResponseEntity<ApiResponse<LeaveResponse>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Leave request cancelled", leaveRequestService.cancel(id)));
    }
}
