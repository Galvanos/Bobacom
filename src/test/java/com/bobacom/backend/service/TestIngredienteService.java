package com.bobacom.backend.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.bobacom.backend.dto.input.IngredienteRequest;
import com.bobacom.backend.model.CategoriaIngrediente;
import com.bobacom.backend.repository.ICategoriaRepository;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TestIngredienteService {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ICategoriaRepository catRepo;
	
	@Order(1)
	@Test
	void createTest() throws Exception{
		CategoriaIngrediente cat = CategoriaIngrediente.builder().nome("topping").build();
		cat = catRepo.save(cat);
		IngredienteRequest request = IngredienteRequest.builder()	.nome("Fragoline")
																	.descrizione("Fragolette")
																	.colore("RED")
																	.prezzoRestock(0.5f)
																	.quantitaStock(BigDecimal.valueOf(100))
																	.sovraprezzoAggiunta(0.5f)
																	.idCategoriaIngrediente(cat.getId())
																	.build();
		try {
			mockMvc.perform(post("/rest/ingrediente/create")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
					).andExpect(status().isOk());
		} catch (Exception e) {
			log.error("Error nel creare ingrediente: {}", e.getMessage());
		}
	}
}
