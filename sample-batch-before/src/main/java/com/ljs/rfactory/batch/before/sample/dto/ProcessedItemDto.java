package com.ljs.rfactory.batch.before.sample.dto;

public class ProcessedItemDto {

    private final Long sourceId;
    private final String processedName;

    public ProcessedItemDto(Long sourceId, String processedName) {
        this.sourceId = sourceId;
        this.processedName = processedName;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public String getProcessedName() {
        return processedName;
    }
}
