package com.bobacom.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bobacom.backend.model.Composizione;

public interface IComposizioneRepository extends JpaRepository<Composizione, Integer>{
	@Query(
			value = """
					select c from Composizione c
					left join c.prodotto prodotto
						where(:idProdotto is null or prodotto.id = :idProdotto)
					""")
	List<Composizione> searchByFilter(@Param("idProdotto") Integer idProdotto);
}
