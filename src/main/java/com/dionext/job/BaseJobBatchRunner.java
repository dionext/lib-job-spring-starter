package com.dionext.job;


import com.dionext.job.entity.JobBatchItem;
import com.dionext.job.entity.JobInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

@Configuration
@Slf4j
public class BaseJobBatchRunner implements JobBatchRunner {

    JobBatchListMaker jobBatchListMaker;
    JobBatchIdExtractor jobBatchIdExtractor;
    JobBatchItemProcessor jobBatchItemProcessor;

    @Override
    public void setJobBatchListMaker(JobBatchListMaker jobBatchListMaker) {
        this.jobBatchListMaker = jobBatchListMaker;
    }

    @Override
    public void setJobBatchIdExtractor(JobBatchIdExtractor jobBatchIdExtractor) {
        this.jobBatchIdExtractor = jobBatchIdExtractor;
    }

    @Override
    public void setJobBatchItemProcessor(JobBatchItemProcessor jobBatchItemProcessor) {
        this.jobBatchItemProcessor = jobBatchItemProcessor;
    }

    @Override
    public void runJob(JobInstance jobInstance) {
        JobManager jobManager = jobInstance.getJobManager();
        jobInstance.setErrorBatchItemCount(0);
        //jobInstance.setSuccessBatchItemCount(0);
        log.info("Start JOB jobTypeId " + jobInstance.getJobTypeId() + " jobId " + jobInstance.getJobId());

        Collection list = jobBatchListMaker.makeList(jobInstance);
        jobInstance.initLongProgress(list.size());
        for (Object item : list) {
            if (Thread.currentThread().isInterrupted()) break;
            if (jobInstance.getJobState() == JobState.CANCELLED) break;
            String itemId = jobBatchIdExtractor.extractId(jobInstance, item);
            JobBatchItem jobBatchItem = jobManager.prepareStep(jobInstance, String.valueOf(itemId));
            try {
                if (jobBatchItem.isNewItem() || jobBatchItem.isError()) {
                    log.info("Start STEP id " + itemId + " jobTypeId " + jobInstance.getJobTypeId() + " jobId " + jobInstance.getJobId());
                    jobInstance.logger().info("Context Test Job executed step " + itemId);

                    jobBatchItemProcessor.processItem(jobInstance, item);

                    log.info("Success end STEP id " + itemId + " jobTypeId " + jobInstance.getJobTypeId() + " jobId " + jobInstance.getJobId());
                    jobManager.resultStepSuccess(jobInstance, jobBatchItem, "");
                } else {
                    log.info("Old STEP id " + itemId + " jobTypeId " + jobInstance.getJobTypeId() + " jobId " + jobInstance.getJobId());
                }
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info("Interrupted Error end STEP id " + itemId + " jobTypeId " + jobInstance.getJobTypeId() + " jobId " + jobInstance.getJobId()
                        + " Error " + e);
                jobManager.resultStepError(jobInstance, jobBatchItem, e);
                throw new RuntimeException(e);
            } catch (Exception ex) {
                log.info("Error end STEP id " + itemId + " jobTypeId " + jobInstance.getJobTypeId() + " jobId " + jobInstance.getJobId()
                        + " Error " + ex);
                jobManager.resultStepError(jobInstance, jobBatchItem, ex);
            }
            finally {
                jobInstance.incrementProgress();
            }
        }
        log.info("End JOB jobTypeId " + jobInstance.getJobTypeId() + " jobId " + jobInstance.getJobId());
    }

}
