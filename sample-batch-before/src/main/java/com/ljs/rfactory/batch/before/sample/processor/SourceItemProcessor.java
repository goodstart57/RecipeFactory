package com.ljs.rfactory.batch.before.sample.processor;

import com.ljs.rfactory.batch.before.sample.dto.ProcessedItemDto;
import com.ljs.rfactory.batch.before.sample.entity.SourceItem;
import com.ljs.rfactory.batch.before.sample.entity.TargetItem;
import com.ljs.rfactory.batch.before.sample.service.SampleItemService;
import java.time.LocalDateTime;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class SourceItemProcessor implements ItemProcessor<SourceItem, TargetItem> {

    private final SampleItemService sampleItemService;

    public SourceItemProcessor(SampleItemService sampleItemService) {
        this.sampleItemService = sampleItemService;
    }

    @Override
    public TargetItem process(SourceItem item) {
        ProcessedItemDto dto = sampleItemService.process(item);
        TargetItem targetItem = new TargetItem();
        targetItem.setSourceId(dto.getSourceId());
        targetItem.setProcessedName(dto.getProcessedName());
        targetItem.setProcessedAt(LocalDateTime.now());
        return targetItem;
    }
}
