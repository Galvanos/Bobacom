package com.bobacom.backend.dto.input;

import com.bobacom.backend.dto.input.validation.ValidationGroups;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AllergeniRequest {
	@NotNull(groups = ValidationGroups.Update.class, message = "id allergene non fornito")
	private Integer id;
	@NotNull(groups = ValidationGroups.Create.class, message = "nome allergene non fornito")
	private String nome;
	private String urlIcona;
}
