package com.capgemini.service;

import java.util.List;

import com.capgemini.domain.BuildingEntity;
import com.capgemini.domain.enums.FlatStatus;
import com.capgemini.types.BuildingTO;
import com.capgemini.types.ClientTO;
import com.capgemini.types.FlatSearchParamsTO;
import com.capgemini.types.FlatTO;

public interface BuildingService {

	
	BuildingTO addBuilding(BuildingTO newBuilding);
	
	BuildingTO findBuildingById(BuildingTO building);
	
	BuildingTO updateBuilding(BuildingTO building);
	
	BuildingTO removeBuilding(BuildingTO building);
	
	List<BuildingTO> findAllBuildings();
	
	
	FlatTO addFlat(FlatTO newFlat, BuildingTO building);
	
	FlatTO findFlatById(FlatTO flat);
	
	FlatTO updateFlat(FlatTO flat);
	
	FlatTO removeFlat(FlatTO flat);
	
	
	//queries
	
	Double countPricesSumOfFlatsBoughtByClient(ClientTO client);
	
	Double countAveragePriceOfFlatInTheBuilding(BuildingTO building);
	
	List<FlatTO> findUnsoldFlatsByCriteria(FlatSearchParamsTO flatSearchParamsTO);
	
	Long countNumberOfFlatsByStatus(FlatStatus flatStatus, Long buildingId);
	
	List<BuildingTO> findBuildingsWithMaxmimumNumberFreeFlats();
	
	
	
	
	
	
}
