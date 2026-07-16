package com.bobacom.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bobacom.backend.model.Ordine;

public interface IOrdineRepository extends JpaRepository<Ordine, Integer>{

}
