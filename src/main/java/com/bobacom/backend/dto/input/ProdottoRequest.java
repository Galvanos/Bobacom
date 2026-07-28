package com.bobacom.backend.dto.input;

import java.util.List;
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
	private String nome;
	private String descrizione;
	private String imgUrl;
	private List<Integer> tag;
	private List<Integer> promozione;
	private List<ComposizioneReq> composizione;

}
