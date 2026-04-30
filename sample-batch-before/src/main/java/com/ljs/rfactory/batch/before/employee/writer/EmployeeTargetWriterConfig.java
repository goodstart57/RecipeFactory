package com.ljs.rfactory.batch.before.employee.writer;

import com.ljs.rfactory.batch.before.employee.entity.EmployeeTarget;
import com.ljs.rfactory.batch.before.employee.repository.EmployeeTargetRepository;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmployeeTargetWriterConfig {

    @Bean
    public RepositoryItemWriter<EmployeeTarget> sampleEmployeeTargetWriter(EmployeeTargetRepository employeeTargetRepository) {
        return new RepositoryItemWriterBuilder<EmployeeTarget>()
                .repository(employeeTargetRepository)
                .methodName("save")
                .build();
    }
}
