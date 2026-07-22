package com.bobacom.backend.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.bobacom.backend.dto.input.ComposizioneReq;
import com.bobacom.backend.dto.input.ProdottoRequest;
import com.bobacom.backend.dto.input.TagProdottoReq;
import com.bobacom.backend.model.Composizione;
import com.bobacom.backend.model.TagProdotto;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProdottoImplementationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Test
	@Order (1)
	public void createProdottoTest() throws Exception {
		log.debug("createProdottoTest");
			
		ProdottoRequest req = new ProdottoRequest();
		
		req.setNome("BobaNome");
		req.setDescrizione("BobaDescrizione");
		req.setImgUrl("BobaImgUrl");
		
		//La composizione è obbligatoria
		List<Composizione> compList = new ArrayList<>();
		compList.add(new Composizione());
		req.setComposizione(compList);
		
		req.setPromozione(null);
		req.setTag(null);
		
		
		mockMvc.perform(post("/rest/prodotto/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk());
	}
	
	@Test
	@Order(2)
	public void createProdottoNoComposizioneTest() throws Exception {
		log.debug("createProdottoNoComposizioneTest");
			
		ProdottoRequest req = new ProdottoRequest();
		req.setNome("BobaNome");
		req.setDescrizione("BobaDescrizione");
		req.setImgUrl("BobaImgUrl");
		
		//req.setComposizione(new ArrayList<>());
		req.setPromozione(null);
		req.setTag(null);
	
		mockMvc.perform(post("/rest/prodotto/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	@Order(3)
	public void updateProdottoTest() throws Exception {
		log.debug("updateProdottoTest");
			
		ProdottoRequest req = new ProdottoRequest();
		req.setId(1);
		req.setNome("NomeAggiornato");
		req.setDescrizione("DescrizioneAggiornata");
		req.setImgUrl("UrlAggiornato");
		req.setComposizione(null); 
		req.setPromozione(null);
		req.setTag(null);
		
		mockMvc.perform(put("/rest/prodotto/update")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk());
	}
	
	@Test
	@Order(4)
	public void updateProdottoConComposizioneTest() throws Exception {
		log.debug("updateProdottoConComposizioneTest");
			
		ProdottoRequest req = new ProdottoRequest();
		req.setId(1);
		req.setNome("NomeAggiornato");
		req.setDescrizione("DescrizioneAggiornata");
		req.setImgUrl("UrlAggiornato");
		
		List<Composizione> compList = new ArrayList<>();
		compList.add(new Composizione()); 
		req.setComposizione(compList); 
		
		req.setPromozione(null);
		req.setTag(null);
		
		mockMvc.perform(put("/rest/prodotto/update")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
				.andDo(print())
				.andExpect(status().isOk());
	}
	/*
	@Test
	@Order(5)
	public void deleteProdottoTest() throws Exception {
		log.debug("deleteProdottoTest");

		Integer id = 1; 
		
		mockMvc.perform(delete("/rest/prodotto/delete/{id}", id)
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());
	}
	*/
	@Test
	@Order(6)
	public void listProdottoTest() throws Exception {
		log.debug("listProdottoTest");
		
		mockMvc.perform(get("/rest/prodotto/list")
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());
	}
	
	@Test
	@Order(7)
	public void getByIdProdottoTest() throws Exception {
		log.debug("getByIdProdottoTest");
		
		Integer id = 1;
		
		mockMvc.perform(get("/rest/prodotto/get/{id}", id)
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());
	}
}