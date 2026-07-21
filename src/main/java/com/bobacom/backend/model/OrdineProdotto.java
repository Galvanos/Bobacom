package com.bobacom.backend.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Table(name="ordine_prodotto")
public class OrdineProdotto {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(
			name="id_ordine",
			foreignKey = @ForeignKey(name="fk_ordine_prodotto"),
			nullable = false
			)
	private Ordine ordine;
	
	@ManyToOne()
	@JoinColumn(
			name="prodotto_id",
			foreignKey = @ForeignKey(name ="fk_prodotto_ordine"),
			nullable = false
			)
	private Prodotto prodotto;
	
	@Column(nullable = false)
	private Integer quantita;
	
	@Column(nullable = false)
	private BigDecimal prezzo;
}
