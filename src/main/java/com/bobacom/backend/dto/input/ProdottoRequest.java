package com.bobacom.backend.dto.input;

import java.util.List;

import com.bobacom.backend.model.Composizione;
import com.bobacom.backend.model.Promozione;
import com.bobacom.backend.model.TagProdotto;

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
public class ProdottoRequest {
	private Integer id;
	private String nome;
	private String descrizione;
	private String imgUrl;
	List<TagProdotto> tag;
	List<Promozione> promozione;
	private List<Composizione> composizione;

}
