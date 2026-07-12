package com.bobacom.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bobacom.backend.model.Ingrediente;

public interface IIngredienteRepository  extends JpaRepository<Ingrediente, Integer>{

}
