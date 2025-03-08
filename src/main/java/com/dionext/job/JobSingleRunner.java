package com.dionext.job;

public interface JobSingleRunner extends JobRunner {
    void setJobProcessor(JobProcessor jobBatchItemProcessor);
}