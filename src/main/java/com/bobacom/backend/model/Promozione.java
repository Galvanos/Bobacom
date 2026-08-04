package com.bobacom.backend.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="promozione")
public class Promozione {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	private Float sconto;
	
	@Column
	private Boolean isActive;
	
	@Builder.Default
	@JsonIgnore
	@ManyToMany(mappedBy = "promozione")
	private Set<Prodotto> prodotto = new HashSet<>();
	
	public void addProdotto(Prodotto prod) {
		prodotto.add(prod);
	}
	public void removeProdotto(Prodotto prod) {
		prodotto.remove(prod);
	}
	public Set<Prodotto> getProdotto() {
		return prodotto;
	}
}
