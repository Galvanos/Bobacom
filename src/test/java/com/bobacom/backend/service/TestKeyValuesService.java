package com.bobacom.backend.service;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.transaction.annotation.Transactional;

import com.bobacom.backend.dto.output.KeyValuesDTO;
import com.bobacom.backend.exceptions.AcademyException;
import com.bobacom.backend.service.interfaces.IKeyValuesService;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@Transactional // per questo test voglio i rollback ad ogni test, quindi transactional
public class TestKeyValuesService {

	@Autowired
	private IKeyValuesService keyValuesService;

	@Order(1)
	@Test
	void createTest() throws Exception {
		KeyValuesDTO createdDto = keyValuesService.create("chiave_singola", "singola");

		Assertions.assertThat(createdDto).isNotNull();
		Assertions.assertThat(createdDto.getKey()).isEqualTo("chiave_singola");
		Assertions.assertThat(createdDto.getValue()).isEqualTo("singola");
		Assertions.assertThat(createdDto.getValues().size()).isEqualTo(1);

		KeyValuesDTO createdDoubleDTO = keyValuesService.create("chiave_doppia", Lists.list("primo", "secondo"));

		Assertions.assertThat(createdDoubleDTO).isNotNull();
		Assertions.assertThat(createdDoubleDTO.getKey()).isEqualTo("chiave_doppia");
		Assertions.assertThat(createdDoubleDTO.getValue()).isEqualTo("primo");
		Assertions.assertThat(createdDoubleDTO.getValues()).isEqualTo(Lists.list("primo", "secondo"));
		Assertions.assertThat(createdDoubleDTO.getValues().size()).isEqualTo(2);

	}
	
	@Order(2)
	@Test
	void createNullValuesTest() throws Exception {
		
		KeyValuesDTO createdNullListDto = keyValuesService.createNullList("null_list");

		Assertions.assertThat(createdNullListDto).isNotNull();
		Assertions.assertThat(createdNullListDto.getKey()).isEqualTo("null_list");
		Assertions.assertThat(createdNullListDto.getValue()).isEqualTo(null);
		Assertions.assertThat(createdNullListDto.getValues()).isNull();
		
		
		KeyValuesDTO createdNullStringDto = keyValuesService.createNullString("null_string");

		Assertions.assertThat(createdNullStringDto).isNotNull();
		Assertions.assertThat(createdNullStringDto.getKey()).isEqualTo("null_string");
		Assertions.assertThat(createdNullStringDto.getValue()).isEqualTo(null);
		Assertions.assertThat(createdNullStringDto.getValues().size()).isEqualTo(1);
		Assertions.assertThat(createdNullStringDto.getValues()).isEqualTo(Collections.singletonList(null));
		
		KeyValuesDTO createdEmptyListDto = keyValuesService.create("empty_list",Collections.emptyList());

		Assertions.assertThat(createdEmptyListDto).isNotNull();
		Assertions.assertThat(createdEmptyListDto.getKey()).isEqualTo("empty_list");
		Assertions.assertThat(createdEmptyListDto.getValue()).isEqualTo(null);
		Assertions.assertThat(createdEmptyListDto.getValues().size()).isEqualTo(0);
		Assertions.assertThat(createdEmptyListDto.getValues()).isEqualTo(Collections.emptyList());
		

		KeyValuesDTO createdDoubleSecondNullDTO = keyValuesService.create("null_secondo", Lists.list("primo", null));

		Assertions.assertThat(createdDoubleSecondNullDTO).isNotNull();
		Assertions.assertThat(createdDoubleSecondNullDTO.getKey()).isEqualTo("null_secondo");
		Assertions.assertThat(createdDoubleSecondNullDTO.getValue()).isEqualTo("primo");
		Assertions.assertThat(createdDoubleSecondNullDTO.getValues()).isEqualTo(Lists.list("primo", null));
		Assertions.assertThat(createdDoubleSecondNullDTO.getValues().size()).isEqualTo(2);
		
		
		KeyValuesDTO createdDoubleFirstNullDTO = keyValuesService.create("null_primo", Lists.list(null, "secondo"));

		Assertions.assertThat(createdDoubleFirstNullDTO).isNotNull();
		Assertions.assertThat(createdDoubleFirstNullDTO.getKey()).isEqualTo("null_primo");
		Assertions.assertThat(createdDoubleFirstNullDTO.getValue()).isNull();
		Assertions.assertThat(createdDoubleFirstNullDTO.getValues()).isEqualTo(Lists.list(null, "secondo"));
		Assertions.assertThat(createdDoubleFirstNullDTO.getValues().size()).isEqualTo(2);
		
		KeyValuesDTO createdDoubleBothNullDTO = keyValuesService.create("null_entrambi", Lists.list(null, null));

		Assertions.assertThat(createdDoubleBothNullDTO).isNotNull();
		Assertions.assertThat(createdDoubleBothNullDTO.getKey()).isEqualTo("null_entrambi");
		Assertions.assertThat(createdDoubleBothNullDTO.getValue()).isEqualTo(null);
		Assertions.assertThat(createdDoubleBothNullDTO.getValues()).isEqualTo(Lists.list(null, null));
		Assertions.assertThat(createdDoubleBothNullDTO.getValues().size()).isEqualTo(2);

	}

