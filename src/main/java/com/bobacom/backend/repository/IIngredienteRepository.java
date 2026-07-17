package com.bobacom.backend.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bobacom.backend.model.Ingrediente;

public interface IIngredienteRepository  extends JpaRepository<Ingrediente, Integer>{
	@Query(
			value = """
					select i from Ingrediente i
						join i.categoriaIngrediente categoria
						where(:id is null or i.id = :id)
							and (:idCategoria is null or categoria.id = :idCategoria)
							and (:maxAmount is null or i.quantitaStock <= :maxAmount)
					""")
	List<Ingrediente> searchByFilter(
												@Param("id") Integer id,
												@Param("idCategoria") Integer idCategoria,
												@Param("maxAmount") BigDecimal maxAmount);
}
