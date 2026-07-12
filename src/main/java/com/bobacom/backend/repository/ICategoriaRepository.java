package com.bobacom.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bobacom.backend.model.CategoriaIngrediente;

public interface ICategoriaRepository extends JpaRepository<CategoriaIngrediente, Integer>{

}