	@Order(3)
	@Test
	void createTestDuplicated() throws Exception {
		KeyValuesDTO createdDto = keyValuesService.create("chiave_singola", "singola");

		Assertions.assertThat(createdDto).isNotNull();
		Assertions.assertThat(createdDto.getKey()).isEqualTo("chiave_singola");
		Assertions.assertThat(createdDto.getValue()).isEqualTo("singola");
		Assertions.assertThat(createdDto.getValues().size()).isEqualTo(1);

		Assertions.assertThatThrownBy(() -> {
			keyValuesService.create("chiave_singola", "duplicata");
		}).isInstanceOf(AcademyException.class).hasMessage("Chiave chiave_singola già esistente");

		KeyValuesDTO createdDoubleDTO = keyValuesService.create("chiave_doppia", Lists.list("primo", "secondo"));

		Assertions.assertThat(createdDoubleDTO).isNotNull();
		Assertions.assertThat(createdDoubleDTO.getKey()).isEqualTo("chiave_doppia");
		Assertions.assertThat(createdDoubleDTO.getValue()).isEqualTo("primo");
		Assertions.assertThat(createdDoubleDTO.getValues()).isEqualTo(Lists.list("primo", "secondo"));
		Assertions.assertThat(createdDoubleDTO.getValues().size()).isEqualTo(2);

		Assertions.assertThatThrownBy(() -> {
			keyValuesService.create("chiave_doppia", Lists.list("ancora", "duplicata"));
		}).isInstanceOf(AcademyException.class).hasMessage("Chiave chiave_doppia già esistente");
	}
	
	
	@Order(4)
	@Test
	void createNullKeyTest() throws Exception {
		Assertions.assertThatThrownBy(() -> {
			keyValuesService.create(null, Lists.list("ancora", "duplicata"));
		}).isInstanceOf(AcademyException.class).hasMessage("La chiave non può essere null");
	}
	

	@Order(5)
	@Test
	void readTest() throws Exception {
		KeyValuesDTO createdDto = keyValuesService.create("chiave_singola", "singola");

		Assertions.assertThat(createdDto).isNotNull();
		Assertions.assertThat(createdDto.getKey()).isEqualTo("chiave_singola");
		Assertions.assertThat(createdDto.getValue()).isEqualTo("singola");
		Assertions.assertThat(createdDto.getValues().size()).isEqualTo(1);

		KeyValuesDTO readDTO = keyValuesService.read("chiave_singola");

		Assertions.assertThat(readDTO).isNotNull();
		Assertions.assertThat(readDTO.getKey()).isEqualTo("chiave_singola");
		Assertions.assertThat(readDTO.getValue()).isEqualTo("singola");
		Assertions.assertThat(readDTO.getValues().size()).isEqualTo(1);

		KeyValuesDTO createdDoubleDTO = keyValuesService.create("chiave_doppia", Lists.list("primo", "secondo"));

		Assertions.assertThat(createdDoubleDTO).isNotNull();
		Assertions.assertThat(createdDoubleDTO.getKey()).isEqualTo("chiave_doppia");
		Assertions.assertThat(createdDoubleDTO.getValue()).isEqualTo("primo");
		Assertions.assertThat(createdDoubleDTO.getValues()).isEqualTo(Lists.list("primo", "secondo"));
		Assertions.assertThat(createdDoubleDTO.getValues().size()).isEqualTo(2);

		KeyValuesDTO readDoubleDTO = keyValuesService.read("chiave_doppia");

		Assertions.assertThat(readDoubleDTO).isNotNull();
		Assertions.assertThat(readDoubleDTO.getKey()).isEqualTo("chiave_doppia");
		Assertions.assertThat(readDoubleDTO.getValue()).isEqualTo("primo");
		Assertions.assertThat(readDoubleDTO.getValues()).isEqualTo(Lists.list("primo", "secondo"));
		Assertions.assertThat(readDoubleDTO.getValues().size()).isEqualTo(2);

	}

