package com.delta.pcpingestion.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.transaction.Transactional;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.delta.pcpingestion.enums.PublishStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Transactional
@Table(name = "contract", schema = "dbo")
@EnableJpaAuditing
public class ContractEntity implements java.io.Serializable {

	private static final long serialVersionUID = 2757500429236458720L;

	@Id
	@Column(name = "id")
	private String id;

	@NonNull
	@Column(name = "contract_json")
	private String contractJson;

	@Column(name = "mtv_person_ids")
	private String mtvPersonIds;
	
	
	@Column(name = "claims_ids")
	private String claimIds;

	@Column(name = "state_codes")
	private String stateCodes;

	@Column(name = "v_contract_id")
	private String contractId;

	@NonNull
	@Column(name = "num_of_enrollee")
	private Integer numberOfEnrollee;

	@NonNull
	@Column(name = "publish_status")
	@Enumerated(EnumType.STRING)
	private PublishStatus publishStatus;

	@NonNull
	@Column(name = "num_of_retries")
	private Integer numOfRetries;

	@Column(name = "created_at")
	@CreationTimestamp
	private Timestamp createdAt;

	@Column(name = "last_updated_at")
	@UpdateTimestamp
	private Timestamp lastUpdatedAt;

}
