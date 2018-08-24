package com.capgemini.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.capgemini.dao.FlatRepositoryCustom;
import com.capgemini.domain.FlatEntity;
import com.capgemini.domain.QFlatEntity;
import com.capgemini.types.FlatSearchParamsTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;


@Repository
public class FlatRepositoryImpl implements FlatRepositoryCustom {

	@PersistenceContext
	protected EntityManager entityManager;
	
	
	@Override
	public List<FlatEntity> findUnsoldFlatsByCriteria(FlatSearchParamsTO flatSearchParamsTO) {
		
		QFlatEntity flat = QFlatEntity.flatEntity;
		
		JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
		
		BooleanBuilder builder = new BooleanBuilder();
		
		if(flatSearchParamsTO.getAreaMin()!=null){
		builder.and(flat.area.gt(flatSearchParamsTO.getAreaMin()));
		}
		if(flatSearchParamsTO.getAreaMax()!=null){
		builder.and(flat.area.lt(flatSearchParamsTO.getAreaMax()));
		}
		if(flatSearchParamsTO.getRoomsCountMin()!=null){
			builder.and(flat.area.gt(flatSearchParamsTO.getRoomsCountMin()));
		}
		if(flatSearchParamsTO.getRoomsCountMax()!=null){
			builder.and(flat.area.lt(flatSearchParamsTO.getRoomsCountMax()));
		}
		if(flatSearchParamsTO.getBalconyCountMin()!=null){
			builder.and(flat.area.gt(flatSearchParamsTO.getBalconyCountMin()));
		}
		if(flatSearchParamsTO.getBalconyCountMax()!=null){
			builder.and(flat.area.lt(flatSearchParamsTO.getBalconyCountMax()));
		}
		

		List<FlatEntity> result = queryFactory.selectFrom(flat).where(builder).fetch();
		
		
		return result;
		


	}
	
	
	@Override
	public List<FlatEntity> findFlatsDisabledSuitable() {

		
		QFlatEntity flat = QFlatEntity.flatEntity;
		
		JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
		
		List<FlatEntity> result = queryFactory
									.selectFrom(flat)
									.where((flat.building.hasElevator.isTrue()).or(flat.floorCount.eq(0)))
									.fetch();
		
		
		return result;
		
		
	}

}
