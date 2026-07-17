package com.bobacom.backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bobacom.backend.enums.StatoSpedizione;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name="ordine")
public class Ordine {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne()
	@JoinColumn(
			name="id_utente",
			foreignKey = @ForeignKey(name="fk_ordine_utente"),
			nullable = false
			)
	private Utente utente;
	
	@Column(name="prezzo_totale", nullable = false)
	private BigDecimal prezzoTotale;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatoSpedizione status;
	
	@Column(name="data_creazione", nullable = false)
	private LocalDateTime dataCreazione;
	
	@Column(name="indirizzo_destinazione", nullable = false)
	private String indirizzoDestinazione;
}
