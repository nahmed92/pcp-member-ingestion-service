package com.delta.pcpingestion.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.delta.pcpingestion.enums.ControlStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Transactional
@ToString
@Table(name = "ingestion_controller", schema = "dbo")
@EnableJpaAuditing
public class IngestionControllerEntity implements Serializable {


	private static final long serialVersionUID = 706822384833879511L;

	@Id
    @Column(name = "id")
    private String id;

    @Column(name = "run_id")
    private String runId;

    @Column(name = "run_timestamp")
    private Timestamp runTimestamp;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ControlStatus status;

    @Column(name = "states")
    private String states;

    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "last_updated_at")
    @UpdateTimestamp
    private Timestamp lastUpdatedAt;

    @Column(name = "no_of_contracts")
    private int noOfContracts;
    
    @Column(name = "no_of_days")
    private int noOfDays;
    
    @Column(name = "cut_off_date")
    private Date cutOffDate;
    

    @Column(name = "service_instance_id")
    private String serviceInstanceId;
}
