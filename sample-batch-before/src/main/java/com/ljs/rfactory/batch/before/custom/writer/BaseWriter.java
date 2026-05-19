package com.ljs.rfactory.batch.before.custom.writer;

import java.util.List;
import org.springframework.batch.item.ItemWriter;

public abstract class BaseWriter<T> implements ItemWriter<T> {

    @Override
    public void write(List<? extends T> items) throws Exception {
        run(items);
    }

    protected abstract void run(List<? extends T> items) throws Exception;
}
