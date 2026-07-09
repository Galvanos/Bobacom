package com.bobacom.backend.model;

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
	private Float deltaQuantita;
	
	@Column
	private String causale;
	
	@Column
	private LocalDate data;
	
}
