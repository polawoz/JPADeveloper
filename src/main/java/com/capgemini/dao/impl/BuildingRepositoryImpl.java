package com.capgemini.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.capgemini.dao.BuildingRepositoryCustom;
import com.capgemini.domain.BuildingEntity;
import com.capgemini.domain.enums.FlatStatus;

public class BuildingRepositoryImpl implements BuildingRepositoryCustom {

	@PersistenceContext
	protected EntityManager entityManager;

	// @Override
	// public Double countPricesSumOfFlatsBoughtByClient(Long clientId) {
	//
	// TypedQuery<Double> query = entityManager.createQuery("SELECT
	// SUM(flat.price) FROM FlatEntity flat "
	// + "JOIN flat.coOwners coOwner "
	// + "WHERE (flat.status='SOLD' AND (flat.owner.id =:clientId OR
	// coOwner.id=:clientId))",
	// Double.class);
	// query.setParameter("clientId", clientId);
	// return query.getSingleResult();
	//
	// }

	@Override
	public Double countPricesSumOfFlatsBoughtByClient(Long clientId) {
		//TODO
		TypedQuery<Double> query = entityManager.createQuery("SELECT SUM(flat.price) FROM FlatEntity flat "
	//			+ "JOIN flat.coOwners coOwner "
				+ "WHERE (flat.status='SOLD' AND flat.owner.id =:clientId)",
				Double.class);
		query.setParameter("clientId", clientId);
		return query.getSingleResult();

	}

	@Override
	public Double countAveragePriceOfFlatInTheBuilding(Long buildingId) {

		TypedQuery<Double> query = entityManager.createQuery(
				"SELECT AVG(flat.price) FROM FlatEntity flat " + "WHERE (flat.building.id =:buildingId)", Double.class);
		query.setParameter("buildingId", buildingId);
		return query.getSingleResult();
	}

	@Override
	public Long countNumberOfFlatsByStatus(FlatStatus flatStatus, Long buildingId) {

		TypedQuery<Long> query = entityManager
				.createQuery(
						"SELECT COUNT(flat) FROM BuildingEntity building " + "JOIN building.flats flat "
								+ "WHERE (building.id =:buildingId AND upper(flat.status) =upper(:flatStatus))",
						Long.class);
		query.setParameter("flatStatus", flatStatus.toString());
		query.setParameter("buildingId", buildingId);
		return query.getSingleResult();

	}

	@Override
	public List<BuildingEntity> findBuildingsWithMaxmimumNumberFreeFlats() {
		//TODO
		TypedQuery<BuildingEntity> query = entityManager.createQuery(
				"SELECT building FROM BuildingEntity building " 
				+ "JOIN building.flats flat ",
				BuildingEntity.class);
		return query.getResultList();


	}

}
