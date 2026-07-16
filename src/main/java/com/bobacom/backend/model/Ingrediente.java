package com.bobacom.backend.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="ingredienti")
public class Ingrediente {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	private String nome;

	@Column
	private String descrizione;	// descrizione, principalmente per mostrarla in UI
	
	@Column(name="quantita_stock")
	private BigDecimal quantitaStock;	//quantitá di pezzi in stock
	
	@Column(name="sovraprezzo_aggiunta") 
	private float sovraprezzoAggiunta; // il prezzo che sostiene il cliente quando aggiunge questo ingrediente al suo prodotto
	
	@Column(name="prezzo_restock")
	private float prezzoRestock;	// il prezzo unitario che viene a costare all'azienda per il restock dell'ingrediente
	
	@Column
	private String colore; // pensato per il seguente utilizzo nella UI
	
	@Builder.Default
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name="ingrediente_allergeni",
			joinColumns = @JoinColumn (name = "ingrediente_id" ),
			inverseJoinColumns = @JoinColumn (name = "allergene_id"))
	Set<Allergeni> allergeni = new HashSet<>();	// lista allergeni, gli oggetti contengono nome e icona da usare in UI
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="categoria_id")
	private CategoriaIngrediente categoriaIngrediente;	// categoria, aka in che sezione della ui composizione prodotto 
														// vogliamo inserirlo, es: thé, succo, latte, topping, etc

    public void addAllergene(Allergeni allergene) {
        this.allergeni.add(allergene);
    }

    public void removeAllergene(Allergeni allergene) {
        this.allergeni.remove(allergene);
    }

    // Standard getters and setters (DO NOT write a setter for 'tags' that replaces the Set!)
    public Set<Allergeni> getAllergeni() {
        return allergeni;
    }
}														
