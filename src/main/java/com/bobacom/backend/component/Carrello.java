package com.bobacom.backend.component;

import java.io.Serializable;
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
	private Float totale = 0f;
	
	public void addItem(CartItem item) {
		items.add(item);
		totale+=item.getPrezzo();
	}
	public void removeItem(CartItem item) {
		items.remove(item);
		totale-=item.getPrezzo();
	}
	public float getTotal() {
		Float total = 0f;
		for(CartItem i : items)
			total += i.getPrezzo();
		return total;
	}
	
}
