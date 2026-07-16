package com.bobacom.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name="composizione")
public class Composizione {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(
			name="id_prodotto",
			foreignKey = @ForeignKey(name="fk_composizione_prodotto")
			)
	private Prodotto prodotto;
	
	@ManyToOne
	@JoinColumn(
			name="id_ingrediente",
			foreignKey = @ForeignKey(name="fk_composizione_ingrediente")
			)
	private Ingrediente ingrediente;
	
	@Column(name="quantita")
	private Integer quantita;
	
	

}
