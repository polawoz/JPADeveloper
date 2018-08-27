package com.capgemini.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.capgemini.dao.ClientRepositoryCustom;
import com.capgemini.domain.ClientEntity;
import com.capgemini.domain.QClientEntity;
import com.capgemini.domain.QFlatEntity;
import com.capgemini.domain.enums.FlatStatus;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;


public class ClientRepositoryImpl implements ClientRepositoryCustom {

	@PersistenceContext
	protected EntityManager entityManager;

	@Override
	public List<ClientEntity> findClientsWhoBoughtFlatsMoreThan(Long flatNumber) {
		
		QClientEntity client = QClientEntity.clientEntity;
		QClientEntity cO = new QClientEntity("cO");
		QFlatEntity flat = QFlatEntity.flatEntity;
		JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
		
		List<ClientEntity> result = 
				queryFactory
				.selectFrom(client)
				.where(JPAExpressions
						.select(flat.count())
						.from(flat)
						.leftJoin(flat.coOwners,cO)
						.where(flat.status.eq(FlatStatus.SOLD).and(flat.owner.eq(client).or(cO.eq(client)))).gt(flatNumber)
						)
				.fetch();


		return result;
	
	}

}
