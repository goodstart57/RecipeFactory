package com.ljs.rfactory.batch.before.sample.writer;

import com.ljs.rfactory.batch.before.sample.entity.TargetItem;
import javax.persistence.EntityManagerFactory;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TargetItemWriterConfig {

    @Bean
    public JpaItemWriter<TargetItem> targetItemWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<TargetItem>()
                .entityManagerFactory(entityManagerFactory)
                .usePersist(true)
                .build();
    }
}
