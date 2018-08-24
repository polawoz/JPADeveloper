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
import com.querydsl.jpa.impl.JPAQueryFactory;

public class ClientRepositoryImpl implements ClientRepositoryCustom {

	@PersistenceContext
	protected EntityManager entityManager;

	@Override
	public List<ClientEntity> findClientsWhoBoughtFlatsMoreThan(Long flatNumber) {
		// TODO
		
		
		QClientEntity client = QClientEntity.clientEntity;
		
		QFlatEntity flatOwned = QFlatEntity.flatEntity;
		
		QFlatEntity flatCoOwned = QFlatEntity.flatEntity;

		JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
		
		
		
		List<ClientEntity> result = queryFactory
									.selectFrom(client).join(client.flatsOwned, flatOwned)
									.join(client.flatsCoOwned, flatCoOwned)
									.where(flatOwned.status.eq(FlatStatus.SOLD).or(flatCoOwned.status.eq(FlatStatus.SOLD)))
									.fetch();
		
		
		TypedQuery<ClientEntity> query = entityManager.createQuery("SELECT client FROM ClientEntity client "
				+ "JOIN client.flatsOwned flatOwned JOIN client.flatsCoOwned flatCoOwned "
				+ "WHERE ((flatOwned.status='SOLD' OR flatCoOwned.status='SOLD'))", ClientEntity.class);
		// query.setParameter("boughtFlatsNumber", flatNumber);

	

		
		
		
		
		return query.getResultList();

	//	return result;
	}

}
