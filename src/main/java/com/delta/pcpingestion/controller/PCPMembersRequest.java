package com.delta.pcpingestion.controller;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PCPMembersRequest {

	@NotBlank
	private String tibcoQueryStr;
}
