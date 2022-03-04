package com.delta.pcpingestion.dto;

import lombok.Data;

/**
 * Claim Class
 * 
 * @author ca94197
 * @since 1.0
 */
@Data
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
