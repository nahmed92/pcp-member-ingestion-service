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
public class MemberIngestionScheduler {

	@Autowired
	private IngestionControllerService service;

	@ApiOperation(value = "Schedule ingestion ", response = String.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Successfully start ingestion", response = Boolean.class), })
	@GetMapping(value = "/ingestion", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@MethodExecutionTime
	public ResponseEntity<Boolean> schedule() {
		log.info("START MemberIngestionScheduler.schedule()");

		service.populateControl();

		log.info("END MemberIngestionScheduler.schedule()");

		return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);

	}

}
