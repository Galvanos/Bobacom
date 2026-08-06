package com.bobacom.backend.dto.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagRequest {
	private Integer id;
	private String nome;
	private String descrizione;
}
