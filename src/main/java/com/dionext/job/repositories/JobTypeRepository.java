package com.dionext.job.repositories;


import com.dionext.job.entity.JobType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobTypeRepository extends JpaRepository<JobType, String> {
}