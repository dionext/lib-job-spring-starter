package com.dionext.job.repositories;

import com.dionext.job.entity.JobBatchItem;
import com.dionext.job.entity.JobBatchItemId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobBatchItemRepository extends JpaRepository<JobBatchItem, JobBatchItemId> {
}