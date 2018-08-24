package com.capgemini.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.capgemini.domain.ClientEntity;

public interface ClientRepository extends JpaRepository<ClientEntity, Long>, ClientRepositoryCustom {

	
//	@SuppressWarnings("unchecked")
//	ClientEntity save(ClientEntity client);

//	ClientEntity findById(Long id);

//	void delete(Long id);
//
//	List<ClientEntity> findAll();
	
	
//	@Query
//	List<ClientEntity> findClientWithName();
	
	

}
