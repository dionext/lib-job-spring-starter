package com.dionext.job;

public interface JobBatchRunner extends JobRunner {
    void setJobBatchListMaker(JobBatchListMaker jobBatchListMaker);

    void setJobBatchIdExtractor(JobBatchIdExtractor jobBatchIdExtractor);

    void setJobBatchItemProcessor(JobBatchItemProcessor jobBatchItemProcessor);
}