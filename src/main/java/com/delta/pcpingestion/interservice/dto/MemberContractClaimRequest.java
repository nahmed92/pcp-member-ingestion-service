package com.delta.pcpingestion.interservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberContractClaimRequest {

	private String claimId;
	private String contractId;
	private String memberId;
	private String providerId;
	private String operatorId;
	private String state;
}
