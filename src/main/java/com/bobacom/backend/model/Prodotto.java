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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="prodotto")
public class Prodotto {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	private String nome;
	
	@Column
	private String descrizione;		// descrizione del prodotto, da usare in UI
	
	@Column(name="img_url")
	private String imgUrl;			// url immagine, da usare in UI
	
	@ManyToMany (fetch = FetchType.EAGER)
	@JoinTable(
			name="prodotto_tag",
			joinColumns = @JoinColumn (name = "prodotto_id" ),
			inverseJoinColumns = @JoinColumn (name = "tag_id")
			)
	List<TagProdotto> tag;	// tag del prodotto, e.g. new, limited, cool drink, warm drink, vegan, etc
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name="prodotto_promozione",
			joinColumns = @JoinColumn (name = "prodotto_id" ),
			inverseJoinColumns = @JoinColumn (name = "promozione_id")
			)
	List<Promozione> promozione;	// sconti legati al prodotto
	
	@OneToMany(
			mappedBy="prodotto",
			fetch = FetchType.EAGER
			)
	private List<Composizione> composizione;	// lista di elementi "composizione" che ognuno contengono
												//  un'ingrediente e la relativa quantitá
}
