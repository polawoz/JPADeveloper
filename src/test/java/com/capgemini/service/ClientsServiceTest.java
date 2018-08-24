package com.capgemini.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
		
		assertEquals(savedFlat.getVersion().longValue()+1L, buildingService.findFlatById(flatTO).getVersion().longValue());
		
		
	}
	
	
	@Test
	public void testShouldNotUpdateFlatEntityAfterReservationMaking(){
		
	 //TODO
		
		
		
	}
	
	@Test
	public void testShouldCountTheSumOfFlatsBoughtByClient(){
		
		
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
		
		//Flat 1
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
		
		//Flat 2
		//Flat
		FlatTO secondFlatToAdd = FlatTO.builder()
				.area(30D)
				.roomsCount(2)
				.balconyCount(0)
				.floorCount(2)
				.location(new Address())
				.status(FlatStatus.FREE)
				.price(25000D)
				.build();
		
		FlatTO secondSavedFlat = buildingService.addFlat(secondFlatToAdd, savedBuilding);
		
	
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
		
		
		List<ClientTO> coOwnersList = new ArrayList<>();
		
		FlatTO boughtFlat = clientsService.buyFlat(savedFlat, savedClient, coOwnersList);
		FlatTO secondBoughtFlat = clientsService.buyFlat(secondSavedFlat, savedClient, coOwnersList);
		
		
		
		
	
		
		
		
		
		
		
		
		//Reservation making
		
		BuildingTO buildingToAddR = BuildingTO.builder()
				.description("Nowy budynek")
				.location(new Address())
				.storeysNumber(1)
				.hasElevator(false)
				.flatCount(0)
				.build();
		
		BuildingTO savedBuildingR = buildingService.addBuilding(buildingToAddR);
		
		//Flat
		FlatTO flatToAddR = FlatTO.builder()
				.area(30D)
				.roomsCount(2)
				.balconyCount(0)
				.floorCount(2)
				.location(new Address())
				.status(FlatStatus.FREE)
				.price(30000D)
				.build();
		
		FlatTO savedFlatR = buildingService.addFlat(flatToAddR, savedBuildingR);
		
		
	
		//Owner client
	
		
		//CoOwner client
		ClientTO clientToAddCoOwnerR = ClientTO.builder()
				.lastName("Kowalska")
				.firstName("Janina")
				.address(new Address())
				.phoneNumber("3232323")
				.eMail("j.kowalska@gmail.com")
				.dateOfBirth(Date.valueOf("1980-09-21"))
				.build();
		
		ClientTO savedClientCoOwnerR = clientsService.addClient(clientToAddCoOwnerR);		
	
		List<ClientTO> coOwnersListR = new ArrayList<>();
		coOwnersList.add(savedClientCoOwnerR);
		
		FlatTO flatTOR = clientsService.makeReservation(savedFlatR, savedClient, coOwnersListR);
		
		
		
		//when
		Double pricesSum = buildingService.countPricesSumOfFlatsBoughtByClient(savedClient);
		
		//then
		assertEquals(new Double(55000), pricesSum);
		
	}
	
	
	
	@Test
	public void testShouldCountAveragePriceOfFlatInTheBuilding(){
		
		
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
		
		//Flat 1
		FlatTO flatToAdd = FlatTO.builder()
				.area(30D)
				.roomsCount(2)
				.balconyCount(0)
				.floorCount(2)
				.location(new Address())
				.status(FlatStatus.FREE)
				.price(30000D)
				.build();
		
		buildingService.addFlat(flatToAdd, savedBuilding);
		
		//Flat 2
		FlatTO secondFlatToAdd = FlatTO.builder()
				.area(30D)
				.roomsCount(2)
				.balconyCount(0)
				.floorCount(2)
				.location(new Address())
				.status(FlatStatus.FREE)
				.price(25000D)
				.build();
		
		buildingService.addFlat(secondFlatToAdd, savedBuilding);
		
		//Building 2
		BuildingTO secondBuildingToAdd = BuildingTO.builder()
				.description("Nowy budynek")
				.location(new Address())
				.storeysNumber(1)
				.hasElevator(false)
				.flatCount(0)
				.build();
		
		BuildingTO secondSavedBuilding = buildingService.addBuilding(secondBuildingToAdd);
		
		//Flat 1
		FlatTO flatToAddSecondBuilding = FlatTO.builder()
				.area(30D)
				.roomsCount(2)
				.balconyCount(0)
				.floorCount(2)
				.location(new Address())
				.status(FlatStatus.FREE)
				.price(10000D)
				.build();
		
		buildingService.addFlat(flatToAddSecondBuilding, secondSavedBuilding);
		

		
		//when
		Double priceAvg = buildingService.countAveragePriceOfFlatInTheBuilding(savedBuilding);
		
		//then
		assertEquals(new Double(55000/2), priceAvg);
		
	}
	
	

	//@Test
	public void testShouldFindClientsWhoBoughtFlatsMoreThan(){
		
		
		//given
		
		int foundClientListBeforeSize = clientsService.findClientsWhoBoughtFlatsMoreThan(1L).size();
		
		//Building
		BuildingTO buildingToAdd = BuildingTO.builder()
				.description("Nowy budynek")
				.location(new Address())
				.storeysNumber(1)
				.hasElevator(false)
				.flatCount(0)
				.build();
		
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);
		
		//Flat 1
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
		
		//Flat 2
		//Flat
		FlatTO secondFlatToAdd = FlatTO.builder()
				.area(30D)
				.roomsCount(2)
				.balconyCount(0)
				.floorCount(2)
				.location(new Address())
				.status(FlatStatus.FREE)
				.price(25000D)
				.build();
		
		FlatTO secondSavedFlat = buildingService.addFlat(secondFlatToAdd, savedBuilding);
		
	
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
		
		
		List<ClientTO> coOwnersList = new ArrayList<>();
		
		FlatTO boughtFlat = clientsService.buyFlat(savedFlat, savedClient, coOwnersList);
		FlatTO secondBoughtFlat = clientsService.buyFlat(secondSavedFlat, savedClient, coOwnersList);
		
		
		
		
		
		//when
		List<ClientTO> foundClients = clientsService.findClientsWhoBoughtFlatsMoreThan(1L);
		
		
		
		
		
		//then
		assertEquals(foundClientListBeforeSize+2, foundClients.size());
		
		
		
		
		
		
		
		
	}
	
	
	
	
	//@Test
	public void testShouldCountTheSumOfFlatsBoughtByClientCoOwnershipIncluded(){
		
		
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
		
		//Flat 1
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
		
		//Flat 2
		//Flat
		FlatTO secondFlatToAdd = FlatTO.builder()
				.area(30D)
				.roomsCount(2)
				.balconyCount(0)
				.floorCount(2)
				.location(new Address())
				.status(FlatStatus.FREE)
				.price(25000D)
				.build();
		
		FlatTO secondSavedFlat = buildingService.addFlat(secondFlatToAdd, savedBuilding);
		
	
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
		
		
		ClientTO otherOwnerSavedClient = clientsService.addClient(ClientTO.builder()
				.lastName("Kowalski")
				.firstName("Jan")
				.address(new Address())
				.phoneNumber("3232323")
				.eMail("j.kowalski@gmail.com")
				.dateOfBirth(Date.valueOf("1980-09-21"))
				.build());
		

		
		List<ClientTO> coOwnersList = new ArrayList<>();
		
		FlatTO boughtFlat = clientsService.buyFlat(savedFlat, savedClient, coOwnersList);
		
		List<ClientTO> secondCoOwnersList = new ArrayList<>();
		secondCoOwnersList.add(savedClient);
		
		FlatTO secondBoughtFlat = clientsService.buyFlat(secondSavedFlat, otherOwnerSavedClient, secondCoOwnersList);
		
		
		
		
	
		
		
		
		
		
		
		
		//Reservation making
		
		BuildingTO buildingToAddR = BuildingTO.builder()
				.description("Nowy budynek")
				.location(new Address())
				.storeysNumber(1)
				.hasElevator(false)
				.flatCount(0)
				.build();
		
		BuildingTO savedBuildingR = buildingService.addBuilding(buildingToAddR);
		
		//Flat
		FlatTO flatToAddR = FlatTO.builder()
				.area(30D)
				.roomsCount(2)
				.balconyCount(0)
				.floorCount(2)
				.location(new Address())
				.status(FlatStatus.FREE)
				.price(70000D)
				.build();
		
		FlatTO savedFlatR = buildingService.addFlat(flatToAddR, savedBuildingR);
		
		
	
		//Owner client
	
		
		//CoOwner client
		ClientTO clientToAddCoOwnerR = ClientTO.builder()
				.lastName("Kowalska")
				.firstName("Janina")
				.address(new Address())
				.phoneNumber("3232323")
				.eMail("j.kowalska@gmail.com")
				.dateOfBirth(Date.valueOf("1980-09-21"))
				.build();
		
		ClientTO savedClientCoOwnerR = clientsService.addClient(clientToAddCoOwnerR);		
	
		List<ClientTO> coOwnersListR = new ArrayList<>();
		coOwnersList.add(savedClientCoOwnerR);
		
		FlatTO flatTOR = clientsService.makeReservation(savedFlatR, savedClient, coOwnersListR);
		
		
		
		//when
		Double pricesSum = buildingService.countPricesSumOfFlatsBoughtByClient(savedClient);
		
		//then
		assertEquals(new Double(55000), pricesSum);
		
	}
	
	
	@Test
	public void testShoudlFindFlatsDisabledSuitable(){
		
		
		//given
		
		
		int foundFlatsListSizeBefore = clientsService.findFlatsDisabledSuitable().size();
		
		BuildingTO buildingToAdd = BuildingTO.builder()
				.description("Nowy budynek")
				.location(new Address())
				.storeysNumber(3)
				.hasElevator(false)
				.flatCount(0)
				.build();
		
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);
		
		//Flat 1
		FlatTO flatToAdd = FlatTO.builder()
				.area(30D)
				.roomsCount(2)
				.balconyCount(0)
				.floorCount(0)
				.location(new Address())
				.status(FlatStatus.FREE)
				.price(30000D)
				.build();
		
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);
		
		//Flat 2
		//Flat
		FlatTO secondFlatToAdd = FlatTO.builder()
				.area(30D)
				.roomsCount(2)
				.balconyCount(0)
				.floorCount(2)
				.location(new Address())
				.status(FlatStatus.FREE)
				.price(25000D)
				.build();
		
		FlatTO secondSavedFlat = buildingService.addFlat(secondFlatToAdd, savedBuilding);
		
		
		//Second building
		BuildingTO anotherBuildingToAdd = BuildingTO.builder()
				.description("Nowy budynek")
				.location(new Address())
				.storeysNumber(4)
				.hasElevator(true)
				.flatCount(0)
				.build();
		
		BuildingTO anotherSavedBuilding = buildingService.addBuilding(anotherBuildingToAdd);
		
		//Flat 1
		FlatTO anotherFlatToAdd = FlatTO.builder()
				.area(30D)
				.roomsCount(2)
				.balconyCount(0)
				.floorCount(3)
				.location(new Address())
				.status(FlatStatus.FREE)
				.price(30000D)
				.build();
		
		FlatTO anotherSavedFlat = buildingService.addFlat(anotherFlatToAdd, anotherSavedBuilding);

		
		//when
		List<FlatTO> foundFlats = clientsService.findFlatsDisabledSuitable();
		
		
		//then
		assertEquals(foundFlatsListSizeBefore+2, foundFlats.size());
		assertTrue(foundFlats.stream().anyMatch(b-> b.getId().equals(savedFlat.getId())));
		assertTrue(foundFlats.stream().anyMatch(b-> b.getId().equals(anotherSavedFlat.getId())));
		
		
		
		
		
		
		
		
		
	}
	
	

}
