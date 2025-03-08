package com.dionext.job;

import com.dionext.job.entity.JobInstance;

public interface JobRunner {
    void runJob(JobInstance jobInstance) throws InterruptedException;
}