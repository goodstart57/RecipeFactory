package com.ljs.rfactory.batch.before.custom.writer;

import com.ljs.rfactory.batch.before.sample.dto.ProcessedItemDto;
import com.ljs.rfactory.batch.before.sample.entity.TargetItem;
import com.ljs.rfactory.batch.before.sample.repository.TargetItemRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProcessedItemWriter extends BaseWriter<ProcessedItemDto> {

    private final TargetItemRepository targetItemRepository;

    public ProcessedItemWriter(TargetItemRepository targetItemRepository) {
        this.targetItemRepository = targetItemRepository;
    }

    @Override
    protected void run(List<? extends ProcessedItemDto> items) {
        List<TargetItem> targetItems = new ArrayList<TargetItem>();
        for (ProcessedItemDto item : items) {
            TargetItem targetItem = new TargetItem();
            targetItem.setSourceId(item.getSourceId());
            targetItem.setProcessedName(item.getProcessedName());
            targetItem.setProcessedAt(LocalDateTime.now());
            targetItems.add(targetItem);
        }
        targetItemRepository.saveAll(targetItems);
    }
}
