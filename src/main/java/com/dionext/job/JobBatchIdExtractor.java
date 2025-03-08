package com.dionext.job;


import com.dionext.job.entity.JobInstance;

@FunctionalInterface
public interface JobBatchIdExtractor {
    String extractId(JobInstance jobInstance, Object item);
}
