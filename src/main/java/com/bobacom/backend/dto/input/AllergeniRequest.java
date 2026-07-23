package com.bobacom.backend.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AllergeniRequest {
	private Integer id;
	private String nome;
	private String urlIcona;
}
