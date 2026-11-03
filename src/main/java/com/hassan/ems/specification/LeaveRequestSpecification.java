package com.hassan.ems.specification;

import com.hassan.ems.entity.LeaveRequest;
import com.hassan.ems.enums.LeaveStatus;
import com.hassan.ems.enums.LeaveType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class LeaveRequestSpecification {

    private LeaveRequestSpecification() {
    }

    public static Specification<LeaveRequest> hasEmployee(Long employeeId) {
        return (root, query, cb) -> cb.equal(root.get("employee").get("id"), employeeId);
    }

    public static Specification<LeaveRequest> hasStatus(LeaveStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<LeaveRequest> hasType(LeaveType type) {
        return (root, query, cb) -> cb.equal(root.get("leaveType"), type);
    }

    public static Specification<LeaveRequest> endsOnOrAfter(LocalDate from) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.<LocalDate>get("endDate"), from);
    }

    public static Specification<LeaveRequest> startsOnOrBefore(LocalDate to) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.<LocalDate>get("startDate"), to);
    }
}