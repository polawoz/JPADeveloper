package com.capgemini.dao;

import java.util.List;

import com.capgemini.domain.ClientEntity;

public interface ClientRepositoryCustom {
	
	
	
	
	List<ClientEntity> findClientsWhoBoughtFlatsMoreThan(Long flatNumber);
	
	
	

}
