package com.capgemini.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.capgemini.domain.ClientEntity;

@Repository
public interface ClientDao extends CrudRepository<ClientEntity, Long> {

	ClientEntity save(ClientEntity client);

	ClientEntity findById(Long id);

	void delete(Long id);

	List<ClientEntity> findAll();

}
