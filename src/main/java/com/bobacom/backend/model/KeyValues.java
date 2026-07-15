package com.bobacom.backend.model;

import java.sql.SQLType;
import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity atta a contenere coppie chiave valori, ad una chiave stringa possono 
 * essere associati più valori stringa, che sono mappati usando un array JSON
 */
@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="key_values")
public class KeyValues {

	/**
	 * Chiave a cui sono associati valori stringa
	 */
	@Id
	@Column(length = 1024, name = "key_for_storage")//key è una parola riservata per h2, per cui non va bene come identificatore
	private String key;
	
	/**
	 * Valori stringa associati alla chiave memorizzati come array JSON
	 */
	@JdbcTypeCode(SqlTypes.JSON_ARRAY)
	@Column(name = "values_stored",nullable = true)//values è una parola riservata di sql e ad alcune implementazioni non piace
	private List<String> values;
	
}
