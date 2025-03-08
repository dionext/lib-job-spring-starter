package com.dionext.job;


import com.dionext.job.entity.JobInstance;

@FunctionalInterface
public interface JobProcessor {
    void processJob(JobInstance jobInstance);
}
