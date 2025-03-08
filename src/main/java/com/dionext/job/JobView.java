package com.dionext.job;

import com.dionext.job.entity.JobInstance;
import com.dionext.job.entity.JobType;
import org.springframework.http.HttpHeaders;

import java.text.MessageFormat;
import java.util.Collection;

public class JobView {
    static public String makeJobList(JobManager jobManager) {
        StringBuilder str = new StringBuilder();
        str.append("""
                <h1>Job list</h1>
                <ul>
                """);
        Collection<JobType> jobTypeList = jobManager.getJobTypeList();
        for (JobType jobType : jobTypeList) {
            str.append(MessageFormat.format("""
                        <li><a href="/job/info?job-type-id={0}">{1}<a></li>
                    """, jobType.getJobTypeId(), jobType.getName()));
            Collection<JobInstance> jobInstances = jobManager.getJobInstances(jobType.getJobTypeId());
            if (!jobInstances.isEmpty()) {
                str.append("<ul>");
                for (JobInstance jobInstance : jobInstances) {
                    str.append(MessageFormat.format("""
                                <li><a href="/job/info?job-id={0}">{0}<a> {1} {2}</li>
                            """, jobInstance.getJobId(),
                            jobInstance.getJobState(),
                            jobInstance.getFinishedTime()));
                    //str.append(MessageFormat.format("""
                    //          <a href="/job/cancel?job-id={0}"> [Cancel]<a></li>
                    //    """, jobInstance.getJobId()));
                    str.append("</li>");
                }
                str.append("</ul>");
            }
        }
        str.append("""
                <ul>
                """);
        return str.toString();
    }

    static public String makeJobInfoBlock(JobManager jobManager, JobService jobService,  String jobTypeId, String jobId) {
        JobInstance jobInstance = null;
        //JobType jobType = null;
        if (jobId != null) {
            jobInstance = jobManager.getJobInstance(jobId);
            if (jobTypeId == null) jobTypeId = jobInstance.getJobTypeId();
        }
        //if (jobTypeId != null) jobType = jobManager.getJobType(jobTypeId);

        StringBuilder str = new StringBuilder();
        str.append("<div>");
        str.append("<h2>Job</h2>");
        //job block
        str.append(makeJobBlock(jobManager, jobService, jobTypeId, jobInstance));
        //footer
        str.append("</div>");
        return str.toString();
    }

