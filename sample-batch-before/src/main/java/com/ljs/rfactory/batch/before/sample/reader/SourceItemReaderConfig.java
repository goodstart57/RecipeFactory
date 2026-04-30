package com.ljs.rfactory.batch.before.sample.reader;

import com.ljs.rfactory.batch.before.sample.entity.SourceItem;
import javax.persistence.EntityManagerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SourceItemReaderConfig {

    @Bean
    @StepScope
    public JpaPagingItemReader<SourceItem> sourceItemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<SourceItem>()
                .name("sourceItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select s from SourceItem s where s.status = 'READY' order by s.id")
                .pageSize(2)
                .build();
    }
}
