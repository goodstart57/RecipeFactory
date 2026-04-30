package com.ljs.rfactory.batch.before.employee.reader;

import com.ljs.rfactory.batch.before.employee.entity.EmployeeSource;
import com.ljs.rfactory.batch.before.employee.repository.EmployeeSourceRepository;
import java.util.Collections;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.data.domain.Sort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmployeeSourceReaderConfig {

    private static final String READY_STATUS = "READY";
    private static final int EMPLOYEE_PAGE_SIZE = 2;

    @Bean
    @StepScope
    public RepositoryItemReader<EmployeeSource> employeeSourceReader(EmployeeSourceRepository employeeSourceRepository) {
        return new RepositoryItemReaderBuilder<EmployeeSource>()
                .name("employeeSourceReader")
                .repository(employeeSourceRepository)
                .methodName("findByStatus")
                .arguments(Collections.singletonList(READY_STATUS))
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .pageSize(EMPLOYEE_PAGE_SIZE)
                .build();
    }
}
