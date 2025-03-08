package com.dionext.job.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class JobBatchItem {
    @EmbeddedId
    private JobBatchItemId id;

    //@Column(columnDefinition="TEXT")
    //private String jobTypeId;
    //@Column(columnDefinition="TEXT")
    //private String itemId;
    //@Column(columnDefinition="TEXT")
    private String result;
    @Column
    boolean error;
    @Column
    private LocalDateTime createdTime;
    @Column
    private LocalDateTime finishedTime;
    @Transient
    private boolean newItem = false;

    public JobBatchItem(){

    }
    public JobBatchItem(String jobTypeId, String itemId) {
        //this.jobTypeId = jobTypeId;
        //this.itemId = itemId;
        id = new JobBatchItemId(jobTypeId, itemId);
        newItem = true;
    }
}
