package com.ljs.rfactory.batch.before.employee.service;

import com.ljs.rfactory.batch.before.employee.dto.EmployeeSnapshotDto;
import com.ljs.rfactory.batch.before.employee.entity.EmployeeSource;
import com.ljs.rfactory.batch.before.employee.repository.EmployeeSourceRepository;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeTaskletService {

    private final EmployeeSourceRepository employeeSourceRepository;

    public EmployeeTaskletService(EmployeeSourceRepository employeeSourceRepository) {
        this.employeeSourceRepository = employeeSourceRepository;
    }

    @Transactional(readOnly = true)
    public List<EmployeeSnapshotDto> fetchReadyEmployees() {
        List<EmployeeSource> employees = employeeSourceRepository.findByStatus(
                        "READY",
                        PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.ASC, "id")))
                .getContent();

        return employees.stream()
                .map(employee -> new EmployeeSnapshotDto(
                        employee.getId(),
                        employee.getEmployeeNo(),
                        employee.getEmployeeName()))
                .collect(Collectors.toList());
    }
}
