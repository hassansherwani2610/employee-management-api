package com.hassan.ems.repository;

import com.hassan.ems.entity.LeaveRequest;
import com.hassan.ems.enums.LeaveStatus;
import com.hassan.ems.enums.LeaveType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long>, JpaSpecificationExecutor<LeaveRequest> {

    boolean existsByEmployeeId(Long employeeId);

    @Query("""
            SELECT COUNT(l) > 0 FROM LeaveRequest l
            WHERE l.employee.id = :employeeId
              AND l.status IN :statuses
              AND l.startDate <= :endDate
              AND l.endDate >= :startDate
              AND l.id <> :excludeId
            """)
    boolean existsOverlapping(@Param("employeeId") Long employeeId,
                              @Param("statuses") Collection<LeaveStatus> statuses,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate,
                              @Param("excludeId") Long excludeId);

    @Query("""
            SELECT COALESCE(SUM(l.totalDays), 0) FROM LeaveRequest l
            WHERE l.employee.id = :employeeId
              AND l.leaveType = :leaveType
              AND l.status IN :statuses
              AND l.startDate BETWEEN :rangeStart AND :rangeEnd
              AND l.id <> :excludeId
            """)
    long sumDays(@Param("employeeId") Long employeeId,
                 @Param("leaveType") LeaveType leaveType,
                 @Param("statuses") Collection<LeaveStatus> statuses,
                 @Param("rangeStart") LocalDate rangeStart,
                 @Param("rangeEnd") LocalDate rangeEnd,
                 @Param("excludeId") Long excludeId);

    @Override
    @EntityGraph(attributePaths = "employee")
    Optional<LeaveRequest> findById(Long id);

    @Override
    @EntityGraph(attributePaths = "employee")
    Page<LeaveRequest> findAll(Specification<LeaveRequest> spec, Pageable pageable);
}
