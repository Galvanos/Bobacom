package com.bobacom.backend.component;

import java.math.BigDecimal;
import java.util.List;

import com.bobacom.backend.model.Ingrediente;
import com.bobacom.backend.model.Prodotto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItem {
	private Prodotto prodotto;
	private List<Ingrediente> ingredienti;
	private BigDecimal prezzo;
	private Integer quantita;
}
