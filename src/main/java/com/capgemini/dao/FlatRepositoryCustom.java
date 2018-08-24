package com.capgemini.dao;

import java.util.List;

import com.capgemini.domain.FlatEntity;
import com.capgemini.types.FlatSearchParamsTO;
import com.capgemini.types.FlatTO;

public interface FlatRepositoryCustom {
	
	
	List<FlatEntity> findUnsoldFlatsByCriteria(FlatSearchParamsTO flatSearchParamsTO);
	
	List<FlatEntity> findFlatsDisabledSuitable();
	

}