    static public  String makeJobBlock(JobManager jobManager, JobService jobService, String jobTypeId, JobInstance jobInstance) {
        StringBuilder str = new StringBuilder();
        //div
        if (jobInstance != null && jobInstance.getJobState() == JobState.RUNNING) {
            str.append(MessageFormat.format("""
                      <div hx-trigger="done" hx-get="/job/completed?job-id={0}&job-type-id={1}" hx-swap="outerHTML" hx-target="this">
                    """, jobInstance.getJobId(), jobInstance.getJobTypeId()));
        }
        else {
            str.append("""
                    <div hx-target="this" hx-swap="outerHTML">
                    """);
        }
        //title
        JobType jobType = jobInstance != null ? jobManager.getJobType(jobInstance.getJobTypeId()) : jobManager.getJobType(jobTypeId);
        str.append(MessageFormat.format("""
                    <p>Job type: {0} (jobTypeId: {1})</p>
                    """, jobType.getName(), jobType.getJobTypeId()
        ));
        if (jobInstance != null) {
            str.append(MessageFormat.format("""
                    <h3>Last run</h3>
                    <table>
                    <tr><td>Job Instance</td><td> {0} </td></tr>
                    <tr><td>Job State</td><td> {1} </td></tr>
                    <tr><td>Created time</td><td> {2} </td></tr>
                    <tr><td>Finished time</td><td> {3} </td></tr>
                    <tr><td>Success BatchItem Count</td><td> {4} </td></tr>
                    <tr><td>Error BatchItem Count</td><td> {5} </td></tr>
                    </table>
                """,
                    jobInstance.getJobId(),
                    jobInstance.getJobState(),
                    jobInstance.getCreatedTime(),
                    jobInstance.getFinishedTime(),
                    jobInstance.getSuccessBatchItemCount(),
                    jobInstance.getErrorBatchItemCount()

            ));
            str.append("<h3>Last run parameters</h3>");
            str.append(JobView.createRunJobParametersHeader(true));
            str.append(jobService.createRunJobParameters(jobTypeId, jobInstance, true));
            str.append(JobView.createRunJobParametersFooter(true));


        }
        //progress
        if (jobInstance != null
                && jobInstance.getJobState() == JobState.RUNNING) {
            str.append("<h3>Running...</h3>");
            str.append(MessageFormat.format("""
                      <div
                        hx-get="/job/progress?job-id={0}"
                        hx-trigger="every 600ms"
                        hx-target="this"
                        hx-swap="innerHTML">
                        {1}
                      </div>
                    """, jobInstance.getJobId(),
                    makeProgressJobBlock(jobInstance)
            ));
        }
        //buttons

        str.append("<div>");
        if (jobInstance == null){
            str.append(MessageFormat.format("""
            <form hx-post="/job/run?job-type-id={0}" hx-swap="outerHTML">
                    """, jobTypeId
            ));
            str.append("<h3>Run...</h3>");
            str.append(JobView.createRunJobParametersHeader(true));
            str.append(jobService.createRunJobParameters(jobTypeId, jobInstance, false));
            str.append(JobView.createRunJobParametersFooter(true));
            str.append(MessageFormat.format("""
              <button class="btn btn-primary">Start Job</button>
            </form>                    
                    """, jobTypeId
            ));
        }
        else {
            if (jobInstance.getJobState() == JobState.RUNNING) {
                //cancel button
                str.append(MessageFormat.format("""
                          <button class="btn btn-primary" hx-post="/job/cancel?job-id={0}"">
                             Cancel Job
                          </button>
                        """,  jobInstance.getJobId()
                ));
            } else if (jobInstance.getJobState() == JobState.SUCCESS
                    || jobInstance.getJobState() == JobState.FAILED
                    || jobInstance.getJobState() == JobState.CANCELLED
            ) {
                str.append(MessageFormat.format("""
            <form hx-post="/job/run?job-type-id={0}&job-id={1}"  hx-swap="outerHTML">
                    """, jobInstance.getJobTypeId(), jobInstance.getJobId()
                ));
                str.append("<h3>Run...</h3>");
                str.append(JobView.createRunJobParametersHeader(false));
                str.append(jobService.createRunJobParameters(jobTypeId, jobInstance, false));
                str.append(JobView.createRunJobParametersFooter(false));
                str.append(MessageFormat.format("""
              <button class="btn btn-primary">Restart Job</button>
            </form>                    
                    """, jobInstance.getJobTypeId(), jobInstance.getJobId()
                ));
            }
        }
        str.append("</div>");
        str.append("""
             <p><a href="/admin/job/index">[Job list]</a></p>
                 """);

        // /div
        str.append("</div>");

        return str.toString();
    }
    static public String makeProgressJobBlock(JobInstance jobInstance) {
        StringBuilder str = new StringBuilder();
        if (jobInstance.getSuccessBatchItemCount() > 0 || jobInstance.getErrorBatchItemCount() > 0){
            str.append(MessageFormat.format("""
            <p>Current progress: {0}%, success processed {1} items, with errors {2}</p> 
            """, jobInstance.getProgress(), jobInstance.getSuccessBatchItemCount(),
                    jobInstance.getErrorBatchItemCount()));
        }
        str.append(MessageFormat.format("""
                <div class="progress" role="progressbar" aria-valuemin="0" aria-valuemax="100" aria-valuenow="{0}" aria-labelledby="pblabel">
                    <div id="pb" class="progress-bar" style="width:{0}%">
                    </div>
                </div>
                """, jobInstance.getProgress()));
        return str.toString();
    }
    public static HttpHeaders makeJobProgressHeaders(JobInstance jobInstance) {
        HttpHeaders responseHeaders = new HttpHeaders();
        int progress = 0;
        if (jobInstance != null) {
            progress = jobInstance.getProgress();
        }
        if (progress >= 100
                || jobInstance != null && jobInstance.getJobState() == JobState.FAILED
                || jobInstance != null && jobInstance.getJobState() == JobState.SUCCESS
                || jobInstance != null && jobInstance.getJobState() == JobState.CANCELLED
        ) {
            responseHeaders.set("HX-Trigger", "done");//for end
        }
        return responseHeaders;
    }
    public static String makeCanceledJobResult(JobInstance jobInstance) {
        String str = MessageFormat.format("""
                <p><b>Canceled job {0}</b></p>
                <p><a href="/admin/job/index">Job list</a></p>
                """, jobInstance.getJobTypeId());
        return str;
    }
    static public String createRunJobParametersHeader(boolean readOnly) {
        return "<table>";
    }
    static public String createRunJobParametersFooter(boolean readOnly) {
        return "</table>";
    }
    static public String createRunJobParameter(String name, String description,  String value, boolean readOnly) {
        if (description == null) description = name;
        StringBuilder str = new StringBuilder();
        if (readOnly){
            //str.append(MessageFormat.format("""
              //      <div>
                //      <label>{1}</label>: {2}
                  //  </div>
                    //""", name, description, value));
            str.append(MessageFormat.format("""
                    <tr><td>{1}</td><td>{2}</td></tr>
                    """, name, description, value));

        }
        else {
            //str.append(MessageFormat.format("""
             //       <div>
              //        <label>{1}</label>
              //        <input type="text" name="{0}" value="{2}">
              //      </div>
              //      """, name, description, value));
            str.append(MessageFormat.format("""
                    <tr><td>{1}</td><td><input type="text" name="{0}" value="{2}"></td></tr>
                    """, name, description, value));
        }
        return str.toString();
    }

}
