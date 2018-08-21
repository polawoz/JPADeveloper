package com.capgemini.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.capgemini.domain.Address;
import com.capgemini.types.BuildingTO;

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

		// when
		BuildingTO addedBuilding = buildingService.addBuilding(buildingToAdd);

		// then
		assertEquals(buildingToAdd.getDescription(), addedBuilding.getDescription());
		assertEquals(1,buildingService.findAll().size());
		

	}
	
	

}
