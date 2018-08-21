package com.capgemini.service;

import java.util.List;

import com.capgemini.types.BuildingTO;

public interface BuildingService {

	
	BuildingTO addBuilding(BuildingTO newBuilding);
	
	BuildingTO findBuildingById(BuildingTO building);
	
	List<BuildingTO> findAll();
	
	
}
