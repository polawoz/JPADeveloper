package com.capgemini.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.capgemini.dao.BuildingRepositoryCustom;
import com.capgemini.domain.BuildingEntity;
import com.capgemini.domain.QBuildingEntity;
import com.capgemini.domain.QFlatEntity;
import com.capgemini.domain.enums.FlatStatus;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class BuildingRepositoryImpl implements BuildingRepositoryCustom {

	@PersistenceContext
	protected EntityManager entityManager;
	

	@Override
	public Double countPricesSumOfFlatsBoughtByClient(Long clientId) {
	
		QFlatEntity flat = QFlatEntity.flatEntity;
		JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
		Double result = queryFactory
			.select(flat.price.sum()).from(flat)
			.where(
				flat.status.eq(FlatStatus.SOLD).and(
													flat.owner.id.eq(clientId).or(flat.coOwners.any().id.eq(clientId))))
				.fetchOne();
		
		return result;
		

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
		
		QBuildingEntity building = QBuildingEntity.buildingEntity;
		QFlatEntity flat = QFlatEntity.flatEntity;
		JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
	
		Long max = queryFactory
				.select(flat.count())
				.from(building)
				.join(building.flats, flat)
				.where(flat.status.eq(FlatStatus.FREE))
				.groupBy(building)
				.orderBy(flat.count().desc())
				.limit(1)
				.fetchOne();
		
		if(max==null){
			return null;
		}
		
		List<BuildingEntity> resultMax = queryFactory.selectFrom(building)
				.where(
							JPAExpressions.select(flat.count()).from(building.flats, flat)
							.where(flat.status.eq(FlatStatus.FREE))
						.eq(max))
				.fetch();
	
		return resultMax;

	}

}
