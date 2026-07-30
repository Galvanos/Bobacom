package com.bobacom.backend.dto.input;

import java.math.BigDecimal;
import java.util.List;

import com.bobacom.backend.dto.input.validation.ValidationGroups;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrdineProdottoRequest {
	private Integer prodotto_id;
	private Integer quantita;
	private String summary;
	private BigDecimal prezzo;
}
