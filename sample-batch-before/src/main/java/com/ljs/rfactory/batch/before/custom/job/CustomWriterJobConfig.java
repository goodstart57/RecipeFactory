package com.ljs.rfactory.batch.before.custom.job;

import com.ljs.rfactory.batch.before.custom.writer.CustomTargetWriter;
import com.ljs.rfactory.batch.before.sample.entity.SourceItem;
import com.ljs.rfactory.batch.before.sample.listener.SampleJobExecutionListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class CustomWriterJobConfig {

    private static final int CUSTOM_WRITER_CHUNK_SIZE = 2;

    @Bean
    public Job customWriterJob(JobBuilderFactory jobBuilderFactory,
                               Step customWriterStep,
                               SampleJobExecutionListener sampleJobExecutionListener) {
        return jobBuilderFactory.get("customWriterJob")
                .listener(sampleJobExecutionListener)
                .start(customWriterStep)
                .build();
    }

    @Bean
    public Step customWriterStep(StepBuilderFactory stepBuilderFactory,
                                 JpaPagingItemReader<SourceItem> sourceItemReader,
                                 CustomTargetWriter customTargetWriter) {
        return stepBuilderFactory.get("customWriterStep")
                .<SourceItem, SourceItem>chunk(CUSTOM_WRITER_CHUNK_SIZE)
                .reader(sourceItemReader)
                .writer(customTargetWriter)
                .build();
    }
}
