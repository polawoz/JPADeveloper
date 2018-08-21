package com.capgemini.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.capgemini.domain.FlatEntity;

@Repository
public interface FlatDao extends CrudRepository<FlatEntity, Long> {

	FlatEntity save(FlatEntity flat);

	FlatEntity findById(Long id);

	void delete(Long id);

	List<FlatEntity> findAll();

}
