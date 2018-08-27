package com.capgemini.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.capgemini.dao.ClientRepositoryCustom;
import com.capgemini.domain.ClientEntity;
import com.capgemini.domain.QClientEntity;
import com.capgemini.domain.QFlatEntity;
import com.capgemini.domain.enums.FlatStatus;
import com.mysema.query.jpa.impl.JPASubQuery;
import com.mysema.query.types.EntityPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;


public class ClientRepositoryImpl implements ClientRepositoryCustom {

	@PersistenceContext
	protected EntityManager entityManager;

	@Override
	public List<ClientEntity> findClientsWhoBoughtFlatsMoreThan(Long flatNumber) {
		// TODO
		
		
		QClientEntity client = QClientEntity.clientEntity;
		
		QClientEntity cO = new QClientEntity("cO");
		
		QFlatEntity flat = QFlatEntity.flatEntity;
		
		
		
		JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
		
		
		List<ClientEntity> result = queryFactory
									.selectFrom(client)
									.where(JPAExpressions.select(flat.count()).from(flat).leftJoin(flat.coOwners,cO)
											.where(flat.status.eq(FlatStatus.SOLD).and(flat.owner.eq(client).or(cO.eq(client)))).gt(flatNumber)
											).fetch();

		
		TypedQuery<ClientEntity> query = entityManager.createQuery("SELECT client FROM ClientEntity client "
				+ "WHERE ("
					+ "SELECT count(flat) from FlatEntity flat left join flat.coOwners coOwner "
					+ "WHERE (flat.status= 'SOLD' AND (flat.owner=client OR coOwner=client)) )"
				+ ">:boughtFlatsNumber ",
		 ClientEntity.class);
		
		
		 query.setParameter("boughtFlatsNumber", flatNumber);

	

		
		
		
		
	//return query.getResultList();

		return result;
	
	}

}
