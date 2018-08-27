package com.capgemini.dao;

import java.util.List;

import com.capgemini.domain.BuildingEntity;
import com.capgemini.domain.enums.FlatStatus;

public interface BuildingRepositoryCustom {
	
	Double countPricesSumOfFlatsBoughtByClient(Long clientId);
	
	Double countAveragePriceOfFlatInTheBuilding(Long buildingId);
	
	Long countNumberOfFlatsByStatus(FlatStatus flatStatus, Long buildingId);
	
	List<BuildingEntity> findBuildingsWithMaxmimumNumberFreeFlats();
	

	

	

}
