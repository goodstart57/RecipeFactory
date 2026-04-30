package com.ljs.rfactory.batch.before.employee.service;

import com.ljs.rfactory.batch.before.employee.entity.EmployeeSource;
import com.ljs.rfactory.batch.before.employee.entity.EmployeeTarget;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class EmployeeBatchService {

    public EmployeeTarget mapToTarget(EmployeeSource source) {
        EmployeeTarget target = new EmployeeTarget();
        target.setSourceEmployeeId(source.getId());
        target.setEmployeeNo(source.getEmployeeNo());
        target.setEmployeeName(source.getEmployeeName());
        target.setNormalizedDepartment(source.getDepartment().trim().toUpperCase());
        target.setProcessedAt(LocalDateTime.now());
        return target;
    }
}
