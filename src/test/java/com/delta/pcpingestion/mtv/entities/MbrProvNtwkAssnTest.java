package com.delta.pcpingestion.mtv.entities;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("When MbrProvNtwkAssnTest")
@Configuration
@Slf4j
public class MbrProvNtwkAssnTest {
	
	
	@Test
	public void testMbrProvNtwkAssn() {
		MbrProvNtwkAssn entity = new MbrProvNtwkAssn();
		entity.setContractID("contractId");
		entity.setEffectiveDate(new Date(Long.parseLong("20221212")));
		entity.setEndDate(new Date(Long.parseLong("20221212")));
		entity.setLastMaintTs(Timestamp.valueOf("2022-05-18 17:20:47.190"));
		entity.setMemberId("memeber01");
		
		Assertions.assertNotNull(entity);
		
		
	}

}
