package com.bobacom.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bobacom.backend.model.Prodotto;

public interface IProdottoRepository extends JpaRepository<Prodotto, Integer>{
	@Query(
			value = """
					select distinct p from Prodotto p
						left join FETCH p.promozione promozione
						left join FETCH p.tag tag
							where(:tag is null or tag.nome = :tag)
							and (:isActive is null or promozione.isActive = :isActive)							
					""")
	List<Prodotto> searchByFilter(	@Param("tag") String tag,
									@Param("isActive") Boolean isActive);
}