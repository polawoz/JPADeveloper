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
import com.capgemini.exception.CannotPerformActionException;
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

		// given
		ClientTO createdTestClient = createTestClient();

		// when
		ClientTO savedClient = clientsService.addClient(createdTestClient);

		// then
		assertNotNull(savedClient);
		assertEquals(createdTestClient.getLastName(), savedClient.getLastName());
		assertEquals(createdTestClient.getFirstName(), savedClient.getFirstName());
		assertEquals(createdTestClient.getDateOfBirth(), savedClient.getDateOfBirth());
		assertEquals(createdTestClient.getEMail(), savedClient.getEMail());
		assertEquals(createdTestClient.getPhoneNumber(), savedClient.getPhoneNumber());
		assertEquals(createdTestClient.getAddress().getBuildingNumber(), savedClient.getAddress().getBuildingNumber());

	}

	@Test
	public void testShouldFindClientById() {

		// given
		ClientTO createdTestClient = createTestClient();
		ClientTO savedClient = clientsService.addClient(createdTestClient);

		// when
		ClientTO foundClient = clientsService.findClientById(savedClient);

		// then
		assertNotNull(foundClient);
		assertEquals(savedClient.getId(), foundClient.getId());
		assertEquals(createdTestClient.getLastName(), foundClient.getLastName());
		assertEquals(createdTestClient.getFirstName(), foundClient.getFirstName());
		assertEquals(createdTestClient.getDateOfBirth(), foundClient.getDateOfBirth());
		assertEquals(createdTestClient.getEMail(), foundClient.getEMail());
		assertEquals(createdTestClient.getPhoneNumber(), foundClient.getPhoneNumber());
		assertEquals(createdTestClient.getAddress().getBuildingNumber(), foundClient.getAddress().getBuildingNumber());

	}

	@Test
	public void testShouldMakeReservation() {

		// given
		// Building

		BuildingTO buildingToAdd = createTestBuilding();
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		// Flat

		FlatTO flatToAdd = createTestFlat();
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);

		// Owner client
		ClientTO clientToAdd = createTestClient();
		ClientTO savedClient = clientsService.addClient(clientToAdd);

		// CoOwner client
		ClientTO clientToAddCoOwner = createTestClient();
		ClientTO savedClientCoOwner = clientsService.addClient(clientToAddCoOwner);
		
		ClientTO secondClientToAddCoOwner = createTestClient();
		ClientTO secondSavedClientCoOwner = clientsService.addClient(secondClientToAddCoOwner);

		List<ClientTO> coOwnersList = new ArrayList<>();
		coOwnersList.add(savedClientCoOwner);
		coOwnersList.add(secondSavedClientCoOwner);

		// when

		FlatTO flatTO = clientsService.makeReservation(savedFlat, savedClient, coOwnersList);

		// then
		assertEquals(savedClient.getId(), flatTO.getOwnerId());
		assertEquals(FlatStatus.BOOKED, flatTO.getStatus());
		assertTrue(flatTO.getCoOwnersId().stream().anyMatch(b -> b == savedClientCoOwner.getId()));
		assertTrue(flatTO.getCoOwnersId().stream().anyMatch(b -> b == secondSavedClientCoOwner.getId()));

		
		ClientTO updatedClientOwner = clientsService.findClientById(savedClient);
		ClientTO updatedClientCoOwner = clientsService.findClientById(savedClientCoOwner);
		ClientTO secondUpdatedClientCoOwner = clientsService.findClientById(secondSavedClientCoOwner);
		
		assertTrue(updatedClientOwner.getFlatsOwnedIds().stream().anyMatch(b -> b == flatTO.getId()));
		assertTrue(updatedClientCoOwner.getFlatsCoOwnedIds().stream().anyMatch(b -> b == flatTO.getId()));
		assertTrue(secondUpdatedClientCoOwner.getFlatsCoOwnedIds().stream().anyMatch(b -> b == flatTO.getId()));
		assertEquals(savedFlat.getVersion().longValue() + 1L,
				buildingService.findFlatById(flatTO).getVersion().longValue());

	}

	@Test
	public void testShouldBuyFlatAfterReservation() {

		// given
		// Building

		BuildingTO buildingToAdd = createTestBuilding();
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		// Flat

		FlatTO flatToAdd = createTestFlat();
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);

		// Owner client
		ClientTO clientToAdd = createTestClient();
		ClientTO savedClient = clientsService.addClient(clientToAdd);

		// CoOwner client
		ClientTO clientToAddCoOwner = createTestClient();
		ClientTO savedClientCoOwner = clientsService.addClient(clientToAddCoOwner);

		List<ClientTO> coOwnersList = new ArrayList<>();
		coOwnersList.add(savedClientCoOwner);

		FlatTO bookedFlatTO = clientsService.makeReservation(savedFlat, savedClient, coOwnersList);
		long flatVersionBeforeBuying = buildingService.findFlatById(bookedFlatTO).getVersion().longValue();

		// whent
		FlatTO boughtFlatTO = clientsService.buyFlatAfterReservation(bookedFlatTO, savedClient);

		// then
		long flatVersionAfterBuying = buildingService.findFlatById(boughtFlatTO).getVersion().longValue();
		assertEquals(FlatStatus.SOLD, boughtFlatTO.getStatus());
		assertEquals(flatVersionBeforeBuying + 1L, flatVersionAfterBuying);

	}

	@Test
	public void testShouldBuyFlat() {

		// given
		// Building

		BuildingTO buildingToAdd = createTestBuilding();
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		// Flat

		FlatTO flatToAdd = createTestFlat();
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);

		// Owner client
		ClientTO clientToAdd = createTestClient();
		ClientTO savedClient = clientsService.addClient(clientToAdd);

		// CoOwner client
		ClientTO clientToAddCoOwner = createTestClient();
		ClientTO savedClientCoOwner = clientsService.addClient(clientToAddCoOwner);

		List<ClientTO> coOwnersList = new ArrayList<>();
		coOwnersList.add(savedClientCoOwner);

		long flatVersionBeforeBuying = buildingService.findFlatById(savedFlat).getVersion().longValue();

		// when
		FlatTO boughtFlatTO = clientsService.buyFlat(savedFlat, savedClient, coOwnersList);

		// then
		long flatVersionAfterBuying = buildingService.findFlatById(boughtFlatTO).getVersion().longValue();
		assertEquals(FlatStatus.SOLD, boughtFlatTO.getStatus());
		assertEquals(flatVersionBeforeBuying + 1L, flatVersionAfterBuying);

	}

	@Test
	public void testShouldNotUpdateFlatEntityAfterReservationMaking() {

		// given
		// Building

		BuildingTO buildingToAdd = createTestBuilding();
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		// Flat

		FlatTO flatToAdd = createTestFlat();
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);

		FlatTO secondFlatToAdd = createTestFlat();
		FlatTO secondSavedFlat = buildingService.addFlat(secondFlatToAdd, savedBuilding);

		FlatTO thirdFlatToAdd = createTestFlat();
		FlatTO thirdSavedFlat = buildingService.addFlat(thirdFlatToAdd, savedBuilding);

		FlatTO fourthFlatToAdd = createTestFlat();
		FlatTO fourthSavedFlat = buildingService.addFlat(fourthFlatToAdd, savedBuilding);

		// Owner client
		ClientTO clientToAdd = createTestClient();
		ClientTO savedClient = clientsService.addClient(clientToAdd);

		FlatTO flatTO = clientsService.makeReservation(savedFlat, savedClient, new ArrayList<>());

		FlatTO secondFlatTO = clientsService.makeReservation(secondSavedFlat, savedClient, new ArrayList<>());

		FlatTO thirdFlatTO = clientsService.makeReservation(thirdSavedFlat, savedClient, new ArrayList<>());

		boolean exceptionThrown = false;
		// when
		try {

			clientsService.makeReservation(fourthSavedFlat, savedClient, new ArrayList<>());

		} catch (CannotPerformActionException e) {
			exceptionThrown = true;
		}

		// then
		FlatTO fourthFlatAfterBookingAttempt = buildingService.findFlatById(fourthSavedFlat);
		assertTrue(exceptionThrown);
		assertEquals(fourthSavedFlat.getStatus(), fourthFlatAfterBookingAttempt.getStatus());

	}

	@Test
	public void testShouldMakeReservationAfter3ReservationsAsCoOwner() {

		// given
		// Building

		BuildingTO buildingToAdd = createTestBuilding();
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		// Flat

		FlatTO flatToAdd = createTestFlat();
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);

		FlatTO secondFlatToAdd = createTestFlat();
		FlatTO secondSavedFlat = buildingService.addFlat(secondFlatToAdd, savedBuilding);

		FlatTO thirdFlatToAdd = createTestFlat();
		FlatTO thirdSavedFlat = buildingService.addFlat(thirdFlatToAdd, savedBuilding);

		FlatTO fourthFlatToAdd = createTestFlat();
		FlatTO fourthSavedFlat = buildingService.addFlat(fourthFlatToAdd, savedBuilding);

		// Owner client
		ClientTO clientToAdd = createTestClient();
		ClientTO savedClient = clientsService.addClient(clientToAdd);

		// CoOwner client
		ClientTO clientToAddCoOwner = createTestClient();
		ClientTO savedClientCoOwner = clientsService.addClient(clientToAddCoOwner);

		List<ClientTO> coOwnersList = new ArrayList<>();
		coOwnersList.add(savedClientCoOwner);

		FlatTO flatTO = clientsService.makeReservation(savedFlat, savedClient, coOwnersList);

		FlatTO secondFlatTO = clientsService.makeReservation(secondSavedFlat, savedClient, coOwnersList);

		FlatTO thirdFlatTO = clientsService.makeReservation(thirdSavedFlat, savedClient, coOwnersList);

		// when

		clientsService.makeReservation(fourthSavedFlat, savedClientCoOwner, new ArrayList<>());

		// then
		FlatTO fourthFlatAfterBookingAttempt = buildingService.findFlatById(fourthSavedFlat);
		assertEquals(FlatStatus.BOOKED, fourthFlatAfterBookingAttempt.getStatus());

	}

	@Test
	public void testShouldUpdateClient() {

		// given
		ClientTO clientToAdd = createTestClient();
		ClientTO savedClient = clientsService.addClient(clientToAdd);

		ClientTO updateParameters = ClientTO.builder().id(savedClient.getId()).firstName("Patrycja").build();

		// when
		ClientTO updatedClient = clientsService.updateClient(updateParameters);

		// then
		ClientTO foundClient = clientsService.findClientById(savedClient);
		assertEquals(updateParameters.getFirstName(), updatedClient.getFirstName());
		assertEquals(savedClient.getVersion().longValue() + 1L, foundClient.getVersion().longValue());

	}

	@Test
	public void testShouldRemoveClient() {

		// given
		ClientTO clientToAdd = createTestClient();
		ClientTO savedClient = clientsService.addClient(clientToAdd);

		// when
		clientsService.removeClient(savedClient);

		// then
		ClientTO foundClient = clientsService.findClientById(savedClient);
		assertNull(foundClient);

	}

	@Test
	public void testShouldCancelReservation() {

		// given
		// Building

		BuildingTO buildingToAdd = createTestBuilding();
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		// Flat

		FlatTO flatToAdd = createTestFlat();
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);

		// Owner client
		ClientTO clientToAdd = createTestClient();
		ClientTO savedClient = clientsService.addClient(clientToAdd);

		// CoOwner client
		ClientTO clientToAddCoOwner = createTestClient();
		ClientTO savedClientCoOwner = clientsService.addClient(clientToAddCoOwner);

		List<ClientTO> coOwnersList = new ArrayList<>();
		coOwnersList.add(savedClientCoOwner);

		FlatTO flatTO = clientsService.makeReservation(savedFlat, savedClient, coOwnersList);

		//when
		FlatTO flatAfterCancellingReservation = clientsService.cancelReservation(flatTO);
		
		
		
		// then
		ClientTO clientAfterCancellingReservation = clientsService.findClientById(savedClient);
		ClientTO coOwnerAfterCancellingReservation = clientsService.findClientById(savedClientCoOwner);
		
		assertTrue(clientAfterCancellingReservation.getFlatsOwnedIds().stream().noneMatch(b->b.equals(savedFlat.getId())));
		assertEquals(FlatStatus.FREE, flatAfterCancellingReservation.getStatus());
		assertNull(flatAfterCancellingReservation.getOwnerId());
		assertTrue(flatAfterCancellingReservation.getCoOwnersId().stream().noneMatch(b-> b.equals(savedClientCoOwner.getId())));
		assertTrue(coOwnerAfterCancellingReservation.getFlatsCoOwnedIds().stream().noneMatch(b->b.equals(savedFlat.getId())));
		assertTrue(flatTO.getCoOwnersId().stream().anyMatch(b -> b == savedClientCoOwner.getId()));


	}

	@Test
	public void testShouldCountTheSumOfFlatsBoughtByClient() {

		// given
		// Building
		BuildingTO buildingToAdd = BuildingTO.builder().description("Nowy budynek").location(new Address())
				.storeysNumber(1).hasElevator(false).flatCount(0).build();

		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		// Flat 1
		FlatTO flatToAdd = FlatTO.builder().area(30D).roomsCount(2).balconyCount(0).floorCount(2)
				.location(new Address()).status(FlatStatus.FREE).price(30000D).build();

		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);

		// Flat 2
		// Flat
		FlatTO secondFlatToAdd = FlatTO.builder().area(30D).roomsCount(2).balconyCount(0).floorCount(2)
				.location(new Address()).status(FlatStatus.FREE).price(25000D).build();

		FlatTO secondSavedFlat = buildingService.addFlat(secondFlatToAdd, savedBuilding);

		// Owner client
		ClientTO clientToAdd = ClientTO.builder().lastName("Kowalski").firstName("Andrzej").address(new Address())
				.phoneNumber("3232323").eMail("a.kowalski@gmail.com").dateOfBirth(Date.valueOf("1980-09-21")).build();

		ClientTO savedClient = clientsService.addClient(clientToAdd);

		List<ClientTO> coOwnersList = new ArrayList<>();

		FlatTO boughtFlat = clientsService.buyFlat(savedFlat, savedClient, coOwnersList);
		FlatTO secondBoughtFlat = clientsService.buyFlat(secondSavedFlat, savedClient, coOwnersList);

		// Reservation making

		BuildingTO buildingToAddR = BuildingTO.builder().description("Nowy budynek").location(new Address())
				.storeysNumber(1).hasElevator(false).flatCount(0).build();

		BuildingTO savedBuildingR = buildingService.addBuilding(buildingToAddR);

		// Flat
		FlatTO flatToAddR = FlatTO.builder().area(30D).roomsCount(2).balconyCount(0).floorCount(2)
				.location(new Address()).status(FlatStatus.FREE).price(30000D).build();

		FlatTO savedFlatR = buildingService.addFlat(flatToAddR, savedBuildingR);

		// Owner client

		// CoOwner client
		ClientTO clientToAddCoOwnerR = ClientTO.builder().lastName("Kowalska").firstName("Janina")
				.address(new Address()).phoneNumber("3232323").eMail("j.kowalska@gmail.com")
				.dateOfBirth(Date.valueOf("1980-09-21")).build();

		ClientTO savedClientCoOwnerR = clientsService.addClient(clientToAddCoOwnerR);

		List<ClientTO> coOwnersListR = new ArrayList<>();
		coOwnersList.add(savedClientCoOwnerR);

		FlatTO flatTOR = clientsService.makeReservation(savedFlatR, savedClient, coOwnersListR);

		// when
		Double pricesSum = buildingService.countPricesSumOfFlatsBoughtByClient(savedClient);

		// then
		assertEquals(new Double(55000), pricesSum);

	}

	@Test
	public void testShouldCountAveragePriceOfFlatInTheBuilding() {

		// given
		// Building
		BuildingTO buildingToAdd = createTestBuilding();
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		// Flat 1
		FlatTO flatToAdd = createTestFlat();
		flatToAdd.setPrice(30000D);
		buildingService.addFlat(flatToAdd, savedBuilding);

		// Flat 2
		FlatTO secondFlatToAdd = createTestFlat();
		secondFlatToAdd.setPrice(25000D);
		buildingService.addFlat(secondFlatToAdd, savedBuilding);

		// Building 2
		BuildingTO secondBuildingToAdd = createTestBuilding();
		BuildingTO secondSavedBuilding = buildingService.addBuilding(secondBuildingToAdd);

		// Flat 1
		FlatTO flatToAddSecondBuilding = createTestFlat();
		flatToAddSecondBuilding.setPrice(10000D);
		buildingService.addFlat(flatToAddSecondBuilding, secondSavedBuilding);

		// when
		Double priceAvg = buildingService.countAveragePriceOfFlatInTheBuilding(savedBuilding);

		// then
		assertEquals(new Double(55000 / 2), priceAvg);

	}

	@Test
	public void testShouldFindClientsWhoBoughtFlatsMoreThan() {

		// given

		int foundClientListBeforeSize = clientsService.findClientsWhoBoughtFlatsMoreThan(1L).size();

		// Building
		BuildingTO buildingToAdd = BuildingTO.builder().description("Nowy budynek").location(new Address())
				.storeysNumber(1).hasElevator(false).flatCount(0).build();

		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		// Flat 1
		FlatTO flatToAdd = FlatTO.builder().area(30D).roomsCount(2).balconyCount(0).floorCount(2)
				.location(new Address()).status(FlatStatus.FREE).price(30000D).build();

		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);

		// Flat 2
		// Flat
		FlatTO secondFlatToAdd = FlatTO.builder().area(30D).roomsCount(2).balconyCount(0).floorCount(2)
				.location(new Address()).status(FlatStatus.FREE).price(25000D).build();

		FlatTO secondSavedFlat = buildingService.addFlat(secondFlatToAdd, savedBuilding);

		// Owner client
		ClientTO clientToAdd = ClientTO.builder().lastName("Kowalski").firstName("Andrzej").address(new Address())
				.phoneNumber("3232323").eMail("a.kowalski@gmail.com").dateOfBirth(Date.valueOf("1980-09-21")).build();

		ClientTO savedClient = clientsService.addClient(clientToAdd);

		List<ClientTO> coOwnersList = new ArrayList<>();

		FlatTO boughtFlat = clientsService.buyFlat(savedFlat, savedClient, coOwnersList);
		FlatTO secondBoughtFlat = clientsService.buyFlat(secondSavedFlat, savedClient, coOwnersList);

		// when
		List<ClientTO> foundClients = clientsService.findClientsWhoBoughtFlatsMoreThan(1L);

		// then
		assertEquals(foundClientListBeforeSize + 2, foundClients.size());

	}

	@Test
	public void testShouldCountTheSumOfFlatsBoughtByClientCoOwnershipIncluded() {

		// given
		// Building
		BuildingTO buildingToAdd = BuildingTO.builder().description("Nowy budynek").location(new Address())
				.storeysNumber(1).hasElevator(false).flatCount(0).build();

		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		// Flat 1
		FlatTO flatToAdd = FlatTO.builder().area(30D).roomsCount(2).balconyCount(0).floorCount(2)
				.location(new Address()).status(FlatStatus.FREE).price(30000D).build();

		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);

		// Flat 2
		// Flat
		FlatTO secondFlatToAdd = FlatTO.builder().area(30D).roomsCount(2).balconyCount(0).floorCount(2)
				.location(new Address()).status(FlatStatus.FREE).price(25000D).build();

		FlatTO secondSavedFlat = buildingService.addFlat(secondFlatToAdd, savedBuilding);

		// Owner client
		ClientTO clientToAdd = ClientTO.builder().lastName("Kowalski").firstName("Andrzej").address(new Address())
				.phoneNumber("3232323").eMail("a.kowalski@gmail.com").dateOfBirth(Date.valueOf("1980-09-21")).build();

		ClientTO savedClient = clientsService.addClient(clientToAdd);

		ClientTO otherOwnerSavedClient = clientsService.addClient(
				ClientTO.builder().lastName("Kowalski").firstName("Jan").address(new Address()).phoneNumber("3232323")
						.eMail("j.kowalski@gmail.com").dateOfBirth(Date.valueOf("1980-09-21")).build());

		List<ClientTO> coOwnersList = new ArrayList<>();

		FlatTO boughtFlat = clientsService.buyFlat(savedFlat, savedClient, coOwnersList);

		List<ClientTO> secondCoOwnersList = new ArrayList<>();
		secondCoOwnersList.add(savedClient);

		FlatTO secondBoughtFlat = clientsService.buyFlat(secondSavedFlat, otherOwnerSavedClient, secondCoOwnersList);

		// Reservation making

		BuildingTO buildingToAddR = BuildingTO.builder().description("Nowy budynek").location(new Address())
				.storeysNumber(1).hasElevator(false).flatCount(0).build();

		BuildingTO savedBuildingR = buildingService.addBuilding(buildingToAddR);

		// Flat
		FlatTO flatToAddR = FlatTO.builder().area(30D).roomsCount(2).balconyCount(0).floorCount(2)
				.location(new Address()).status(FlatStatus.FREE).price(70000D).build();

		FlatTO savedFlatR = buildingService.addFlat(flatToAddR, savedBuildingR);

		// Owner client

		// CoOwner client
		ClientTO clientToAddCoOwnerR = ClientTO.builder().lastName("Kowalska").firstName("Janina")
				.address(new Address()).phoneNumber("3232323").eMail("j.kowalska@gmail.com")
				.dateOfBirth(Date.valueOf("1980-09-21")).build();

		ClientTO savedClientCoOwnerR = clientsService.addClient(clientToAddCoOwnerR);

		List<ClientTO> coOwnersListR = new ArrayList<>();
		coOwnersList.add(savedClientCoOwnerR);

		FlatTO flatTOR = clientsService.makeReservation(savedFlatR, savedClient, coOwnersListR);

		// when
		Double pricesSum = buildingService.countPricesSumOfFlatsBoughtByClient(savedClient);

		// then
		assertEquals(new Double(55000), pricesSum);

	}

	@Test
	public void testShoudlFindFlatsDisabledSuitable() {

		// given

		int foundFlatsListSizeBefore = clientsService.findFlatsDisabledSuitable().size();

		BuildingTO buildingToAdd = createTestBuilding();
		buildingToAdd.setHasElevator(false);
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		// Flat 1
		FlatTO flatToAdd = createTestFlat();
		flatToAdd.setFloorCount(0);
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);

		// Flat 2
		// Flat
		FlatTO secondFlatToAdd = createTestFlat();
		secondFlatToAdd.setFloorCount(2);
		buildingService.addFlat(secondFlatToAdd, savedBuilding);

		// Second building
		BuildingTO secondBuildingToAdd = createTestBuilding();
		secondBuildingToAdd.setHasElevator(true);
		BuildingTO secondSavedBuilding = buildingService.addBuilding(secondBuildingToAdd);

		// Flat 1
		FlatTO anotherFlatToAdd = createTestFlat();
		anotherFlatToAdd.setFloorCount(3);
		FlatTO anotherSavedFlat = buildingService.addFlat(anotherFlatToAdd, secondSavedBuilding);

		// when
		List<FlatTO> foundFlats = clientsService.findFlatsDisabledSuitable();

		// then
		assertEquals(foundFlatsListSizeBefore + 2, foundFlats.size());
		assertTrue(foundFlats.stream().anyMatch(b -> b.getId().equals(savedFlat.getId())));
		assertTrue(foundFlats.stream().anyMatch(b -> b.getId().equals(anotherSavedFlat.getId())));

	}
	
	
	
	
	
	@Test
	public void testShouldCountNumberOfFlatsByStatus() {

		// given
		// Building
		BuildingTO buildingToAdd = createTestBuilding();
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);
		
		Long flatCountBefore = buildingService.countNumberOfFlatsByStatus(FlatStatus.FREE, savedBuilding.getId());

		// Flat
		FlatTO flatToAdd = createTestFlat();
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);
		
		FlatTO secondFlatToAdd = createTestFlat();
		FlatTO secondSavedFlat = buildingService.addFlat(secondFlatToAdd, savedBuilding);
		
		//Sold flat
		FlatTO thirdFlatToAdd = createTestFlat();
		FlatTO thirdSavedFlat = buildingService.addFlat(thirdFlatToAdd, savedBuilding);
		ClientTO clientToAdd = createTestClient();
		ClientTO savedClient = clientsService.addClient(clientToAdd);
		FlatTO boughtFlat = clientsService.buyFlat(thirdSavedFlat, savedClient, new ArrayList<>());
		
		//when
		Long flatCountAfter = buildingService.countNumberOfFlatsByStatus(FlatStatus.FREE, savedBuilding.getId());
		
		//then
		assertEquals(flatCountBefore.longValue()+2L, flatCountAfter.longValue());

	}
	

	private ClientTO createTestClient() {

		String[] lastNameTable = { "Kowalski", "Nowak", "Piotrowski", "Adamski", "Lecki", "Markowski", "Przybylski",
				"Wisniewski", "Majetny", "Stratny" };

		String[] firstNameTable = { "Jacek", "Placek", "Mateusz", "Arek", "Artur", "Pawel", "Patryk", "Blazej", "Jan",
				"Jakub" };

		String lastName = lastNameTable[(int) (Math.random() * 10)];
		String firstName = firstNameTable[(int) (Math.random() * 10)];
		String eMail = firstName.toLowerCase().charAt(0) + "." + lastName.toLowerCase() + "@gmail.com";
		Date dateOfBirth = Date.valueOf("1999-12-12");

		Address address = Address.builder().street("Niepodleglosci")
				.buildingNumber(String.valueOf((int) (Math.random() * 100 + 1)))
				.flatNumber(String.valueOf((int) (Math.random() * 100 + 1)))
				.postalCode(String.valueOf((int) (Math.random() * 100 + 1))).town("Poznan").country("Polska").build();

		ClientTO createdClient = ClientTO.builder().lastName(lastName).firstName(firstName).address(address)
				.phoneNumber(String.valueOf((int) (Math.random() * 10000000))).eMail(eMail).dateOfBirth(dateOfBirth)
				.build();

		return createdClient;
	}

	private BuildingTO createTestBuilding() {

		Address address = Address.builder().street("Niepodleglosci")
				.buildingNumber(String.valueOf((int) (Math.random() * 100 + 1)))
				.flatNumber(String.valueOf((int) (Math.random() * 100 + 1)))
				.postalCode(String.valueOf((int) (Math.random() * 100 + 1))).town("Poznan").country("Polska").build();

		BuildingTO createdBuilding = BuildingTO.builder()
				.description("Nowy budynek nr " + String.valueOf(((int) (Math.random() * 1000)) + 1)).location(address)
				.storeysNumber((int) (Math.random() * 10) + 1)
				.hasElevator((int) (Math.random() * 2) == 0 ? false : true).flatCount(0).build();

		return createdBuilding;
	}

	private FlatTO createTestFlat() {

		Address address = Address.builder().street("Niepodleglosci")
				.buildingNumber(String.valueOf((int) (Math.random() * 100 + 1)))
				.flatNumber(String.valueOf((int) (Math.random() * 100 + 1)))
				.postalCode(String.valueOf((int) (Math.random() * 100 + 1))).town("Poznan").country("Polska").build();

		FlatTO createdFlat = FlatTO.builder().area(0d).roomsCount(0).balconyCount(0).floorCount(0).location(address)
				.status(FlatStatus.FREE).price(300000d).build();

		return createdFlat;
	}
	
	


}
