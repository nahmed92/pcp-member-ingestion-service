package com.delta.pcpingestion.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Transactional
@Table(name = "ingestion_stats", schema = "dbo")
@EnableJpaAuditing
public class IngestionStatsEntity implements Serializable {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "run_id")
    private String runId;

    @Column(name = "state")
    private String state;

    @Column(name = "no_of_contracts")
    private int noOfContracts;

    @Column(name = "start_time")
    private Timestamp startTime;

    @Column(name = "end_time")
    private Timestamp endTime;

    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "last_updated_at")
    @UpdateTimestamp
    private Timestamp lastUpdatedAt;

    @Column(name = "service_instance_id")
    private String serviceInstanceId;
}
