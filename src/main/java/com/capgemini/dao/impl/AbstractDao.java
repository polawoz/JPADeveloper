package com.capgemini.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.dao.Dao;

@Transactional(Transactional.TxType.SUPPORTS)
public abstract class AbstractDao<T, K extends Serializable> implements Dao<T, K> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDao.class);

	@PersistenceContext
	protected EntityManager entityManager;

	private Class<T> domainClass;

	@Override
	public T save(T entity) {
		entityManager.persist(entity);
		return entity;
	}

	@Override
	public T getOne(K id) {
		return entityManager.getReference(getDomainClass(), id);
	}

	@Override
	public T findOne(K id) {

		T entity = entityManager.find(getDomainClass(), id);
		if (entity == null) {
			LOGGER.info("No " + getDomainClass().getSimpleName() + " with id: " + id);
		}
		return entity;
	}

	@Override
	public List<T> findAll() {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = builder.createQuery(getDomainClass());
		criteriaQuery.from(getDomainClass());
		TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
		return query.getResultList();
	}

	@Override
	public T update(T entity) {
		return entityManager.merge(entity);
	}

	@Override
	public void delete(T entity) {
		entityManager.remove(entity);
	}

	@Override
	public void delete(K id) {
		entityManager.remove(getOne(id));
	}

	@Override
	public void deleteAll() {
		entityManager.createQuery("delete " + getDomainClassName()).executeUpdate();
	}

	@Override
	public long count() {
		return (long) entityManager.createQuery("Select count(*) from " + getDomainClassName()).getSingleResult();
	}

	@Override
	public boolean exists(K id) {
		return findOne(id) != null;
	}

	@SuppressWarnings("unchecked")
	protected Class<T> getDomainClass() {
		if (domainClass == null) {
			ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
			domainClass = (Class<T>) type.getActualTypeArguments()[0];
		}
		return domainClass;
	}

	protected String getDomainClassName() {
		return getDomainClass().getName();
	}

	// --------- optimistic locking
	@Override
	public void flush() {
		entityManager.flush();

	}

	@Override
	public void detach(T entity) {
		entityManager.detach(entity);

	}

}