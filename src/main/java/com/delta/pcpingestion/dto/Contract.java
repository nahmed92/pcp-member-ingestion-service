package com.delta.pcpingestion.dto;

import java.util.List;

import lombok.Data;

/**
 * Contract Object Class
 * 
 * @author ca94197
 * @since 1.0
 */
@Data
public class Contract {

	private String contractID;

	private String groupNumber;

	private String divisionNumber;

	private List<Enrollee> enrollees;

}
