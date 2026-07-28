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
						left join p.promozione promozione
							on (:isActive is null or promozione.isActive = :isActive)
							left join p.tag tag
								where(:tag is null or tag.nome = :tag)
					""")
	List<Prodotto> searchByFilter(	@Param("tag") String tag,
									@Param("isActive") Boolean isActive);
}