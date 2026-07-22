package com.bobacom.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bobacom.backend.model.Utente;
import java.util.List;
import java.util.Optional;


public interface IUtenteRepository extends JpaRepository<Utente, Integer>,JpaSpecificationExecutor<Utente> {

	Optional<Utente> findByUsername(String username);
	boolean existsByUsername(String username);
}
