package com.hassan.ems.repository;

import com.hassan.ems.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);
    @Query("""
            SELECT d FROM Department d
            WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%')) 
            OR LOWER(d.code) LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    Page<Department> search(@Param("search") String search, Pageable pageable);
}
