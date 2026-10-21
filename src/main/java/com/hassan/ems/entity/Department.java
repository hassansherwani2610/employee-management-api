package com.hassan.ems.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

@Entity
@Table(name = "departments", uniqueConstraints = {
        @UniqueConstraint(name = "uk_department_name", columnNames = "name"),
        @UniqueConstraint(name = "uk_departments_code", columnNames = "code")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department extends BaseEntity{

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 10)
    private String code;

    @Column(length = 500)
    private String description;
}