	@Order(6)
	@Test
	void readNotExistingTest() throws Exception {
		KeyValuesDTO createdDto = keyValuesService.create("chiave_singola", "singola");

		Assertions.assertThat(createdDto).isNotNull();
		Assertions.assertThat(createdDto.getKey()).isEqualTo("chiave_singola");
		Assertions.assertThat(createdDto.getValue()).isEqualTo("singola");
		Assertions.assertThat(createdDto.getValues().size()).isEqualTo(1);

		Assertions.assertThatThrownBy(() -> {
			keyValuesService.read("inesistente");
		}).isInstanceOf(AcademyException.class).hasMessage("Chiave inesistente non trovata");
	}

	@Order(7)
	@Test
	void updateTest() throws Exception {
		KeyValuesDTO createdDto = keyValuesService.create("chiave_singola", "singola");

		Assertions.assertThat(createdDto).isNotNull();
		Assertions.assertThat(createdDto.getKey()).isEqualTo("chiave_singola");
		Assertions.assertThat(createdDto.getValue()).isEqualTo("singola");
		Assertions.assertThat(createdDto.getValues().size()).isEqualTo(1);

		KeyValuesDTO updatedDTO = keyValuesService.update("chiave_singola", "aggiornata");

		Assertions.assertThat(updatedDTO).isNotNull();
		Assertions.assertThat(updatedDTO.getKey()).isEqualTo("chiave_singola");
		Assertions.assertThat(updatedDTO.getValue()).isEqualTo("aggiornata");
		Assertions.assertThat(updatedDTO.getValues().size()).isEqualTo(1);

		// verifico una lettura dopo l'aggiornamento
		KeyValuesDTO readDTO = keyValuesService.read("chiave_singola");

		Assertions.assertThat(readDTO).isNotNull();
		Assertions.assertThat(readDTO.getKey()).isEqualTo("chiave_singola");
		Assertions.assertThat(readDTO.getValue()).isEqualTo("aggiornata");
		Assertions.assertThat(readDTO.getValues().size()).isEqualTo(1);

		KeyValuesDTO createdDoubleDTO = keyValuesService.create("chiave_doppia", Lists.list("primo", "secondo"));

		Assertions.assertThat(createdDoubleDTO).isNotNull();
		Assertions.assertThat(createdDoubleDTO.getKey()).isEqualTo("chiave_doppia");
		Assertions.assertThat(createdDoubleDTO.getValue()).isEqualTo("primo");
		Assertions.assertThat(createdDoubleDTO.getValues()).isEqualTo(Lists.list("primo", "secondo"));
		Assertions.assertThat(createdDoubleDTO.getValues().size()).isEqualTo(2);

		KeyValuesDTO updatedDoppiaDTO = keyValuesService.update("chiave_doppia",
				Lists.list("prima aggiornata", "secondo aggiornato"));

		Assertions.assertThat(updatedDoppiaDTO).isNotNull();
		Assertions.assertThat(updatedDoppiaDTO.getKey()).isEqualTo("chiave_doppia");
		Assertions.assertThat(updatedDoppiaDTO.getValue()).isEqualTo("prima aggiornata");
		Assertions.assertThat(updatedDoppiaDTO.getValues())
				.isEqualTo(Lists.list("prima aggiornata", "secondo aggiornato"));
		Assertions.assertThat(updatedDoppiaDTO.getValues().size()).isEqualTo(2);

		// verifico una lettura dopo l'aggiornamento
		KeyValuesDTO readDoppiaDTO = keyValuesService.read("chiave_doppia");

		Assertions.assertThat(readDoppiaDTO).isNotNull();
		Assertions.assertThat(readDoppiaDTO.getKey()).isEqualTo("chiave_doppia");
		Assertions.assertThat(readDoppiaDTO.getValue()).isEqualTo("prima aggiornata");
		Assertions.assertThat(readDoppiaDTO.getValues())
				.isEqualTo(Lists.list("prima aggiornata", "secondo aggiornato"));
		Assertions.assertThat(readDoppiaDTO.getValues().size()).isEqualTo(2);
		
		//adesso si provano ad inserire più valori nella singola e un valore solo nella doppia
		
		
		KeyValuesDTO updatedTriplaInSingolaDTO = keyValuesService.update("chiave_singola", Lists.list("tripla","in","singola"));

		Assertions.assertThat(updatedTriplaInSingolaDTO).isNotNull();
		Assertions.assertThat(updatedTriplaInSingolaDTO.getKey()).isEqualTo("chiave_singola");
		Assertions.assertThat(updatedTriplaInSingolaDTO.getValue()).isEqualTo("tripla");
		Assertions.assertThat(updatedTriplaInSingolaDTO.getValues())
		.isEqualTo( Lists.list("tripla","in","singola"));
		Assertions.assertThat(updatedTriplaInSingolaDTO.getValues().size()).isEqualTo(3);

		// verifico una lettura dopo l'aggiornamento
		KeyValuesDTO readTriplaInSingolaDTO = keyValuesService.read("chiave_singola");

		Assertions.assertThat(readTriplaInSingolaDTO).isNotNull();
		Assertions.assertThat(readTriplaInSingolaDTO.getKey()).isEqualTo("chiave_singola");
		Assertions.assertThat(readTriplaInSingolaDTO.getValue()).isEqualTo("tripla");
		Assertions.assertThat(readTriplaInSingolaDTO.getValues())
		.isEqualTo( Lists.list("tripla","in","singola"));
		Assertions.assertThat(readTriplaInSingolaDTO.getValues().size()).isEqualTo(3);

		
		KeyValuesDTO updatedSingolaInDoppiaDTO = keyValuesService.update("chiave_doppia",
				"Singola in doppia");

		Assertions.assertThat(updatedSingolaInDoppiaDTO).isNotNull();
		Assertions.assertThat(updatedSingolaInDoppiaDTO.getKey()).isEqualTo("chiave_doppia");
		Assertions.assertThat(updatedSingolaInDoppiaDTO.getValue()).isEqualTo("Singola in doppia");
		Assertions.assertThat(updatedSingolaInDoppiaDTO.getValues().size()).isEqualTo(1);

		// verifico una lettura dopo l'aggiornamento
		KeyValuesDTO readSingolaInDoppiaDTO = keyValuesService.read("chiave_doppia");

		Assertions.assertThat(readSingolaInDoppiaDTO).isNotNull();
		Assertions.assertThat(readSingolaInDoppiaDTO.getKey()).isEqualTo("chiave_doppia");
		Assertions.assertThat(readSingolaInDoppiaDTO.getValue()).isEqualTo("Singola in doppia");
		Assertions.assertThat(readSingolaInDoppiaDTO.getValues().size()).isEqualTo(1);

	}
	
