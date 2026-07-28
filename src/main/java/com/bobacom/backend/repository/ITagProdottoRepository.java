package com.bobacom.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bobacom.backend.model.TagProdotto;

public interface ITagProdottoRepository extends JpaRepository<TagProdotto, Integer>{
}
