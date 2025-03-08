package com.dionext.job.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;


@Embeddable
@Getter
@Setter
public class JobBatchItemId implements Serializable {

    public JobBatchItemId() {

    }
    public JobBatchItemId(String jobId, String itemId) {
        this.jobId = jobId;
        this.itemId = itemId;
    }

    //private static final long serialVersionUID = -876068764395666653L;
    @Column(nullable = false)
    private String jobId;
    @Column(nullable = false)
    private String itemId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        JobBatchItemId entity = (JobBatchItemId) o;
        return Objects.equals(this.jobId, entity.jobId) &&
                Objects.equals(this.itemId, entity.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId, itemId);
    }
}