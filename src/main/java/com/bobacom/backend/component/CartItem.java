package com.bobacom.backend.component;

import java.util.List;

import com.bobacom.backend.model.Ingrediente;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItem {
	private List<Ingrediente> ingredienti;
	private Float prezzo;
}
