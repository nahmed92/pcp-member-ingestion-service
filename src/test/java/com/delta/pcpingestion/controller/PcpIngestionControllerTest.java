package com.delta.pcpingestion.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
//import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import com.delta.pcpingestion.AbstractRestIntegrationTest;
import com.delta.pcpingestion.client.TibcoClient;
import com.delta.pcpingestion.repo.ContractRepository;
import com.delta.pcpingestion.service.PCPIngestionService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * PCP Ingestion Controller Test
 * 
 * @author ca94197
 *
 */
public class PcpIngestionControllerTest extends AbstractRestIntegrationTest {

	@Mock
	private ContractRepository repo;

	@Mock
	private TibcoClient tibcoRestTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private PCPIngestionService service;

	@InjectMocks
	private PcpIngestionController controller;

	/*
	@Test
	public void testCreateNewPCPMembercontract() throws Exception {
		PCPMembersRequest member = new PCPMembersRequest();
		member.setTibcoQueryStr(
				"{\r\n" + "    'pcpMembersRequest':'{\"states\":[\"CA\"],\"numofdays\":1553}'\r\n" + "}");
		mockMvc.perform(post("/pcp-members/contract/create") //
				.contentType(MediaType.APPLICATION_JSON) //
				.content(objectMapper.writeValueAsString(member))) //
				.andExpect(status().isCreated());
	}

	@Test
	public void testFindAllContract() throws Exception {
		mockMvc.perform(get("/pcp-members/contract/find-all")) //
				.andExpect(status().isOk()); //
	}

	@Test
	public void testReturnBadRequestWhenPayloadisNull() throws Exception {
		mockMvc.perform(post("/pcp-members/contract/create") //
				.contentType(MediaType.APPLICATION_JSON) //
				.content("")) //
				.andExpect(status().isBadRequest());
	}
	*/
	
	@Test
	public void testReturnRequestNotFoundWhenURLIsNotCorrect() throws Exception {
		mockMvc.perform(post("/pcp-members/contract") //
				.contentType(MediaType.APPLICATION_JSON) //
				.content("{\r\n" + "    'pcpMembersRequest':'{\"states\":[\"CA\"],\"numofdays\":1553}'\r\n" + "}")) //
				.andExpect(status().isNotFound());
	}
}
