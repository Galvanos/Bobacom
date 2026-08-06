package com.bobacom.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bobacom.backend.model.TagProdotto;

public interface ITagProdottoRepository extends JpaRepository<TagProdotto, Integer>{
	List<TagProdotto> findAllByOrderById();
}
