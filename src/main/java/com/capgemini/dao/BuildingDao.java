package com.capgemini.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.capgemini.domain.BuildingEntity;

@Repository
public interface BuildingDao extends CrudRepository<BuildingEntity, Long>{
	
	
	BuildingEntity save(BuildingEntity building);
	
	BuildingEntity findById(Long id);
	
	void delete(Long id);
	
	List<BuildingEntity> findAll();
	
	
	

	
	

}
