package com.ljs.rfactory.samplebatchbefore.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class SampleBatchBeforeJobConfig {

    @Bean
    public Job sampleJob(JobBuilderFactory jobBuilderFactory, Step sampleStep) {
        return jobBuilderFactory.get("sampleJob")
                .start(sampleStep)
                .build();
    }

    @Bean
    public Step sampleStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("sampleStep")
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
                .build();
    }
}
