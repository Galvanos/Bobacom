package com.bobacom.backend.dto.input;

import java.math.BigDecimal;

import com.bobacom.backend.dto.input.validation.ValidationGroups;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdineProdottoReq {
	
	@NotNull(groups = ValidationGroups.Create.class, message = "prodotto id non fornito")
	@NotNull(groups = ValidationGroups.Update.class, message = "prodotto id non fornito")
	private Integer prodotto_id;
	@NotNull(groups = ValidationGroups.Create.class, message = "quantita non fornito")
	@NotNull(groups = ValidationGroups.Update.class, message = "quantita non fornito")
	private Integer quantita;

}
