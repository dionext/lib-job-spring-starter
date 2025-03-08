package com.dionext.job;


import com.dionext.job.entity.JobInstance;

@FunctionalInterface
public interface JobBatchItemProcessor {
    void processItem(JobInstance jobInstance, Object item);
}
