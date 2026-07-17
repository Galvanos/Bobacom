package com.bobacom.backend.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.bobacom.backend.enums.Ruolo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name="utente")
public class Utente {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(unique = true, nullable = false)
	private String username;
	
	@Column(nullable = false)
	private String email;
	
	@Column(nullable = false)
	private String password;
	
	@Enumerated(EnumType.STRING)
	@Column
	private Ruolo ruolo;
	
	@Column
	private BigDecimal credito;
	
	@Column
	private String indirizzo;
	
	@Builder.Default
	@OneToMany(mappedBy = "utente",
			fetch = FetchType.EAGER)
	Set<Ordine> ordini = new HashSet<Ordine>();
	
	public void addOrdine(Ordine ordine) {
        this.ordini.add(ordine); 
    }

    public void removeOrdine(Ordine ordine) {
        this.ordini.remove(ordine);
    }

    // Standard getters and setters (DO NOT write a setter for 'tags' that replaces the Set!)
    public Set<Ordine> getOrdine() {
        return ordini;
    }
	
}
