package com.capgemini.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.capgemini.domain.Address;
import com.capgemini.domain.enums.FlatStatus;
import com.capgemini.exception.CannotPerformActionException;
import com.capgemini.types.BuildingTO;
import com.capgemini.types.FlatTO;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=hsql")
public class BuildingServiceTest {

	@Autowired
	private BuildingService buildingService;

	
	@Test
	public void testShouldAddBuilding() {
		

		// given
		BuildingTO buildingToAdd = BuildingTO.builder().description("Nowy budynek").location(new Address())
				.storeysNumber(3).hasElevator(true).flatCount(6).build();
		
		int buildingsListSizeBefore = buildingService.findAll().size();

		// when
		BuildingTO addedBuilding = buildingService.addBuilding(buildingToAdd);

		// then
		assertEquals(buildingToAdd.getDescription(), addedBuilding.getDescription());
		assertEquals(buildingsListSizeBefore+1,buildingService.findAll().size());
		

	}
	
	
	@Test
	public void testShouldUpdateFlatFloorCount(){
		
		//given
		//Building
		
		BuildingTO buildingToAdd = BuildingTO.builder()
				.description("Nowy budynek")
				.location(new Address())
				.storeysNumber(1)
				.hasElevator(false)
				.flatCount(0)
				.build();
		
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);
		
		//Flat
		FlatTO flatToAdd = FlatTO.builder()
				.area(30D)
				.roomsCount(2)
				.balconyCount(0)
				.floorCount(2)
				.location(new Address())
				.status(FlatStatus.FREE)
				.price(30000D)
				.build();
		
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);
		
		FlatTO updateParameters = FlatTO.builder()
				.id(savedFlat.getId())
				.floorCount(3)
				.version(savedFlat.getVersion())
				.build();
		
		
		//when
		FlatTO updatedFlat = buildingService.updateFlat(updateParameters);
		FlatTO foundFlatAfterUpdate = buildingService.findFlatById(updatedFlat);
		
		//then
		assertEquals(updateParameters.getFloorCount(), updatedFlat.getFloorCount());
		assertEquals(savedFlat.getVersion().longValue()+1, foundFlatAfterUpdate.getVersion().longValue());
		
		
		
	}
	
	
	
	@Test
	public void testShouldNotUpdateFlatFloorCountOptimisticLocking(){
		
		//given
		//Building
		
		BuildingTO buildingToAdd = BuildingTO.builder()
				.description("Nowy budynek")
				.location(new Address())
				.storeysNumber(1)
				.hasElevator(false)
				.flatCount(0)
				.build();
		
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);
		
		//Flat
		FlatTO flatToAdd = FlatTO.builder()
				.area(30D)
				.roomsCount(2)
				.balconyCount(0)
				.floorCount(2)
				.location(new Address())
				.status(FlatStatus.FREE)
				.price(30000D)
				.build();
		
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);
		
		
		
		FlatTO flatToUpdateFoundByFirstUser= buildingService.findFlatById(savedFlat);
		FlatTO flatToUpdateFoundBySecondUser= buildingService.findFlatById(savedFlat);
		
		
		FlatTO updateParametersFirstUser = FlatTO.builder()
				.id(savedFlat.getId())
				.floorCount(3)
				.version(flatToUpdateFoundByFirstUser.getVersion())
				.build();
		
		FlatTO updateParametersSecondUser = FlatTO.builder()
				.id(savedFlat.getId())
				.floorCount(7)
				.version(flatToUpdateFoundBySecondUser.getVersion())
				.build();
		
		boolean exceptionThrown = false;
		//when
		
		//drugi zmieni liczbe pieter na 7
		FlatTO updatedFlatSecondUser = buildingService.updateFlat(updateParametersSecondUser);
		//pierwszy nie zmieni
		try{
		FlatTO updatedFlatFirstUser = buildingService.updateFlat(updateParametersFirstUser);
		}
		catch(CannotPerformActionException e){
			exceptionThrown=true;
			e.printStackTrace();
		}
		
		
		//then
		
		FlatTO flatAfterBothUpdates = buildingService.findFlatById(savedFlat);
		assertTrue(exceptionThrown);
		assertEquals(updateParametersSecondUser.getFloorCount(), updatedFlatSecondUser.getFloorCount());
		assertEquals(updateParametersSecondUser.getFloorCount(), flatAfterBothUpdates.getFloorCount());
		

		
	}
	
	
	
	
	
	@Test
	public void testShouldNotUpdateBuildingStoreysNumberOptimisticLocking(){
		
		//given
		//Building
		
		BuildingTO buildingToAdd = BuildingTO.builder()
				.description("Nowy budynek")
				.location(new Address())
				.storeysNumber(1)
				.hasElevator(false)
				.flatCount(0)
				.build();
		
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);
		
		BuildingTO buildingToUpdateFoundByFirstUser= buildingService.findBuildingById(savedBuilding);
		BuildingTO buildingToUpdateFoundBySecondUser= buildingService.findBuildingById(savedBuilding);
		
		BuildingTO updateParametersFirstUser = BuildingTO.builder()
				.id(buildingToUpdateFoundByFirstUser.getId())
				.storeysNumber(4)
				.version(buildingToUpdateFoundByFirstUser.getVersion())
				.build();
		
		BuildingTO updateParametersSecondUser = BuildingTO.builder()
				.id(buildingToUpdateFoundBySecondUser.getId())
				.storeysNumber(10)
				.version(buildingToUpdateFoundBySecondUser.getVersion())
				.build();
		
		boolean exceptionThrown = false;
		//when
		
		//drugi zmieni liczbe pieter na 10
		BuildingTO updatedBuildingSecondUser = buildingService.updateBuilding(updateParametersSecondUser);
		//pierwszy nie zmieni
		try{
		BuildingTO updatedBuildingFirstUser = buildingService.updateBuilding(updateParametersFirstUser);
		}
		catch(CannotPerformActionException e){
			exceptionThrown=true;
			e.printStackTrace();
		}
		
		
		//then
		
		BuildingTO buidlingAfterBothUpdates = buildingService.findBuildingById(savedBuilding);
		assertTrue(exceptionThrown);
		assertEquals(updateParametersSecondUser.getStoreysNumber(), updatedBuildingSecondUser.getStoreysNumber());
		assertEquals(updateParametersSecondUser.getStoreysNumber(), buidlingAfterBothUpdates.getStoreysNumber());
		

		
	}
	
	
	
	
	
	
	

}
