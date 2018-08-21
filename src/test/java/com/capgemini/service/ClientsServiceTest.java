package com.capgemini.service;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.capgemini.domain.Address;
import com.capgemini.domain.enums.FlatStatus;
import com.capgemini.types.BuildingTO;
import com.capgemini.types.ClientTO;
import com.capgemini.types.FlatTO;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=hsql")
public class ClientsServiceTest {

	@Autowired
	private ClientsService clientsService;
	
	@Autowired
	private BuildingService buildingService;


	
	@Test
	public void testShouldAddClient() {

	
		

	}
	
	@Test
	public void testShouldMakeReservation(){
		
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
		
		
	
		//Owner client
		ClientTO clientToAdd = ClientTO.builder()
				.lastName("Kowalski")
				.firstName("Andrzej")
				.address(new Address())
				.phoneNumber("3232323")
				.eMail("a.kowalski@gmail.com")
				.dateOfBirth(Date.valueOf("1980-09-21"))
				.build();
		
		ClientTO savedClient = clientsService.addClient(clientToAdd);
		
		//CoOwner client
		ClientTO clientToAddCoOwner = ClientTO.builder()
				.lastName("Kowalska")
				.firstName("Janina")
				.address(new Address())
				.phoneNumber("3232323")
				.eMail("j.kowalska@gmail.com")
				.dateOfBirth(Date.valueOf("1980-09-21"))
				.build();
		
		ClientTO savedClientCoOwner = clientsService.addClient(clientToAddCoOwner);		
	
		List<ClientTO> coOwnersList = new ArrayList<>();
		coOwnersList.add(savedClientCoOwner);
		
		//when
		
		FlatTO flatTO = clientsService.makeReservation(savedFlat, savedClient, coOwnersList);
		
		//then
		assertEquals(savedClient.getId(), flatTO.getOwnerId());
		assertEquals(FlatStatus.BOOKED, flatTO.getStatus());
		assertTrue(flatTO.getCoOwnersId().stream().anyMatch(b -> b==savedClientCoOwner.getId()));
		
		ClientTO updatedClientOwner = clientsService.findClientById(savedClient);
		ClientTO updatedClientCoOwner = clientsService.findClientById(savedClientCoOwner);
		
		assertTrue(updatedClientOwner.getFlatsOwnedIds().stream().anyMatch(b -> b== flatTO.getId()));
		assertTrue(updatedClientCoOwner.getFlatsCoOwnedIds().stream().anyMatch(b -> b==flatTO.getId()));
		
		
	}
	
	

}
