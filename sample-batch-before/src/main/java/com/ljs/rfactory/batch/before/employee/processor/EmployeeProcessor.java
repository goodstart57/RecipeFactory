package com.ljs.rfactory.batch.before.employee.processor;

import com.ljs.rfactory.batch.before.employee.entity.EmployeeSource;
import com.ljs.rfactory.batch.before.employee.entity.EmployeeTarget;
import java.time.LocalDateTime;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class EmployeeProcessor implements ItemProcessor<EmployeeSource, EmployeeTarget> {

    @Override
    public EmployeeTarget process(EmployeeSource item) {
        EmployeeTarget target = new EmployeeTarget();
        target.setSourceEmployeeId(item.getId());
        target.setEmployeeNo(item.getEmployeeNo());
        target.setEmployeeName(item.getEmployeeName());
        target.setNormalizedDepartment(item.getDepartment().trim().toUpperCase());
        target.setProcessedAt(LocalDateTime.now());
        return target;
    }
}
