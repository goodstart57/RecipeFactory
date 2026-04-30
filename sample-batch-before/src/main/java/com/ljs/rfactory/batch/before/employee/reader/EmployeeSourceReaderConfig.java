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

    @Bean
    @StepScope
    public RepositoryItemReader<EmployeeSource> sampleEmployeeSourceReader(EmployeeSourceRepository employeeSourceRepository) {
        return new RepositoryItemReaderBuilder<EmployeeSource>()
                .name("sampleEmployeeSourceReader")
                .repository(employeeSourceRepository)
                .methodName("findByStatus")
                .arguments(Collections.singletonList("READY"))
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .pageSize(2)
                .build();
    }
}
