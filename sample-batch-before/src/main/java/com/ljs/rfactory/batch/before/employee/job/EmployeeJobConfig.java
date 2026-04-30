package com.ljs.rfactory.batch.before.employee.job;

import com.ljs.rfactory.batch.before.employee.entity.EmployeeSource;
import com.ljs.rfactory.batch.before.employee.entity.EmployeeTarget;
import com.ljs.rfactory.batch.before.employee.listener.EmployeeJobExecutionListener;
import com.ljs.rfactory.batch.before.employee.tasklet.EmployeeLoadTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmployeeJobConfig {

    private static final int EMPLOYEE_CHUNK_SIZE = 2;

    @Bean
    public Job employeeJob(JobBuilderFactory jobBuilderFactory,
                           Step employeeLoadStep,
                           Step employeeChunkStep,
                           EmployeeJobExecutionListener employeeJobExecutionListener) {
        return jobBuilderFactory.get("sample_employeeJob")
                .listener(employeeJobExecutionListener)
                .start(employeeLoadStep)
                .next(employeeChunkStep)
                .build();
    }

    @Bean
    public Step employeeLoadStep(StepBuilderFactory stepBuilderFactory, EmployeeLoadTasklet employeeLoadTasklet) {
        return stepBuilderFactory.get("sample_employeeLoadStep")
                .tasklet(employeeLoadTasklet)
                .build();
    }

    @Bean
    public Step employeeChunkStep(StepBuilderFactory stepBuilderFactory,
                                  RepositoryItemReader<EmployeeSource> employeeSourceReader,
                                  ItemProcessor<EmployeeSource, EmployeeTarget> employeeProcessor,
                                  RepositoryItemWriter<EmployeeTarget> employeeTargetWriter) {
        return stepBuilderFactory.get("sample_employeeChunkStep")
                .<EmployeeSource, EmployeeTarget>chunk(EMPLOYEE_CHUNK_SIZE)
                .reader(employeeSourceReader)
                .processor(employeeProcessor)
                .writer(employeeTargetWriter)
                .build();
    }
}
