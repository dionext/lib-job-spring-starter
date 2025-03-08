package com.dionext.job.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Entity
@Getter
@Setter
public class JobType {
    @Column
    @Id
    String jobTypeId;

    @Column(columnDefinition="TEXT")
    String name;

}
