package com.ljs.rfactory.batch.before.sample.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
public class SampleJobExecutionListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(SampleJobExecutionListener.class);

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("sampleJob completed with status {}", jobExecution.getStatus());
        }
    }
}
