package com.bobacom.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bobacom.backend.model.OperazioneMagazzino;

public interface IOperazioneMagazzinoRepository extends JpaRepository<OperazioneMagazzino, Integer>{
	@Query(
			value = """
					select o from OperazioneMagazzino o
						join o.ingrediente ingrediente
						where(:id is null or o.id = :id)
							and (:idIngrediente is null or ingrediente.id = :idIngrediente)
							and (cast(:dataMax as localdate) is null or o.data <= :dataMax)
							and (cast(:dataMin as localdate) is null or o.data >= :dataMin)
							and (:positivo is null or 
								(:positivo = true and o.deltaQuantita >= 0) or
								(:positivo = false and o.deltaQuantita < 0))
					""")
	List<OperazioneMagazzino> searchByFilter(
												@Param("id") Integer id,
												@Param("idIngrediente") Integer idIngrediente,
												@Param("dataMax") LocalDate dataMax,
												@Param("dataMin") LocalDate dataMin,
												@Param("positivo") Boolean positivo);
}
