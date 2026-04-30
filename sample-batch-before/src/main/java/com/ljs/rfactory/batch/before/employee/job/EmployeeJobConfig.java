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

    @Bean
    public Job sampleEmployeeJob(JobBuilderFactory jobBuilderFactory,
                           Step sampleEmployeeLoadStep,
                           Step sampleEmployeeChunkStep,
                           EmployeeJobExecutionListener employeeJobExecutionListener) {
        return jobBuilderFactory.get("sample_employee_job")
                .listener(employeeJobExecutionListener)
                .start(sampleEmployeeLoadStep)
                .next(sampleEmployeeChunkStep)
                .build();
    }

    @Bean
    public Step sampleEmployeeLoadStep(StepBuilderFactory stepBuilderFactory, EmployeeLoadTasklet employeeLoadTasklet) {
        return stepBuilderFactory.get("sample_employee_load_step")
                .tasklet(employeeLoadTasklet)
                .build();
    }

    @Bean
    public Step sampleEmployeeChunkStep(StepBuilderFactory stepBuilderFactory,
                                  RepositoryItemReader<EmployeeSource> sampleEmployeeSourceReader,
                                  ItemProcessor<EmployeeSource, EmployeeTarget> employeeProcessor,
                                  RepositoryItemWriter<EmployeeTarget> sampleEmployeeTargetWriter) {
        return stepBuilderFactory.get("sample_employee_chunk_step")
                .<EmployeeSource, EmployeeTarget>chunk(2)
                .reader(sampleEmployeeSourceReader)
                .processor(employeeProcessor)
                .writer(sampleEmployeeTargetWriter)
                .build();
    }
}
