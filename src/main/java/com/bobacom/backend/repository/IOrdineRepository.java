package com.bobacom.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bobacom.backend.model.Ordine;

public interface IOrdineRepository extends JpaRepository<Ordine, Integer>{

	@Query(
			value = """
					select o from Ordine o
						join o.utente utente
						where(:id is null or o.id = :id)
							and (:idUtente is null or utente.id = :idUtente)
					""")
	List<Ordine> searchByFilter(
												@Param("id") Integer id,
												@Param("idUtente") Integer idUtente);
}
