package com.capgemini.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.capgemini.dao.ClientRepositoryCustom;
import com.capgemini.domain.ClientEntity;

public class ClientRepositoryImpl implements ClientRepositoryCustom{

	
	@PersistenceContext
	protected EntityManager entityManager;
	
	
	@Override
	public List<ClientEntity> findClientsWhoBoughtFlatsMoreThan(Long flatNumber) {
		//TODO
		TypedQuery<ClientEntity> query = entityManager.createQuery("SELECT client FROM ClientEntity client "
				+ "JOIN client.flatsOwned flatOwned JOIN client.flatsCoOwned flatCoOwned "
				+ "WHERE ((flatOwned.status='SOLD' OR flatCoOwned.status='SOLD'))",
				ClientEntity.class);
//		query.setParameter("boughtFlatsNumber", flatNumber);

		return query.getResultList();


	}

}
