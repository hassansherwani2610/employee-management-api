package com.hassan.ems.specification;

import com.hassan.ems.entity.Employee;
import com.hassan.ems.enums.EmploymentStatus;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;

public final class EmployeeSpecification {

    private EmployeeSpecification() {}

    public static Specification<Employee> search(String term) {
        return (root, query, cb) -> {
            String pattern = "%" + escapeLike(term.toLowerCase()) + "%";

            Expression<String> firstName = cb.lower(root.<String>get("firstName"));
            Expression<String> lastName = cb.lower(root.<String>get("lastName"));
            Expression<String> fullName = cb.concat(cb.concat(firstName, " "), lastName);

            return cb.or(
                    cb.like(fullName, pattern, '\\'),
                    cb.like(cb.lower(root.<String>get("email")), pattern, '\\'),
                    cb.like(cb.lower(root.<String>get("jobTitle")), pattern, '\\')
            );
        };
    }

    public static Specification<Employee> hasDepartment(Long departmentId) {
        return (root, query, cb) -> cb.equal(root.get("department").get("id"), departmentId);
    }

    public static Specification<Employee> hasStatus(EmploymentStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    private static String escapeLike(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}