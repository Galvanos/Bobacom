package com.bobacom.backend.model;

import java.math.BigDecimal;

import com.bobacom.backend.enums.Ruoli;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="Utente")
public class Utente {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	private String username;
	
	@Column
	private String email;
	
	@Column
	private String password;
	
	@Column
	private Ruoli ruolo;
	
	@Column
	private BigDecimal credito;
	
	@Column
	private String indirizzo;
	
}
