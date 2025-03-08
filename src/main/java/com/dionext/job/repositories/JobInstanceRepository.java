package com.dionext.job.repositories;


import com.dionext.job.entity.JobInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface JobInstanceRepository extends JpaRepository<JobInstance, String> {
    Collection<JobInstance> findByJobTypeId(String jobTypeId);
}