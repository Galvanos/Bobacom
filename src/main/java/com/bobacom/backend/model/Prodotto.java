package com.bobacom.backend.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="prodotto")
public class Prodotto {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	private String nome;
	
	@Column
	private String descrizione;
	
	@Column(name="img_url")
	private String imgUrl;
	
	@ManyToMany (fetch = FetchType.EAGER)
	@JoinTable(
			name="prodotto_categorie",
			joinColumns = @JoinColumn (name = "prodotto_id" ),
			inverseJoinColumns = @JoinColumn (name = "categoria_id")
			)
	List<Categoria> categorie;
	
	@OneToMany(
			mappedBy="prodotto",
			fetch = FetchType.EAGER
			)
	private List<Composizione> composizione;
}
