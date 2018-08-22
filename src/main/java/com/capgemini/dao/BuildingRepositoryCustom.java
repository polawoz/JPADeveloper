package com.capgemini.dao;

public interface BuildingRepositoryCustom {
	
	Double countPricesSumOfFlatsBoughtByClient(Long clientId);
	
	Double countAveragePriceOfFlatInTheBuilding(Long buildingId);
	
	
	

	

}
