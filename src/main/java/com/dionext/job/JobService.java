package com.dionext.job;

import com.dionext.job.entity.JobInstance;

public interface JobService {
    String createRunJobParameters(String jobTypeId, JobInstance jobInstance, boolean readOnly);
}
