package com.bobacom.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="ingredienti")
public class Ingrediente {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	private String nome;

	@Column
	private String descrizione;
	
	@Column(name="sovraprezzo_aggiunta")
	private float sovraprezzoAggiunta;
	
	@Column(name="prezzo_restock")
	private float prezzoRestock;
	
	@Column
	private String colore;
	
	@Column(name="is_vegan")
	private Boolean isVegan;
	
	@Column(name="is_lactose_free")
	private Boolean isLactoseFree;
	
	@Column(name="is_gluten_free")
	private Boolean isGlutenFree;
}
