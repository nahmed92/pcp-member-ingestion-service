package com.delta.pcpingestion.entity;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.transaction.Transactional;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.delta.pcpingestion.service.STATUS;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;


/**
 * Contract Entity Class
 * 
 * @author ca94197
 * @since 1.0
 */
@Data
@Builder
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Transactional
@Table(name = PCPMemberContract.TABLE_NAME,  schema = "dbo")
@EnableJpaAuditing
public class PCPMemberContract implements java.io.Serializable {

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
	@CollectionTable(name="contract_memberId")
	private Set<String> memberId;
	
	@NonNull
	@Column(name = "mtvPerson_ID")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="contract_mtvPersonID")
	private Set<String> mtvPersonID;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="contract_claim")
	private List<Claim> claim;
	 
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

	@Column(name= "created_date", updatable = false)
	@CreationTimestamp
	private Timestamp createdDate;

	@Column(name = "last_updated_date")
	@UpdateTimestamp
	private Timestamp lastUpdatedDate;

}