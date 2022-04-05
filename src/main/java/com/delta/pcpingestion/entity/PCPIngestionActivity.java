package com.delta.pcpingestion.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.transaction.Transactional;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Data
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Transactional
@Table(name = PCPIngestionActivity.TABLE_NAME,  schema = "dbo")
@EnableJpaAuditing
public class PCPIngestionActivity implements java.io.Serializable{
	
	/**
	 * Serialization Key
	 */
	private static final long serialVersionUID = 7059820753864814432L;

	protected static final String TABLE_NAME = "pcpingestionactivity";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, unique = true)
	private Integer id;

	@NonNull
	@Column(name = "state")
	private String state;
	
	@NonNull
	@Column(name = "num_of_contract")
	private Integer numOfContract = 0;
	
	@Column(name = "num_of_claim")
	private Integer numOfClaims = 0;
	
	@NonNull
	@Column(name = "num_claim_send_to_calculation")
	private Integer numClaimSendToCalculation = 0;

	@NonNull
	@Column(name = "staged_period_in_seconds")
	private Long tibcoDataStagePeriod;
	
	@Column(name= "created_date", updatable = false)
	@CreationTimestamp
	private Timestamp createdDate;

	@Column(name = "last_updated_date")
	@UpdateTimestamp
	private Timestamp lastUpdatedDate;

}