	@Order(8)
	@Test
	void updateNotExistingTest() throws Exception {
		KeyValuesDTO createdDto = keyValuesService.create("chiave_singola", "singola");

		Assertions.assertThat(createdDto).isNotNull();
		Assertions.assertThat(createdDto.getKey()).isEqualTo("chiave_singola");
		Assertions.assertThat(createdDto.getValue()).isEqualTo("singola");
		Assertions.assertThat(createdDto.getValues().size()).isEqualTo(1);

		Assertions.assertThatThrownBy(() -> {
			keyValuesService.update("inesistente", "deve fallire");
		}).isInstanceOf(AcademyException.class).hasMessage("Chiave inesistente non trovata");
	}
	
	
	@Order(9)
	@Test
	void updateNullValuesTest() throws Exception {
		KeyValuesDTO createdDto = keyValuesService.create("chiave_singola", "singola");

		Assertions.assertThat(createdDto).isNotNull();
		Assertions.assertThat(createdDto.getKey()).isEqualTo("chiave_singola");
		Assertions.assertThat(createdDto.getValue()).isEqualTo("singola");
		Assertions.assertThat(createdDto.getValues().size()).isEqualTo(1);

		//aggiorno mettendo la list a null
		KeyValuesDTO updatedNullListDTO = keyValuesService.updateNullList("chiave_singola");

		Assertions.assertThat(updatedNullListDTO).isNotNull();
		Assertions.assertThat(updatedNullListDTO.getKey()).isEqualTo("chiave_singola");
		Assertions.assertThat(updatedNullListDTO.getValue()).isEqualTo(null);
		Assertions.assertThat(updatedNullListDTO.getValues()).isNull();

		// verifico una lettura dopo l'aggiornamento
		KeyValuesDTO readNullListDTO = keyValuesService.read("chiave_singola");

		Assertions.assertThat(readNullListDTO).isNotNull();
		Assertions.assertThat(readNullListDTO.getKey()).isEqualTo("chiave_singola");
		Assertions.assertThat(readNullListDTO.getValue()).isNull();
		Assertions.assertThat(readNullListDTO.getValues()).isNull();
		
		//aggiorno mettendo la string a null
		KeyValuesDTO updatedNullStringDTO = keyValuesService.updateNullString("chiave_singola");

		Assertions.assertThat(updatedNullStringDTO).isNotNull();
		Assertions.assertThat(updatedNullStringDTO.getKey()).isEqualTo("chiave_singola");
		Assertions.assertThat(updatedNullStringDTO.getValue()).isEqualTo(null);
		Assertions.assertThat(updatedNullStringDTO.getValues().size()).isEqualTo(1);
		Assertions.assertThat(updatedNullStringDTO.getValues()).isEqualTo(Collections.singletonList(null));

		// verifico una lettura dopo l'aggiornamento
		KeyValuesDTO readNullStringDTO = keyValuesService.read("chiave_singola");

		Assertions.assertThat(readNullStringDTO).isNotNull();
		Assertions.assertThat(readNullStringDTO.getKey()).isEqualTo("chiave_singola");
		Assertions.assertThat(readNullStringDTO.getValue()).isNull();
		Assertions.assertThat(readNullStringDTO.getValues().size()).isEqualTo(1);
		Assertions.assertThat(readNullStringDTO.getValues()).isEqualTo(Collections.singletonList(null));
		
		// aggiorno mettendo una lista vuota
		KeyValuesDTO updatedEmptyListDTO = keyValuesService.update("chiave_singola",Collections.emptyList());

		Assertions.assertThat(updatedEmptyListDTO).isNotNull();
		Assertions.assertThat(updatedEmptyListDTO.getKey()).isEqualTo("chiave_singola");
		Assertions.assertThat(updatedEmptyListDTO.getValue()).isEqualTo(null);
		Assertions.assertThat(updatedEmptyListDTO.getValues().size()).isEqualTo(0);
		Assertions.assertThat(updatedEmptyListDTO.getValues()).isEqualTo(Collections.emptyList());

		// verifico una lettura dopo l'aggiornamento
		KeyValuesDTO readEmptyListDTO = keyValuesService.read("chiave_singola");

		Assertions.assertThat(readEmptyListDTO).isNotNull();
		Assertions.assertThat(readEmptyListDTO.getKey()).isEqualTo("chiave_singola");
		Assertions.assertThat(readEmptyListDTO.getValue()).isNull();
		Assertions.assertThat(readEmptyListDTO.getValues().size()).isEqualTo(0);
		Assertions.assertThat(readEmptyListDTO.getValues()).isEqualTo(Collections.emptyList());


		KeyValuesDTO createdDoubleDTO = keyValuesService.create("chiave_doppia", Lists.list("primo", "secondo"));

		Assertions.assertThat(createdDoubleDTO).isNotNull();
		Assertions.assertThat(createdDoubleDTO.getKey()).isEqualTo("chiave_doppia");
		Assertions.assertThat(createdDoubleDTO.getValue()).isEqualTo("primo");
		Assertions.assertThat(createdDoubleDTO.getValues()).isEqualTo(Lists.list("primo", "secondo"));
		Assertions.assertThat(createdDoubleDTO.getValues().size()).isEqualTo(2);

		//aggiorno mettendo il primo valore a null
		KeyValuesDTO updatedDoppiaFirstNullDTO = keyValuesService.update("chiave_doppia",
				Lists.list(null, "secondo aggiornato"));

		Assertions.assertThat(updatedDoppiaFirstNullDTO).isNotNull();
		Assertions.assertThat(updatedDoppiaFirstNullDTO.getKey()).isEqualTo("chiave_doppia");
		Assertions.assertThat(updatedDoppiaFirstNullDTO.getValue()).isEqualTo(null);
		Assertions.assertThat(updatedDoppiaFirstNullDTO.getValues())
				.isEqualTo(Lists.list(null, "secondo aggiornato"));
		Assertions.assertThat(updatedDoppiaFirstNullDTO.getValues().size()).isEqualTo(2);

		// verifico una lettura dopo l'aggiornamento
		KeyValuesDTO readDoppiaFirstNullDTO = keyValuesService.read("chiave_doppia");

		Assertions.assertThat(readDoppiaFirstNullDTO).isNotNull();
		Assertions.assertThat(readDoppiaFirstNullDTO.getKey()).isEqualTo("chiave_doppia");
		Assertions.assertThat(readDoppiaFirstNullDTO.getValue()).isNull();
		Assertions.assertThat(readDoppiaFirstNullDTO.getValues())
				.isEqualTo(Lists.list(null, "secondo aggiornato"));
		Assertions.assertThat(readDoppiaFirstNullDTO.getValues().size()).isEqualTo(2);
		//TODO riprendere qui
		
		//adesso si provano ad inserire più valori nella singola e un valore solo nella doppia
		
		
		KeyValuesDTO updatedTriplaInSingolaDTO = keyValuesService.update("chiave_singola", Lists.list("tripla","in","singola"));

		Assertions.assertThat(updatedTriplaInSingolaDTO).isNotNull();
		Assertions.assertThat(updatedTriplaInSingolaDTO.getKey()).isEqualTo("chiave_singola");
		Assertions.assertThat(updatedTriplaInSingolaDTO.getValue()).isEqualTo("tripla");
		Assertions.assertThat(updatedTriplaInSingolaDTO.getValues())
		.isEqualTo( Lists.list("tripla","in","singola"));
		Assertions.assertThat(updatedTriplaInSingolaDTO.getValues().size()).isEqualTo(3);

		// verifico una lettura dopo l'aggiornamento
		KeyValuesDTO readTriplaInSingolaDTO = keyValuesService.read("chiave_singola");

		Assertions.assertThat(readTriplaInSingolaDTO).isNotNull();
		Assertions.assertThat(readTriplaInSingolaDTO.getKey()).isEqualTo("chiave_singola");
		Assertions.assertThat(readTriplaInSingolaDTO.getValue()).isEqualTo("tripla");
		Assertions.assertThat(readTriplaInSingolaDTO.getValues())
		.isEqualTo( Lists.list("tripla","in","singola"));
		Assertions.assertThat(readTriplaInSingolaDTO.getValues().size()).isEqualTo(3);

		
		KeyValuesDTO updatedSingolaInDoppiaDTO = keyValuesService.update("chiave_doppia",
				"Singola in doppia");

		Assertions.assertThat(updatedSingolaInDoppiaDTO).isNotNull();
		Assertions.assertThat(updatedSingolaInDoppiaDTO.getKey()).isEqualTo("chiave_doppia");
		Assertions.assertThat(updatedSingolaInDoppiaDTO.getValue()).isEqualTo("Singola in doppia");
		Assertions.assertThat(updatedSingolaInDoppiaDTO.getValues().size()).isEqualTo(1);

		// verifico una lettura dopo l'aggiornamento
		KeyValuesDTO readSingolaInDoppiaDTO = keyValuesService.read("chiave_doppia");

		Assertions.assertThat(readSingolaInDoppiaDTO).isNotNull();
		Assertions.assertThat(readSingolaInDoppiaDTO.getKey()).isEqualTo("chiave_doppia");
		Assertions.assertThat(readSingolaInDoppiaDTO.getValue()).isEqualTo("Singola in doppia");
		Assertions.assertThat(readSingolaInDoppiaDTO.getValues().size()).isEqualTo(1);

	}
	
	

}
