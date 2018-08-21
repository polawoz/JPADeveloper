package com.capgemini.service;

import java.util.List;

import com.capgemini.types.BuildingTO;
import com.capgemini.types.FlatTO;

public interface BuildingService {

	
	BuildingTO addBuilding(BuildingTO newBuilding);
	
	BuildingTO findBuildingById(BuildingTO building);
	
	List<BuildingTO> findAll();
	
	FlatTO addFlat(FlatTO newFlat, BuildingTO building);
	
	FlatTO findFlatById(FlatTO flat);
	
	
	
	
}
