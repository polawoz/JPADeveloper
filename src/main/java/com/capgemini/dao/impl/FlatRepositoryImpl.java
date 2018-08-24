package com.capgemini.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.capgemini.dao.FlatRepositoryCustom;
import com.capgemini.domain.FlatEntity;
import com.capgemini.types.FlatSearchParamsTO;

@Repository
public class FlatRepositoryImpl implements FlatRepositoryCustom {

	@PersistenceContext
	protected EntityManager entityManager;
	
	
	@Override
	public List<FlatEntity> findUnsoldFlatsByCriteria(FlatSearchParamsTO flatSearchParamsTO) {
		
		//TODO
		
		//JPAQueryFactory queryFactory = new JPAQueryFactory();
		
		
		
		TypedQuery<FlatEntity> query = entityManager.createQuery(createQueryFromParamsTO(flatSearchParamsTO),
				FlatEntity.class);
		
//		if(flatSearchParamsTO.getAreaMin()==null){
//			query.setParameter("areaMin", flatSearchParamsTO.getAreaMin());
//		}
//		if(flatSearchParamsTO.getAreaMax()==null){
//			query.setParameter("areaMax", flatSearchParamsTO.getAreaMax());
//		}
//		if(flatSearchParamsTO.getRoomsCountMin()==null){
//			query.setParameter("roomsCountMin", flatSearchParamsTO.getRoomsCountMin());
//		}
//		if(flatSearchParamsTO.getRoomsCountMax()==null){
//			query.setParameter("roomsCountMax", flatSearchParamsTO.getRoomsCountMax());
//		}
//		if(flatSearchParamsTO.getBalconyCountMin()==null){
//			query.setParameter("balconyCountMin", flatSearchParamsTO.getBalconyCountMin());
//		}
//		if(flatSearchParamsTO.getBalconyCountMax()==null){
//			query.setParameter("balconyCountMin", flatSearchParamsTO.getBalconyCountMax());
//		}

		return query.getResultList();
	}
	
	
	
	private String createQueryFromParamsTO(FlatSearchParamsTO flatSearchParamsTO){
		
		
		String query="SELECT flat FROM FlatEntity flat WHERE (1=1 AND 2=2 AND 3=3)";
		
		if(flatSearchParamsTO.getAreaMin()!=null){
			query= query.replace("1=1","area>"+ flatSearchParamsTO.getAreaMin());
		}
		if(flatSearchParamsTO.getAreaMax()!=null){
			query= query.replace("1=1","area<"+ flatSearchParamsTO.getAreaMax());
		}
		if(flatSearchParamsTO.getRoomsCountMin()!=null){
			query= query.replace("2=2","roomsCount>"+ flatSearchParamsTO.getRoomsCountMin());
		}
		if(flatSearchParamsTO.getRoomsCountMax()!=null){
			query= query.replace("2=2","roomsCount<"+ flatSearchParamsTO.getRoomsCountMax());
		}
		if(flatSearchParamsTO.getBalconyCountMin()!=null){
			query= query.replace("3=3", "balconyCount>"+ flatSearchParamsTO.getBalconyCountMin());
		}
		if(flatSearchParamsTO.getBalconyCountMax()!=null){
			query= query.replace("3=3", "balconyCount<"+ flatSearchParamsTO.getBalconyCountMax());
		}
		
		return query;
	}



	@Override
	public List<FlatEntity> findFlatsDisabledSuitable() {

		TypedQuery<FlatEntity> query = entityManager.createQuery("SELECT flat FROM FlatEntity flat "
				+ "JOIN flat.building building "
				+ "WHERE ((building.hasElevator IS true) OR (flat.floorCount=0))",
				FlatEntity.class);
		return query.getResultList();
		
	}

}
