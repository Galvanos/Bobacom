package com.bobacom.backend.dto.input;

import java.util.List;

import com.bobacom.backend.dto.input.validation.ValidationGroups;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdottoRequest {
	private Integer id;
	@NotNull(groups = ValidationGroups.Create.class, message = "nome non fornito")
	private String nome;
	private String descrizione;
	private String imgUrl;
	@NotNull(groups = ValidationGroups.Create.class, message = "tag non fornito")
	private List<Integer> tag;
	private List<Integer> promozione;
	private List<ComposizioneReq> composizione;

}
