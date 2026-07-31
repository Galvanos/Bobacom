package com.bobacom.backend.model;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="promozione")
public class Promozione {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	private Float sconto;
	
	@Column
	private Boolean isActive;
	
	@JsonIgnore
	@ManyToMany(mappedBy = "promozione")
	List<Prodotto> prodotto;
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true; // 1. Memory reference check
        if (o == null || getClass() != o.getClass()) return false; // 2. Class check
        Promozione that = (Promozione) o;
        // 3. Compare IDs only if ID is not null
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        // 4. Return a constant or class hash so hashCode doesn't change when ID is generated
        return getClass().hashCode(); 
    }
}
