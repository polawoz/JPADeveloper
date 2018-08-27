package com.capgemini.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
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
		
		
		
		BuildingTO buildingToAdd = createTestBuilding();
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		// Flat 1
		FlatTO flatToAdd = createTestFlat();
		flatToAdd.setPrice(35000D);
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);

		// Flat 2
		FlatTO secondFlatToAdd = createTestFlat();
		secondFlatToAdd.setPrice(30000D);
		FlatTO secondSavedFlat = buildingService.addFlat(secondFlatToAdd, savedBuilding);

	
		//Clients
		ClientTO clientToAdd = createTestClient();
		ClientTO savedClient = clientsService.addClient(clientToAdd);
		
		
		
		//Transaction
		FlatTO boughtFirstFlat = clientsService.buyFlat(savedFlat, savedClient, new ArrayList<>());
		FlatTO boughtSecondFlat = clientsService.buyFlat(secondSavedFlat, savedClient, new ArrayList<>());
		
	
		// when
		Double pricesSum = buildingService.countPricesSumOfFlatsBoughtByClient(savedClient);

		// then
		assertEquals(new Double(65000), pricesSum);

	}
	
	
	
	
	@Test
	public void testShouldCountTheSumOfFlatsBoughtByClientCoOwnershipIncluded() {

	// given
		
		
		
		BuildingTO buildingToAdd = createTestBuilding();
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		// Flat 1
		FlatTO flatToAdd = createTestFlat();
		flatToAdd.setPrice(35000D);
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);

		// Flat 2
		FlatTO secondFlatToAdd = createTestFlat();
		secondFlatToAdd.setPrice(30000D);
		FlatTO secondSavedFlat = buildingService.addFlat(secondFlatToAdd, savedBuilding);

		// Building 2
		BuildingTO secondBuildingToAdd = createTestBuilding();
		BuildingTO secondSavedBuilding = buildingService.addBuilding(secondBuildingToAdd);

		// Flat 1
		FlatTO flatToAddSecondBuilding = createTestFlat();
		flatToAddSecondBuilding.setPrice(25000D);
		FlatTO thirdSavedFlat = buildingService.addFlat(flatToAddSecondBuilding, secondSavedBuilding);
		
		//Clients
		ClientTO clientToAdd = createTestClient();
		ClientTO savedClient = clientsService.addClient(clientToAdd);
		
		ClientTO secondClientToAdd = createTestClient();
		ClientTO secondSavedClient = clientsService.addClient(secondClientToAdd);
		
		List<ClientTO> coOwnersList = new ArrayList<>();
		coOwnersList.add(savedClient);
		
		//Transaction
		FlatTO boughtFirstFlat = clientsService.buyFlat(savedFlat, savedClient, new ArrayList<>());
		FlatTO boughtSecondFlat = clientsService.buyFlat(secondSavedFlat, savedClient, new ArrayList<>());
		FlatTO boughtThirdFlatAsCoOwner = clientsService.buyFlat(thirdSavedFlat, secondSavedClient, coOwnersList);
	
		// when
		Double pricesSum = buildingService.countPricesSumOfFlatsBoughtByClient(savedClient);

		// then
		assertEquals(new Double(90000), pricesSum);

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
		BuildingTO buildingToAdd = createTestBuilding();
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		// Flat 1
		FlatTO flatToAdd = createTestFlat();
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);

		// Flat 2
		FlatTO secondFlatToAdd = createTestFlat();
		FlatTO secondSavedFlat = buildingService.addFlat(secondFlatToAdd, savedBuilding);

		// Owner client
		//Clients
		ClientTO clientToAdd = createTestClient();
		ClientTO savedClient = clientsService.addClient(clientToAdd);
		
		ClientTO secondClientToAdd = createTestClient();
		ClientTO secondSavedClient = clientsService.addClient(secondClientToAdd);
		FlatTO scFlatToAdd = createTestFlat();
		FlatTO scSavedFlat = buildingService.addFlat(scFlatToAdd, savedBuilding);
		FlatTO scSecondFlatToAdd = createTestFlat();
		FlatTO scSecondSavedFlat = buildingService.addFlat(scSecondFlatToAdd, savedBuilding);
		FlatTO scBoughtFlat = clientsService.buyFlat(scSavedFlat, secondSavedClient, new ArrayList<>());
		FlatTO scSecondBoughtFlat = clientsService.buyFlat(scSecondSavedFlat, secondSavedClient, new ArrayList<>());
		
		//Transaction
		List<ClientTO> coOwnersList = new ArrayList<>();

		FlatTO boughtFlat = clientsService.buyFlat(savedFlat, savedClient, coOwnersList);
		FlatTO secondBoughtFlat = clientsService.buyFlat(secondSavedFlat, savedClient, coOwnersList);

		// when
		List<ClientTO> foundClients = clientsService.findClientsWhoBoughtFlatsMoreThan(1L);

		// then
		assertEquals(foundClientListBeforeSize + 2, foundClients.size());

	}
	
	
	
	
	@Test
	public void testShouldFindClientsWhoBoughtFlatsMoreThanCoOwnershipIncluded() {

		// given

		int foundClientListBeforeSize = clientsService.findClientsWhoBoughtFlatsMoreThan(1L).size();

		// Building
		BuildingTO buildingToAdd = createTestBuilding();
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);

		// Flat 1
		FlatTO flatToAdd = createTestFlat();
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);

		// Flat 2
		FlatTO secondFlatToAdd = createTestFlat();
		FlatTO secondSavedFlat = buildingService.addFlat(secondFlatToAdd, savedBuilding);

		// Owner client
		//Clients
		ClientTO clientToAdd = createTestClient();
		ClientTO savedClient = clientsService.addClient(clientToAdd);
		
		//Other client
		ClientTO secondClientToAdd = createTestClient();
		ClientTO secondSavedClient = clientsService.addClient(secondClientToAdd);
		
		//Transaction
		List<ClientTO> coOwnersList = new ArrayList<>();
		coOwnersList.add(savedClient);

		FlatTO boughtFlat = clientsService.buyFlat(savedFlat, savedClient, new ArrayList<>());
		FlatTO secondBoughtFlat = clientsService.buyFlat(secondSavedFlat, secondSavedClient, coOwnersList);

		// when
		List<ClientTO> foundClients = clientsService.findClientsWhoBoughtFlatsMoreThan(1L);

		// then
		assertEquals(foundClientListBeforeSize + 1, foundClients.size());

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
	
	
	

	@Test
	public void testShouldCountNumberOfFreeFlatsForBuilding(){
		
		
		
		BuildingTO buildingToAdd = createTestBuilding();
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);
	
		
		FlatTO firstFlatToAdd = createTestFlat();
		FlatTO savedFlat = buildingService.addFlat(firstFlatToAdd, savedBuilding);
		
		Long numberOfFreeFlatsBefore = buildingService.countFreeFlatFromBuilding(savedBuilding);
		
		FlatTO secondFlatToAdd = createTestFlat();
		FlatTO secondSavedFlat = buildingService.addFlat(secondFlatToAdd, savedBuilding);
		
		//Second building
		BuildingTO secondBuildingToAdd = createTestBuilding();
		BuildingTO secondSavedBuilding = buildingService.addBuilding(secondBuildingToAdd);
		
		FlatTO secondBuildingFlatToAdd = createTestFlat();
		FlatTO secondBuildingSavedFlat = buildingService.addFlat(secondBuildingFlatToAdd, secondSavedBuilding);
		
		//Sold
		FlatTO thirdFlatToAdd = createTestFlat();
		FlatTO thirdSavedFlat = buildingService.addFlat(thirdFlatToAdd, savedBuilding);
		ClientTO clientToAdd = createTestClient();
		ClientTO savedClient = clientsService.addClient(clientToAdd);
		FlatTO boughtFlat = clientsService.buyFlat(thirdSavedFlat, savedClient, new ArrayList<>());
		
		
		//when
		Long numberOfFreeFlats = buildingService.countFreeFlatFromBuilding(savedBuilding);
		
		//then
		assertEquals(numberOfFreeFlatsBefore.longValue()+1L, numberOfFreeFlats.longValue());
		
		
		
	}
	
	@DirtiesContext(methodMode=MethodMode.BEFORE_METHOD)
	@Test
	public void testShouldFindBuildingsWithMaxmimumNumberFreeFlats(){
		
		//given

	
		//Building 1
		BuildingTO buildingToAdd = createTestBuilding();
		buildingToAdd.setDescription("Maksymalna liczba mieszkan wolnych");
		BuildingTO savedBuilding = buildingService.addBuilding(buildingToAdd);
		
		//Flat 1
		FlatTO flatToAdd = createTestFlat();
		FlatTO savedFlat = buildingService.addFlat(flatToAdd, savedBuilding);
		//Flat 2
		FlatTO secondFlatToAdd = createTestFlat();
		FlatTO secondSavedFlat = buildingService.addFlat(secondFlatToAdd, savedBuilding);
		
		//Building 2
		BuildingTO anotherBuildingToAdd = createTestBuilding();
		BuildingTO anotherSavedBuilding = buildingService.addBuilding(anotherBuildingToAdd);
		//Flat 1
		FlatTO anotherFlatToAdd = createTestFlat();
		FlatTO anotherSavedFlat = buildingService.addFlat(anotherFlatToAdd, anotherSavedBuilding);
		
		
		//when
		List<BuildingTO> foundBuildings = buildingService.findBuildingsWithMaxmimumNumberFreeFlats();
		
		
		//then
		assertEquals(1, foundBuildings.size());
		assertTrue(foundBuildings.stream().anyMatch(b->b.getDescription().equals(savedBuilding.getDescription())));
		
		
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
