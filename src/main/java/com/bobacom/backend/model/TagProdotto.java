package com.bobacom.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name="tag_prodotto")
public class TagProdotto {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	private String nome;
	
	@Column
	private String descrizione;
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true; // 1. Memory reference check
        if (o == null || getClass() != o.getClass()) return false; // 2. Class check
        TagProdotto that = (TagProdotto) o;
        // 3. Compare IDs only if ID is not null
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        // 4. Return a constant or class hash so hashCode doesn't change when ID is generated
        return getClass().hashCode(); 
    }
}
