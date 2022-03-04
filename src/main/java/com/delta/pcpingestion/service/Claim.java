package com.delta.pcpingestion.service;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.transaction.Transactional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name="Claim")
@Embeddable
public class Claim {
	
	private String claimId;

	private String billingProviderId;

	private String billProviderSpeciality;

	private String receivedDate;
	
	private String resolvedDate;

	private String serviceNumber;
	
	private String emergencyFlag;

	private String encounterFlag;
	
	private String claimStatus;
	
	private String stateCode;
	
	private String securityGroupId;
}
