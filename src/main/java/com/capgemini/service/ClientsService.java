package com.capgemini.service;

import java.util.List;

import com.capgemini.types.ClientTO;
import com.capgemini.types.FlatTO;

public interface ClientsService {
	
	
	ClientTO addClient(ClientTO newClient);
	
	ClientTO findClientById(ClientTO client);
	
	FlatTO makeReservation(FlatTO flat, ClientTO owner, List<ClientTO> coOwners);
	
	FlatTO buyFlatAfterReservation(FlatTO flat);
	
	FlatTO buyFlat(FlatTO flat, ClientTO owner, List<ClientTO> coOwners);
	
	
	

}
