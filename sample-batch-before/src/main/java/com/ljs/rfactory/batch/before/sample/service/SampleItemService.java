package com.ljs.rfactory.batch.before.sample.service;

import com.ljs.rfactory.batch.before.sample.dto.ProcessedItemDto;
import com.ljs.rfactory.batch.before.sample.entity.SourceItem;
import org.springframework.stereotype.Service;

@Service
public class SampleItemService {

    public ProcessedItemDto process(SourceItem sourceItem) {
        String processedName = sourceItem.getName().trim().toUpperCase() + "-PROCESSED";
        return new ProcessedItemDto(sourceItem.getId(), processedName);
    }
}
