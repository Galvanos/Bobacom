package com.bobacom.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bobacom.backend.model.Utente;

public interface IUtenteRepository extends JpaRepository<Utente, Integer>{

}
