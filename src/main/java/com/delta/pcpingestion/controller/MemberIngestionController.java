package com.delta.pcpingestion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.delta.pcpingestion.service.IngestionControllerService;
import com.delta.pcpingestion.service.IngestionService;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@RestController
@RequestMapping(value = "/pcp", produces = "application/json")
@Api(value = "/pcp")
@Slf4j
public class MemberIngestionController {

	@Autowired
	private IngestionControllerService service;
	
	@Autowired
	private IngestionService ingestionService;

	@ApiOperation(value = "populate control ", response = String.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Successfully start ingestion", response = Boolean.class), })
	@GetMapping(value = "/init-controller", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@MethodExecutionTime
	public ResponseEntity<Boolean> initiateControll() {
		log.info("START MemberIngestionController.initiateControll()");

		service.populateControl();

		log.info("END MemberIngestionController.initiateControll()");

		return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);

	}
	
	
	@ApiOperation(value = "Schedule ingestion ", response = String.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Successfully start ingestion", response = Boolean.class), })
	@GetMapping(value = "/ingest", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@MethodExecutionTime
	public ResponseEntity<Boolean> ingest() {
		log.info("START MemberIngestionController.ingest()");

		ingestionService.ingest();

		log.info("END MemberIngestionController.ingest()");

		return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);

	}


}
