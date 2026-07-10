package com.bobacom.backend.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

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
	
	@ManyToMany(mappedBy = "promozione")
	List<Prodotto> prodotto;
}
