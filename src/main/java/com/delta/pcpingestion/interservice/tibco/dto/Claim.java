package com.delta.pcpingestion.interservice.tibco.dto;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Claim {

	private String claimId;

	private String billingProviderId;

	private String billProviderSpeciality;

	@JsonFormat(pattern="MM-dd-yyyy")
	private Date receivedDate; 

	@JsonFormat(pattern="MM-dd-yyyy HH:mm:ss")
	private Date resolvedDate;

	private String serviceNumber;

	private String emergencyFlag;

	private String encounterFlag;
	
	private String claimStatus;
	
	private String stateCode;
	
	private String groupNumber;
}
