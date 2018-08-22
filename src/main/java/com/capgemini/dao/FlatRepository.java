package com.capgemini.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capgemini.domain.FlatEntity;


public interface FlatRepository extends JpaRepository<FlatEntity, Long>, FlatRepositoryCustom {

	
//	@SuppressWarnings("unchecked")
//	FlatEntity save(FlatEntity flat);

//	FlatEntity findById(Long id);

//	void delete(Long id);
//
//	List<FlatEntity> findAll();
	
	
	

}
