package com.bobacom.backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;


import com.bobacom.backend.enums.StatoSpedizione;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	
	@Builder.Default
	@OneToMany(mappedBy = "ordine",
			fetch = FetchType.EAGER)
	Set<OrdineProdotto> ordineProdotti = new HashSet<OrdineProdotto>();
	
	public void addOrdineProdotto(OrdineProdotto ordineProdotti) {
        this.ordineProdotti.add(ordineProdotti); 
    }

    public void removeOrdineProdotto(OrdineProdotto ordineProdotti) {
        this.ordineProdotti.remove(ordineProdotti);
    }

    public Set<OrdineProdotto> getOrdineProdotto() {
        return ordineProdotti;
    }
}
