package com.bobacom.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bobacom.backend.model.KeyValues;

public interface IKeyValuesRepository extends JpaRepository<KeyValues, String>,JpaSpecificationExecutor<KeyValues> {
	
}
