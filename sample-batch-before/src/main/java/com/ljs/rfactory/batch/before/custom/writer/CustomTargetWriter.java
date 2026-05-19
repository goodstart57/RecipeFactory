package com.ljs.rfactory.batch.before.custom.writer;

import com.ljs.rfactory.batch.before.sample.dto.ProcessedItemDto;
import com.ljs.rfactory.batch.before.sample.entity.SourceItem;
import com.ljs.rfactory.batch.before.sample.service.SampleItemService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CustomTargetWriter extends BaseWriter<SourceItem> {

    private final SampleItemService sampleItemService;
    private final ProcessedItemWriter processedItemWriter;

    public CustomTargetWriter(SampleItemService sampleItemService, ProcessedItemWriter processedItemWriter) {
        this.sampleItemService = sampleItemService;
        this.processedItemWriter = processedItemWriter;
    }

    @Override
    protected void run(List<? extends SourceItem> items) throws Exception {
        List<ProcessedItemDto> processedItemDtos = new ArrayList<ProcessedItemDto>();
        for (SourceItem item : items) {
            processedItemDtos.add(sampleItemService.process(item));
        }
        processedItemWriter.write(processedItemDtos);
    }
}
