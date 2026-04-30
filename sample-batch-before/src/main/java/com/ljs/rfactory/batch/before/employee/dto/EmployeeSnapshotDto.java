package com.ljs.rfactory.batch.before.employee.dto;

public class EmployeeSnapshotDto {

    private final Long employeeId;
    private final String employeeNo;
    private final String employeeName;

    public EmployeeSnapshotDto(Long employeeId, String employeeNo, String employeeName) {
        this.employeeId = employeeId;
        this.employeeNo = employeeNo;
        this.employeeName = employeeName;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public String getEmployeeName() {
        return employeeName;
    }
}
