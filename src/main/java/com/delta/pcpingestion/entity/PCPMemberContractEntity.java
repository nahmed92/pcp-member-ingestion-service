package com.delta.pcpingestion.entity;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.delta.pcpingestion.service.STATUS;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Contract Entity Class
 * 
 * @author ca94197
 * @since 1.0
 */
@Data
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = PCPMemberContractEntity.TABLE_NAME,  schema = "dbo")
@EnableJpaAuditing
public class PCPMemberContractEntity implements java.io.Serializable {

	/**
	 * Serialization Key
	 */
	private static final long serialVersionUID = 2757500429236458720L;

	protected static final String TABLE_NAME = "contract";
	
	protected static final String SCHEMA= "dbo";

	@Id
	@NonNull
	@Column(name = "contract_id")
	private String contractID;
	
	@NonNull
	@Column(name = "member_Id")
	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> memberId;
	
//	@NonNull
//	@Column(name = "group_number")
//	private String groupNumber;
	
	@NonNull
	@Column(name = "mtvPerson_ID")
	@ElementCollection
	private List<String> mtvPersonID;
	
//	@NonNull
//	@Column(name = "division_number")
//    private String divisionNumber;

	@NonNull
	@Column(name = "contract", length = 80000)
	private String contract;

	@NonNull
	@Column(name = "num_of_enrollee")
	private Integer numberOfEnrollee;

	@NonNull
	@Column(name = "status")
	private STATUS status;
	
	@NonNull
	@Column(name = "num_of_attempt")
	private Integer numOfAttempt;

	@Column(name = "created_date")
	@CreationTimestamp
	private Timestamp createdDate;

	@Column(name = "last_updated_date")
	@UpdateTimestamp
	private Timestamp lastUpdatedDate;

}