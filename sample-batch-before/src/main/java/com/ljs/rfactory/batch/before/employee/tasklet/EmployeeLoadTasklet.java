package com.ljs.rfactory.batch.before.employee.tasklet;

import com.ljs.rfactory.batch.before.employee.dto.EmployeeSnapshotDto;
import com.ljs.rfactory.batch.before.employee.service.EmployeeTaskletService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class EmployeeLoadTasklet implements Tasklet {

    private static final Logger log = LoggerFactory.getLogger(EmployeeLoadTasklet.class);

    private final EmployeeTaskletService employeeTaskletService;

    public EmployeeLoadTasklet(EmployeeTaskletService employeeTaskletService) {
        this.employeeTaskletService = employeeTaskletService;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        List<EmployeeSnapshotDto> employees = employeeTaskletService.fetchReadyEmployees();
        log.info("sample_employee_load_step read {} employees", employees.size());
        return RepeatStatus.FINISHED;
    }
}
