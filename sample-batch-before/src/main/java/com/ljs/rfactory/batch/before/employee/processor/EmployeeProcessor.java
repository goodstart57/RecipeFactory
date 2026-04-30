package com.ljs.rfactory.batch.before.employee.processor;

import com.ljs.rfactory.batch.before.employee.entity.EmployeeSource;
import com.ljs.rfactory.batch.before.employee.entity.EmployeeTarget;
import com.ljs.rfactory.batch.before.employee.service.EmployeeBatchService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class EmployeeProcessor implements ItemProcessor<EmployeeSource, EmployeeTarget> {

    private final EmployeeBatchService employeeBatchService;

    public EmployeeProcessor(EmployeeBatchService employeeBatchService) {
        this.employeeBatchService = employeeBatchService;
    }

    @Override
    public EmployeeTarget process(EmployeeSource item) {
        return employeeBatchService.mapToTarget(item);
    }
}
