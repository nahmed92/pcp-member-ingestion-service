package com.delta.pcpingestion.entity;

import com.delta.pcpingestion.enums.PublishStatus;
import com.delta.pcpingestion.enums.RevisionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.sql.Timestamp;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Transactional
@Table(name = "contract_audit", schema = "dbo")
public class ContractAuditEntity implements java.io.Serializable {

	private static final long serialVersionUID = 2757500429236458720L;

	@EmbeddedId
	private ContractAuditPK contractAuditPK;

	@Column(name = "revision_type")
	@Enumerated(EnumType.STRING)
	private RevisionType revisionType;

	@Column(name = "audit_timestamp")
	@CreationTimestamp
	private Timestamp auditTimestamp;

	@Column(name = "contract_json")
	private String contractJson;

	@Column(name = "mtv_person_ids")
	private String mtvPersonIds;

	@Column(name = "claims_ids")
	private String claimIds;

	@Column(name = "state_codes")
	private String stateCodes;

	@Column(name = "contract_id")
	private String contractId;

	@Column(name = "num_of_enrollee")
	private Integer numberOfEnrollee;

	@Column(name = "publish_status")
	@Enumerated(EnumType.STRING)
	private PublishStatus publishStatus;

	@Column(name = "num_of_retries")
	private Integer numOfRetries;

	@Column(name = "created_at")
	@CreationTimestamp
	private Timestamp createdAt;

	@Column(name = "last_updated_at")
	@UpdateTimestamp
	private Timestamp lastUpdatedAt;

}
