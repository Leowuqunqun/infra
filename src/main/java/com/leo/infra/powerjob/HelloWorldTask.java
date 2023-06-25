package com.leo.infra.powerjob;

import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

@Component
public class HelloWorldTask implements BasicProcessor {

    @Override
    public ProcessResult process(TaskContext taskContext) throws Exception {
        return new ProcessResult(true,"hello world");
    }
}
