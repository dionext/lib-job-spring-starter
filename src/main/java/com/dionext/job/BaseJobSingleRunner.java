package com.dionext.job;


import com.dionext.job.entity.JobInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class BaseJobSingleRunner implements JobSingleRunner {

    JobProcessor jobProcessor;

    @Override
    public void setJobProcessor(JobProcessor jobProcessor) {
        this.jobProcessor = jobProcessor;
    }

    @Override
    public void runJob(JobInstance jobInstance) throws InterruptedException {
        jobProcessor.processJob(jobInstance);
    }


}
