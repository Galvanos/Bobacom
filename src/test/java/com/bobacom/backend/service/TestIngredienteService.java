package com.bobacom.backend.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.jdbc.Expectations;
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
import com.bobacom.backend.model.Allergeni;
import com.bobacom.backend.model.CategoriaIngrediente;
import com.bobacom.backend.repository.IAllergeniRepository;
import com.bobacom.backend.repository.ICategoriaRepository;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TestIngredienteService {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ICategoriaRepository catRepo;
	@Autowired
	private IAllergeniRepository allRepo;
	
	@Order(1)
	@Test
	void createTest() throws Exception{
		CategoriaIngrediente cat = CategoriaIngrediente.builder().nome("topping").build();
		cat = catRepo.save(cat);
		Allergeni all = Allergeni.builder().nome("Sfrigolizia").build();
		all = allRepo.save(all);
		List<Integer> allList = new ArrayList<Integer>();
		allList.add(all.getId());
		IngredienteRequest request = IngredienteRequest.builder()	.nome("Fragoline")
																	.descrizione("Fragolette")
																	.colore("RED")
																	.prezzoRestock(0.5f)
																	.quantitaStock(BigDecimal.valueOf(100))
																	.sovraprezzoAggiunta(0.5f)
																	.idCategoriaIngrediente(cat.getId())
																	.idAllergene(allList)
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
	
	@Order(2)
	@Test
	void updateTest() throws Exception{
		CategoriaIngrediente cat = CategoriaIngrediente.builder().nome("sfizioseria").build();
		cat = catRepo.save(cat);
		Allergeni all = Allergeni.builder().nome("Blumele").build();
		all = allRepo.save(all);
		List<Integer> allList = new ArrayList<Integer>();
		allList.add(all.getId());
		IngredienteRequest request = IngredienteRequest.builder()	.id(1)
																	.nome("Appine")
																	.descrizione("Mappenze")
																	.colore("YELLOW")
																	.prezzoRestock(1f)
																	.quantitaStock(BigDecimal.valueOf(30))
																	.sovraprezzoAggiunta(0.7f)
																	.idCategoriaIngrediente(cat.getId())
																	.idAllergene(allList)
																	.build();
		try {
			mockMvc.perform(patch("/rest/ingrediente/update")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
			).andExpect(status().isOk());
		} catch (Exception e) {
			log.error("Error nell' update di un ingrediente: {}", e.getMessage());
		}	
	}
	
	@Order(3)
	@Test
	void deleteTest() throws Exception{
		try {
			mockMvc.perform(delete("/rest/ingrediente/delete/1")
			.contentType(MediaType.APPLICATION_JSON)
			).andExpect(status().isOk());
		} catch (Exception e) {
			log.error("Error nell' update di un ingrediente: {}", e.getMessage());
		}	
	}
	@Order(4)
	@Test
	void listTest() throws Exception{
		try {
			mockMvc.perform(get("/rest/ingrediente/list")
					.contentType(MediaType.APPLICATION_JSON)
					).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
		} catch (Exception e) {
			log.error("Error nell' update di un ingrediente: {}", e.getMessage());
		}
	}
}
