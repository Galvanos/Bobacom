package com.bobacom.backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name="operazione_magazzino")
public class OperazioneMagazzino {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(
			name="id_ingrediente",
			foreignKey = @ForeignKey(name="fk_operazionemagazzino_ingrediente")
			)
	private Ingrediente ingrediente;
	
	@Column(name="delta_quantita")
	private BigDecimal deltaQuantita;
	
	@Column
	private String causale;
	
	@Column
	private LocalDate data;
	
}
