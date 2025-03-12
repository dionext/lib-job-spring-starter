package com.dionext.job.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "jobtype")
public class JobType {
    @Column
    @Id
    String jobTypeId;

    @Column(columnDefinition="TEXT")
    String name;

}
