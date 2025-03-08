package com.dionext.job.entity;

import com.dionext.job.JobLogger;
import com.dionext.job.JobManager;
import com.dionext.job.JobState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
public class JobInstance {
    @Column
    @Id
    String jobId;
    @Column
    String jobTypeId;

    @Column
    @Enumerated(EnumType.STRING)
    JobState jobState;

    @Column(columnDefinition="TEXT")
    String result;
    @Column(columnDefinition="TEXT")
    String error;

    @Column
    long successBatchItemCount;
    @Column
    long errorBatchItemCount;
    @Column
    private int progress;
    @Column
    private LocalDateTime createdTime;
    @Column
    private LocalDateTime finishedTime;
    @Column(columnDefinition="TEXT")
    private String parameters;

    @Transient
    private Thread thread;
    //@Transient
    //private JobContext jobContext;
    @Transient
    private JobManager jobManager;
    @Transient
    long totalAmount = 100;
    @Transient
    long longProgress;

    @Transient
    JobLogger logger;
    @Transient
    private Map<String, String> parametersMap = null;

    public String getParameter(String key){
        loadJobContext();
        return parametersMap.get(key);
    }
    public String putParameter(String key, String value){
        loadJobContext();
        String v = parametersMap.put(key, value);
        saveJobContext();
        return v;
    }
    public String removeParameter(String key){
        loadJobContext();
        String v = parametersMap.remove(key);
        saveJobContext();
        return v;
    }

    public JobLogger logger() {
        if (logger == null) {
            logger = new JobLogger();
        }
        return logger;
    }

    public void setJobError(Throwable ex){
        error = ex.toString();
    }
    public void initLongProgress(long totalAmount){
        this.totalAmount = totalAmount;
        this.longProgress = 0;
        this.progress = calculateProgress(this.longProgress, this.totalAmount);
    }
    static private int calculateProgress(long longProgress, long totalAmount){
        int progressPercent = (int)(((double)longProgress/(double)totalAmount)*100.0);
        return progressPercent;
    }

    public void setLongProgress(long longProgress){
        this.longProgress = longProgress;
        this.progress = calculateProgress(this.longProgress, this.totalAmount);
    }
    public void incrementProgress(){
        this.longProgress += 1;
        this.progress = calculateProgress(this.longProgress, this.totalAmount);
    }
    /*
    public int getProgress(){
        return progress;
    }

    public JobState getJobState(){
        return jobState;
    }

     */

    private void loadJobContext()  {
        //Note! Alternative solution @PostLoad @PreUpdate - not working well
        if (parametersMap == null) {
            parametersMap = new HashMap<>();
            if (parameters != null) {
                TypeReference<HashMap<String, String>> typeRef
                        = new TypeReference<HashMap<String, String>>() {
                };
                ObjectMapper mapper = new ObjectMapper();
                Map<String, String> map = null;
                try {
                    map = mapper.readValue(parameters, typeRef);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                if (map != null && map.size() > 0) {
                    for (var e : map.entrySet()) {
                        parametersMap.put(e.getKey(), e.getValue());
                    }
                }
            }
        }

    }
    private void saveJobContext()  {
        if (parametersMap.size() > 0) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                parameters = mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(parametersMap);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        else parameters = null;
    }
    /// /////////////////////////////////////////

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobTypeId() {
        return jobTypeId;
    }

    public void setJobTypeId(String jobTypeId) {
        this.jobTypeId = jobTypeId;
    }

    public JobState getJobState() {
        return jobState;
    }

    public void setJobState(JobState jobState) {
        this.jobState = jobState;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public long getSuccessBatchItemCount() {
        return successBatchItemCount;
    }

    public void setSuccessBatchItemCount(long successBatchItemCount) {
        this.successBatchItemCount = successBatchItemCount;
    }

    public long getErrorBatchItemCount() {
        return errorBatchItemCount;
    }

    public void setErrorBatchItemCount(long errorBatchItemCount) {
        this.errorBatchItemCount = errorBatchItemCount;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(LocalDateTime finishedTime) {
        this.finishedTime = finishedTime;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    public void setJobManager(JobManager jobManager) {
        this.jobManager = jobManager;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getLongProgress() {
        return longProgress;
    }
}
