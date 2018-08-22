package com.capgemini.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capgemini.domain.BuildingEntity;


public interface BuildingRepository extends JpaRepository<BuildingEntity, Long>, BuildingRepositoryCustom {
	
//	@SuppressWarnings("unchecked")
//	BuildingEntity save(BuildingEntity building);
	
// BuildingEntity findById(Long id);
	
//	void delete(Long id);
//	
//	List<BuildingEntity> findAll();
	
	
	
	

}
