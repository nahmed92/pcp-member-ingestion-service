package com.delta.pcpingestion.dto;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Claim Class
 * 
 * @author ca94197
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Claim {

	private String claimId;

	private String billingProviderId;

	private String billProviderSpeciality;

	@JsonFormat(pattern="dd-MMM-yy")
	private Date receivedDate;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date resolvedDate;

	private String serviceNumber;

	private String emergencyFlag;

	private String encounterFlag;
	
	private String claimStatus;
	
	private String stateCode;
	
	private String securityGroupId;
}
