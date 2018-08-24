package com.capgemini.service;

import java.util.List;

import com.capgemini.types.ClientTO;
import com.capgemini.types.FlatTO;

public interface ClientsService {
	
	
	ClientTO addClient(ClientTO newClient);
	
	ClientTO findClientById(ClientTO client);
	
	ClientTO updateClient(ClientTO client);
	
	ClientTO removeClient(ClientTO client);
	
	FlatTO makeReservation(FlatTO flat, ClientTO owner, List<ClientTO> coOwners);
	
	FlatTO buyFlatAfterReservation(FlatTO flat, ClientTO client);
	
	FlatTO buyFlat(FlatTO flat, ClientTO owner, List<ClientTO> coOwners);
	
	FlatTO cancelReservation(FlatTO flat);
	
	List<ClientTO> findClientsWhoBoughtFlatsMoreThan(Long flatNumber);
	
	List<FlatTO> findFlatsDisabledSuitable();

}
