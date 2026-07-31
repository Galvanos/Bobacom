package com.bobacom.backend.dto.input;

import java.math.BigDecimal;
import java.util.List;

import com.bobacom.backend.dto.input.validation.ValidationGroups;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdineProdottoRequest {
	private Integer prodotto_id;
	private Integer quantita;
	private String summary;
	private BigDecimal prezzo;
}
