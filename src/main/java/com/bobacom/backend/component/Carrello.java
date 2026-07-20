package com.bobacom.backend.component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Component
@SessionScope
public class Carrello implements Serializable{
	private List<CartItem> items = new ArrayList<>();

	public BigDecimal getTotal() {
		BigDecimal total = BigDecimal.ZERO;
		for(CartItem i: items)
			total.add(i.getPrezzo().multiply(BigDecimal.valueOf(i.getQuantita())));
		return total;
	}
	
}
