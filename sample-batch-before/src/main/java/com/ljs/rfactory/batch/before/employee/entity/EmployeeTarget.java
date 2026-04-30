package com.ljs.rfactory.batch.before.employee.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "employee_target")
public class EmployeeTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_employee_id", nullable = false)
    private Long sourceEmployeeId;

    @Column(name = "employee_no", nullable = false)
    private String employeeNo;

    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    @Column(name = "normalized_department", nullable = false)
    private String normalizedDepartment;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSourceEmployeeId() {
        return sourceEmployeeId;
    }

    public void setSourceEmployeeId(Long sourceEmployeeId) {
        this.sourceEmployeeId = sourceEmployeeId;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getNormalizedDepartment() {
        return normalizedDepartment;
    }

    public void setNormalizedDepartment(String normalizedDepartment) {
        this.normalizedDepartment = normalizedDepartment;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}
