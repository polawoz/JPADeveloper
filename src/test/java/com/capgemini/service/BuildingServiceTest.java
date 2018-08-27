package com.capgemini.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.capgemini.domain.Address;
import com.capgemini.domain.enums.FlatStatus;
import com.capgemini.exception.CannotPerformActionException;
import com.capgemini.types.BuildingTO;
import com.capgemini.types.FlatSearchParamsTO;
import com.capgemini.types.FlatTO;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=hsql")
public class BuildingServiceTest {

	@Autowired
	private BuildingService buildingService;

	@Test
	public void testShouldAddBuilding() {

		// given
		BuildingTO buildingToAdd = createTestBuilding();
		buildingToAdd.setDescription("Opis");
		int buildingsListSizeBefore = buildingService.findAllBuildings().size();

		// when
		BuildingTO addedBuilding = buildingService.addBuilding(buildingToAdd);

		// then
		assertEquals(buildingToAdd.getDescription(), addedBuilding.getDescription());
		assertEquals(buildingsListSizeBefore + 1, buildingService.findAllBuildings().size());

	}

	@Test
	public void testShouldFindBuildingById() {

		// given
		BuildingTO buildingToAdd = createTestBuilding();
		buildingToAdd.setDescription("Opis");
		BuildingTO addedBuilding = buildingService.addBuilding(buildingToAdd);

		// when
		BuildingTO foundBuilding = buildingService.findBuildingById(addedBuilding);

		// then
		assertNotNull(foundBuilding);
		assertEquals(addedBuilding.getId(), foundBuilding.getId());
		assertEquals(addedBuilding.getDescription(), foundBuilding.getDescription());

	}

	@Test
	public void testShouldFindAllBuildings() {

		// given
		int foundBuildingsListSizeBefore = buildingService.findAllBuildings().size();

		BuildingTO buildingToAdd = createTestBuilding();
		buildingService.addBuilding(buildingToAdd);

		BuildingTO secondBuildingToAdd = createTestBuilding();
		buildingService.addBuilding(secondBuildingToAdd);

		BuildingTO thirdBuildingToAdd = createTestBuilding();
		buildingService.addBuilding(thirdBuildingToAdd);

		// when
		List<BuildingTO> foundBuildings = buildingService.findAllBuildings();

		// then
		assertNotNull(foundBuildings);
		assertEquals(foundBuildingsListSizeBefore + 3, foundBuildings.size());

	}

	@Test
	public void testShouldUpdateBuildingStoreysNumber() {

		// given
		// Building

		BuildingTO buildingToAdd = createTestBuilding();
		buildingToAdd.setStoreysNumber(1);
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		BuildingTO updateParameters = BuildingTO.builder().id(savedBuilding.getId()).storeysNumber(4)
				.version(savedBuilding.getVersion()).build();

		// when
		BuildingTO updatedBuilding = buildingService.updateBuilding(updateParameters);

		// then

		BuildingTO buildingAfterUpdate = buildingService.findBuildingById(savedBuilding);
		assertEquals(updateParameters.getStoreysNumber(), updatedBuilding.getStoreysNumber());
		assertEquals(savedBuilding.getVersion().longValue() + 1, buildingAfterUpdate.getVersion().longValue());

	}

	@Test
	public void testShouldNotUpdateBuildingStoreysNumberOptimisticLocking() {

		// given
		// Building

		BuildingTO buildingToAdd = createTestBuilding();
		buildingToAdd.setStoreysNumber(1);
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		BuildingTO buildingToUpdateFoundByFirstUser = buildingService.findBuildingById(savedBuilding);
		BuildingTO buildingToUpdateFoundBySecondUser = buildingService.findBuildingById(savedBuilding);

		BuildingTO updateParametersFirstUser = BuildingTO.builder().id(buildingToUpdateFoundByFirstUser.getId())
				.storeysNumber(4).version(buildingToUpdateFoundByFirstUser.getVersion()).build();

		BuildingTO updateParametersSecondUser = BuildingTO.builder().id(buildingToUpdateFoundBySecondUser.getId())
				.storeysNumber(10).version(buildingToUpdateFoundBySecondUser.getVersion()).build();

		boolean exceptionThrown = false;
		// when
		BuildingTO updatedBuildingSecondUser = buildingService.updateBuilding(updateParametersSecondUser);

		try {
			buildingService.updateBuilding(updateParametersFirstUser);
		} catch (CannotPerformActionException e) {
			exceptionThrown = true;
			e.printStackTrace();
		}

		// then

		BuildingTO buildingAfterBothUpdates = buildingService.findBuildingById(savedBuilding);
		assertTrue(exceptionThrown);
		assertEquals(updateParametersSecondUser.getStoreysNumber(), updatedBuildingSecondUser.getStoreysNumber());
		assertEquals(updateParametersSecondUser.getStoreysNumber(), buildingAfterBothUpdates.getStoreysNumber());

	}

