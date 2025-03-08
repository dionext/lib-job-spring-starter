package com.dionext.job;

import com.dionext.job.entity.JobBatchItem;
import com.dionext.job.entity.JobBatchItemId;
import com.dionext.job.entity.JobInstance;
import com.dionext.job.entity.JobType;
import com.dionext.job.repositories.JobBatchItemRepository;
import com.dionext.job.repositories.JobInstanceRepository;
import com.dionext.job.repositories.JobTypeRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class JobManager {
    public static final String JOB_TYPE_ID = "job-type-id";
    public static final String JOB_ID = "job-id";
    private ConcurrentHashMap<String, JobInstance> jobs = new ConcurrentHashMap<>();

    @Autowired
    private JobBatchItemRepository jobBatchItemRepository;
    @Autowired
    private JobInstanceRepository jobInstanceRepository;
    @Autowired
    private JobTypeRepository jobTypeRepository;

    @PostConstruct
    void postConstruct() {
       // Collection<JobInstance> jobsFromDb = jobInstanceRepository.findAll();
        //todo resume jobs

    }

    public JobType addJobType(String jobTypeId, String name){
        JobType jobType = jobTypeRepository.findById(jobTypeId).orElse(null);
        if (jobType == null){
            jobType = new JobType();
            jobType.setJobTypeId(jobTypeId);
            jobType.setName(name);
            jobTypeRepository.save(jobType);
        }
        return jobType;
    }

    public JobInstance preRegisterJob(String jobTypeId, String jobId,
                                      Map<String, String> requestParameters){
        JobInstance jobInstance = null;
        if (jobId != null){
            jobInstance = jobInstanceRepository.findById(jobId).orElse(null);
        }
        if (jobInstance == null)
        {
            jobId = UUID.randomUUID().toString();
            addJobType(jobTypeId, jobTypeId);

            jobInstance = new JobInstance();
            jobInstance.setJobTypeId(jobTypeId);
            jobInstance.setJobId(jobId);
        }
        jobInstance.setJobManager(this);
        jobInstance.setCreatedTime(LocalDateTime.now());
        jobInstance.setJobState(JobState.PLANNED);
        if (requestParameters != null)
            fillParameters(jobInstance, requestParameters);

        jobs.put(jobId, jobInstance);
        jobInstanceRepository.save(jobInstance);
        return jobInstance;
    }

    private void fillParameters(JobInstance jobInstance, Map<String, String> requestParameters) {
        for(Map.Entry<String, String> e : requestParameters.entrySet()){
            if (!JOB_TYPE_ID.equals(e.getKey()) && !JOB_ID.equals(e.getKey())){
                jobInstance.putParameter(e.getKey(), e.getValue());
            }
        }
    }

    public void postRegisterJob(JobInstance jobInstance, Thread thread){
        jobInstance.setJobState(JobState.RUNNING);
        jobInstance.setThread(thread);
        jobInstanceRepository.save(jobInstance);
    }
    public void resultJobSuccess(JobInstance jobInstance){
        jobInstance.setJobState(JobState.SUCCESS);
        jobInstance.setFinishedTime(LocalDateTime.now());
        jobInstanceRepository.save(jobInstance);
    }
    public void resultJobCanceled(JobInstance jobInstance){
        jobInstance.setJobState(JobState.CANCELLED);
        jobInstance.setFinishedTime(LocalDateTime.now());
        jobInstanceRepository.save(jobInstance);
    }
    public void resultJobError(JobInstance jobInstance, Throwable ex){
        jobInstance.setJobState(JobState.FAILED);
        jobInstance.setJobError(ex);
        jobInstance.setFinishedTime(LocalDateTime.now());
        jobInstanceRepository.save(jobInstance);
    }
    public JobInstance getJobInstance(String jobId){
        JobInstance jobInstance = jobs.get(jobId);
        if (jobInstance == null) {
            jobInstance = jobInstanceRepository.findById(jobId).orElse(null);
            if (jobInstance != null) {
                jobInstance.setJobManager(this);
                jobs.put(jobId, jobInstance);
            }
        }
        return jobInstance;
    }
    public JobType getJobType(String jobTypeId){
        return jobTypeRepository.findById(jobTypeId).orElse(null);
    }
    public boolean cancelJob(String jobId){
        JobInstance jobInstance = jobs.get(jobId);
        if (jobInstance != null){
            //boolean res = jobInstance.getFuture().cancel(true);
            //jobInstance.getThread().interrupt();
            //jobInstance.getThread().stop();

            //todo wait for stop
            //jobs.remove(jobId);
            jobInstance.setJobState(JobState.CANCELLED);
            jobInstanceRepository.save(jobInstance);
            return true;
        }
        else return false;
    }

    //batch
    public JobBatchItem prepareStep(JobInstance jobInstance, String itemId){
        JobBatchItem jobBatchItem = jobBatchItemRepository.findById(new JobBatchItemId(jobInstance.getJobId(), itemId)).orElse(null);
        if (jobBatchItem == null){
            jobBatchItem = new JobBatchItem(jobInstance.getJobId(), itemId);
            jobBatchItem.setCreatedTime(LocalDateTime.now());
        }
        return jobBatchItem;
    }
    public void resultStepSuccess(JobInstance jobInstance, JobBatchItem jobBatchItem, String result){
        jobBatchItem.setError(false);
        jobBatchItem.setResult(result);
        jobBatchItem.setFinishedTime(LocalDateTime.now());
        jobInstance.setSuccessBatchItemCount(jobInstance.getSuccessBatchItemCount() + 1);
        jobBatchItemRepository.save(jobBatchItem);
        jobInstanceRepository.save(jobInstance);
    }
    public void resultStepError(JobInstance jobInstance, JobBatchItem jobBatchItem, Throwable ex){
        jobBatchItem.setError(true);
        jobBatchItem.setResult(ex.toString());
        jobBatchItem.setFinishedTime(LocalDateTime.now());
        jobInstance.setErrorBatchItemCount(jobInstance.getErrorBatchItemCount() + 1);
        jobBatchItemRepository.save(jobBatchItem);
        jobInstanceRepository.save(jobInstance);
    }

    public Collection<JobInstance> getJobInstances(String jobTypeId){
        return jobInstanceRepository.findByJobTypeId(jobTypeId);
    }
    public Collection<JobType> getJobTypeList(){
        return jobTypeRepository.findAll();
    }

    public void executeJob(JobInstance jobInstance, JobRunner jobRunner) throws InterruptedException {
        JobManager jobManager = this;
        Runnable basic = () ->
        {
            String threadName = Thread.currentThread().getName();
            log.info("Running job task by "  + threadName);
            try {
                jobRunner.runJob(jobInstance);
                if (jobInstance.getJobState() == JobState.CANCELLED)
                    jobManager.resultJobCanceled(jobInstance);
                else
                    jobManager.resultJobSuccess(jobInstance);
            } catch (Exception ex) {
                jobManager.resultJobError(jobInstance, ex);//todo full stack trace
                throw new RuntimeException(ex);
            }
        };

        // Instantiating two thread classes
        Thread thread = new Thread(basic);
        jobManager.postRegisterJob(jobInstance, thread);
        // Running two threads for the same task
        thread.start();
    }


}

