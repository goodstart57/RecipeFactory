package com.ljs.rfactory.batch.before.sample.job;

import com.ljs.rfactory.batch.before.sample.entity.SourceItem;
import com.ljs.rfactory.batch.before.sample.entity.TargetItem;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class SampleJobConfig {

    @Bean
    public Job sampleJob(JobBuilderFactory jobBuilderFactory,
                         Step sampleStep,
                         JobExecutionListenerSupport sampleJobExecutionListener) {
        return jobBuilderFactory.get("sampleJob")
                .listener(sampleJobExecutionListener)
                .start(sampleStep)
                .build();
    }

    @Bean
    public Step sampleStep(StepBuilderFactory stepBuilderFactory,
                           JpaPagingItemReader<SourceItem> sourceItemReader,
                           ItemProcessor<SourceItem, TargetItem> sourceItemProcessor,
                           JpaItemWriter<TargetItem> targetItemWriter) {
        return stepBuilderFactory.get("sampleStep")
                .<SourceItem, TargetItem>chunk(2)
                .reader(sourceItemReader)
                .processor(sourceItemProcessor)
                .writer(targetItemWriter)
                .build();
    }
}
