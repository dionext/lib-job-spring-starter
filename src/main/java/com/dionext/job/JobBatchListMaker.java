package com.dionext.job;

import com.dionext.job.entity.JobInstance;

import java.util.Collection;

@FunctionalInterface
public interface JobBatchListMaker {
    Collection<?> makeList(JobInstance jobInstance);
}
