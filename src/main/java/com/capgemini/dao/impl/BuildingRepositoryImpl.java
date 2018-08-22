package com.capgemini.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.capgemini.dao.BuildingRepositoryCustom;


public class BuildingRepositoryImpl implements BuildingRepositoryCustom {

	
	@PersistenceContext
	protected EntityManager entityManager;
	
	
	@Override
	public Double countPricesSumOfFlatsBoughtByClient(Long clientId) {

		TypedQuery<Double> query = entityManager.createQuery("SELECT SUM(flat.price) FROM FlatEntity flat "
				+ "WHERE (upper(flat.status) like upper('sold') AND flat.owner.id =:clientId)",
				Double.class);
		query.setParameter("clientId", clientId);
		return query.getSingleResult();

		

	}


	@Override
	public Double countAveragePriceOfFlatInTheBuilding(Long buildingId) {

		
		TypedQuery<Double> query = entityManager.createQuery("SELECT AVG(flat.price) FROM FlatEntity flat "
				+ "WHERE (flat.building.id =:buildingId)",
				Double.class);
		query.setParameter("buildingId", buildingId);
		return query.getSingleResult();
	}

	
	
	
}