	@Test
	public void testShouldRemoveBuilding() {

		// given
		BuildingTO buildingToAdd = createTestBuilding();
		buildingToAdd.setStoreysNumber(1);
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		// when
		buildingService.removeBuilding(savedBuilding);

		// then
		BuildingTO foundBuilding = buildingService.findBuildingById(savedBuilding);
		assertNull(foundBuilding);

	}

	@Test
	public void testShouldRemoveBuildingAndItsFlatsCascade() {

		// Building
		BuildingTO buildingToAdd = createTestBuilding();
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		// Flat
		FlatTO flatToAdd = createTestFlat();
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);

		FlatTO secondFlatToAdd = createTestFlat();
		FlatTO secondSavedFlat = buildingService.addFlat(secondFlatToAdd, savedBuilding);

		// when
		buildingService.removeBuilding(savedBuilding);

		// then
		BuildingTO foundBuilding = buildingService.findBuildingById(savedBuilding);
		FlatTO foundFlatFirst = buildingService.findFlatById(savedFlat);
		FlatTO foundFlatSecond = buildingService.findFlatById(secondSavedFlat);
		assertNull(foundBuilding);
		assertNull(foundFlatFirst);
		assertNull(foundFlatSecond);

	}

	@Test
	public void testShouldAddFlat() {

		// given
		BuildingTO buildingToAdd = createTestBuilding();
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);
		FlatTO flatToAdd = createTestFlat();
		flatToAdd.setBalconyCount(29);

		// when
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);

		// then
		FlatTO foundFlat = buildingService.findFlatById(savedFlat);
		BuildingTO foundBuilding = buildingService.findBuildingById(savedBuilding);
		assertNotNull(foundFlat);
		assertEquals(flatToAdd.getBalconyCount(), foundFlat.getBalconyCount());
		assertTrue(foundBuilding.getFlats().stream().anyMatch(b -> b.equals(savedFlat.getId())));
		assertTrue(foundFlat.getBuildingId().equals(savedBuilding.getId()));
		assertEquals(1, foundBuilding.getFlatCount().longValue());

	}

	@Test
	public void testFindFlatById() {

		// given

		BuildingTO buildingToAdd = createTestBuilding();
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);
		FlatTO flatToAdd = createTestFlat();
		flatToAdd.setBalconyCount(29);
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);

		// when
		FlatTO foundFlat = buildingService.findFlatById(savedFlat);

		// then
		assertNotNull(foundFlat);
		assertEquals(savedFlat.getId(), foundFlat.getId());
		assertEquals(savedFlat.getBalconyCount(), foundFlat.getBalconyCount());

	}

	@Test
	public void testShouldUpdateFlatFloorCount() {

		// given
		// Building

		BuildingTO buildingToAdd = createTestBuilding();
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		// Flat
		FlatTO flatToAdd = createTestFlat();
		flatToAdd.setFloorCount(2);
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);

		FlatTO updateParameters = FlatTO.builder().id(savedFlat.getId()).floorCount(3).version(savedFlat.getVersion())
				.build();

		// when
		FlatTO updatedFlat = buildingService.updateFlat(updateParameters);

		// then
		FlatTO foundFlatAfterUpdate = buildingService.findFlatById(updatedFlat);
		assertEquals(updateParameters.getFloorCount(), updatedFlat.getFloorCount());
		assertEquals(savedFlat.getVersion().longValue() + 1, foundFlatAfterUpdate.getVersion().longValue());

	}

	@Test
	public void testShouldNotUpdateFlatFloorCountOptimisticLocking() {

		// given
		// Building
		BuildingTO buildingToAdd = createTestBuilding();
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		// Flat
		FlatTO flatToAdd = createTestFlat();
		flatToAdd.setFloorCount(3);
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);

		FlatTO flatToUpdateFoundByFirstUser = buildingService.findFlatById(savedFlat);
		FlatTO flatToUpdateFoundBySecondUser = buildingService.findFlatById(savedFlat);

		FlatTO updateParametersFirstUser = FlatTO.builder().id(savedFlat.getId()).floorCount(3)
				.version(flatToUpdateFoundByFirstUser.getVersion()).build();

		FlatTO updateParametersSecondUser = FlatTO.builder().id(savedFlat.getId()).floorCount(7)
				.version(flatToUpdateFoundBySecondUser.getVersion()).build();

		boolean exceptionThrown = false;
		// when
		FlatTO updatedFlatSecondUser = buildingService.updateFlat(updateParametersSecondUser);

		try {
			buildingService.updateFlat(updateParametersFirstUser);
		} catch (CannotPerformActionException e) {
			exceptionThrown = true;
			e.printStackTrace();
		}

		// then

		FlatTO flatAfterBothUpdates = buildingService.findFlatById(savedFlat);
		assertTrue(exceptionThrown);
		assertEquals(updateParametersSecondUser.getFloorCount(), updatedFlatSecondUser.getFloorCount());
		assertEquals(updateParametersSecondUser.getFloorCount(), flatAfterBothUpdates.getFloorCount());

	}

	@Test
	public void testShouldRemoveFlat() {

		// given
		BuildingTO buildingToAdd = createTestBuilding();
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);
		FlatTO flatToAdd = createTestFlat();
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);

		// when
		buildingService.removeFlat(savedFlat);

		// then
		FlatTO foundFlat = buildingService.findFlatById(savedFlat);
		BuildingTO foundBuilding = buildingService.findBuildingById(savedBuilding);
		assertNull(foundFlat);
		assertTrue(foundBuilding.getFlats().stream().noneMatch(b -> b.equals(savedFlat.getId())));
		assertEquals(0, foundBuilding.getFlatCount().longValue());

	}

	@Test
	public void testShouldFindFlatsByAreaCriteria() {

		// given

		// Search params
		FlatSearchParamsTO searchParams = FlatSearchParamsTO.builder().areaMin(20D).areaMax(35d).build();

		int foundFlatsListBeforeSize = buildingService.findUnsoldFlatsByCriteria(searchParams).size();

		// Building
		BuildingTO buildingToAdd = createTestBuilding();
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		// Flat
		FlatTO flatToAdd = createTestFlat();
		flatToAdd.setArea(30D);
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);

		FlatTO secondFlatToAdd = createTestFlat();
		secondFlatToAdd.setArea(40D);
		buildingService.addFlat(secondFlatToAdd, savedBuilding);

		FlatTO thirdFlatToAdd = createTestFlat();
		thirdFlatToAdd.setArea(18D);
		buildingService.addFlat(thirdFlatToAdd, savedBuilding);
		// when
		List<FlatTO> foundFlats = buildingService.findUnsoldFlatsByCriteria(searchParams);

		// then
		assertEquals(foundFlatsListBeforeSize + 1, foundFlats.size());
		assertTrue(foundFlats.stream().anyMatch(b -> b.getId().equals(savedFlat.getId())));

	}

	@Test
	public void testShouldFindFlatsByAreaAndBalconyCriteria() {

		// given

		// Search params
		FlatSearchParamsTO searchParams = FlatSearchParamsTO.builder().areaMin(25d).balconyCountMin(1).build();

		int foundFlatsListBeforeSize = buildingService.findUnsoldFlatsByCriteria(searchParams).size();

		// Building
		BuildingTO buildingToAdd = createTestBuilding();
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		// Flat
		FlatTO flatToAdd = createTestFlat();
		flatToAdd.setArea(30D);
		flatToAdd.setBalconyCount(0);
		buildingService.addFlat(flatToAdd, savedBuilding);

		FlatTO secondFlatToAdd = createTestFlat();
		secondFlatToAdd.setArea(40D);
		secondFlatToAdd.setBalconyCount(1);
		FlatTO secondSavedFlat = buildingService.addFlat(secondFlatToAdd, savedBuilding);

		FlatTO thirdFlatToAdd = createTestFlat();
		thirdFlatToAdd.setArea(18D);
		thirdFlatToAdd.setBalconyCount(1);
		buildingService.addFlat(thirdFlatToAdd, savedBuilding);
		// when
		List<FlatTO> foundFlats = buildingService.findUnsoldFlatsByCriteria(searchParams);

		// then
		assertEquals(foundFlatsListBeforeSize + 1, foundFlats.size());
		assertTrue(foundFlats.stream().anyMatch(b -> b.getId().equals(secondSavedFlat.getId())));

	}

	private BuildingTO createTestBuilding() {

		Address address = Address.builder().street("Niepodleglosci").buildingNumber("1").flatNumber("2")
				.postalCode("60-777").town("Poznan").country("Polska").build();

		BuildingTO createdBuilding = BuildingTO.builder().description("Nowy budynek").location(address).storeysNumber(1)
				.hasElevator(false).flatCount(0).build();

		return createdBuilding;
	}

	private FlatTO createTestFlat() {

		Address address = Address.builder().street("Niepodleglosci").buildingNumber("1").flatNumber("2")
				.postalCode("60-777").town("Poznan").country("Polska").build();

		FlatTO createdFlat = FlatTO.builder().area(0d).roomsCount(0).balconyCount(0).floorCount(0).location(address)
				.status(FlatStatus.FREE).price(300000d).build();

		return createdFlat;
	}

}
