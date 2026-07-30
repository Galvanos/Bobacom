package com.bobacom.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bobacom.backend.model.Ordine;
import com.bobacom.backend.model.Utente;

public interface IOrdineRepository extends JpaRepository<Ordine, Integer>{
	List<Ordine> findByUtente(Utente utente);
}
