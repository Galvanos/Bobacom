package com.bobacom.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bobacom.backend.model.OrdineProdotto;

public interface IOrdineProdottoRepository extends JpaRepository<OrdineProdotto, Integer>{

}
