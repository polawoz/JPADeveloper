package com.capgemini.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.dao.ClientRepository;
import com.capgemini.dao.FlatRepository;
import com.capgemini.domain.ClientEntity;
import com.capgemini.domain.FlatEntity;
import com.capgemini.domain.enums.FlatStatus;
import com.capgemini.exception.CannotPerformActionException;
import com.capgemini.mappers.ClientMapper;
import com.capgemini.mappers.FlatMapper;
import com.capgemini.service.ClientsService;
import com.capgemini.types.ClientTO;
import com.capgemini.types.FlatTO;

@Service
@Transactional
public class ClientsServiceImpl implements ClientsService {

	private ClientRepository clientDao;

	private ClientMapper clientMapper;

	private FlatRepository flatDao;

	private FlatMapper flatMapper;

	@Autowired
	public ClientsServiceImpl(ClientRepository clientDao, ClientMapper clientMapper, FlatRepository flatDao, FlatMapper flatMapper) {
		this.clientDao = clientDao;
		this.clientMapper = clientMapper;
		this.flatDao = flatDao;
		this.flatMapper = flatMapper;
	}

	@Override
	public ClientTO addClient(ClientTO newClient) {

		ClientEntity clientEntity = clientDao.save(clientMapper.mapToEntity(newClient));

		return clientMapper.mapToTO(clientEntity);
	}

	@Override
	public ClientTO findClientById(ClientTO client) {

		ClientEntity clientEntity = clientDao.findOne(client.getId());

		return clientMapper.mapToTO(clientEntity);
	}

	@Override
	public FlatTO makeReservation(FlatTO flat, ClientTO owner, List<ClientTO> coOwners) {

		FlatEntity flatEntity = flatDao.findOne(flat.getId());

		if (flatEntity.getStatus().equals(FlatStatus.BOOKED)) {
			throw new CannotPerformActionException("Flat is already booked by other client!");
		}

		if (flatEntity.getStatus().equals(FlatStatus.SOLD)) {
			throw new CannotPerformActionException("Flat is already sold to other client!");
		}

		ClientEntity ownerEntity = clientDao.findOne(owner.getId());

		if (ownerEntity.getFlatsOwned().stream().filter(b -> b.getStatus().equals(FlatStatus.BOOKED))
				.collect(Collectors.toList()).size() >= 3) {
			throw new CannotPerformActionException(
					"Client indicated as the owner has reached the limit of 3 booked flats!");
		}

		ownerEntity.addOwnedFlat(flatEntity);

		
		for (ClientTO coOwner : coOwners) {
			ClientEntity client = clientDao.findOne(coOwner.getId());
			if (client != null) {
				client.addCoOwnedFlat(flatEntity);
			}
		}

		flatEntity.setStatus(FlatStatus.BOOKED);

		return flatMapper.mapToTO(flatEntity);
	}

	@Override
	public FlatTO buyFlatAfterReservation(FlatTO flat, ClientTO client) {
		
		FlatEntity flatEntity = flatDao.findOne(flat.getId());
		
		if(flatEntity.getStatus()!=FlatStatus.BOOKED){
			throw new CannotPerformActionException("This flat is not booked!");
		}
		if(flatEntity.getOwner().getId()!=client.getId()){
			throw new CannotPerformActionException("This flat can be bought only by the client who booked it!");
		}

		flatEntity.setStatus(FlatStatus.SOLD);

		return flatMapper.mapToTO(flatEntity);
	}

	@Override
	public FlatTO buyFlat(FlatTO flat, ClientTO owner, List<ClientTO> coOwners) {
		FlatEntity flatEntity = flatDao.findOne(flat.getId());

		ClientEntity ownerEntity = clientDao.findOne(owner.getId());

		ownerEntity.addOwnedFlat(flatEntity);

		for (ClientTO coOwner : coOwners) {
			ClientEntity client = clientDao.findOne(coOwner.getId());
			if (client != null) {
				client.addCoOwnedFlat(flatEntity);
			}
		}

		flatEntity.setStatus(FlatStatus.SOLD);

		return flatMapper.mapToTO(flatEntity);
	}

	@Override
	public ClientTO updateClient(ClientTO client) {

		ClientEntity clientToUpdate = clientDao.findOne(client.getId());

		return clientMapper.update(client, clientToUpdate);
	}

	@Override
	public ClientTO removeClient(ClientTO client) {

		clientDao.delete(client.getId());

		return client;
	}

	@Override
	public FlatTO cancelReservation(FlatTO flat) {

		FlatEntity flatToCancelReservation = flatDao.findOne(flat.getId());

		if (flatToCancelReservation.getStatus() == FlatStatus.SOLD) {
			throw new CannotPerformActionException("This flat is already sold!");
		}

		if (flatToCancelReservation.getStatus() == FlatStatus.FREE) {
			throw new CannotPerformActionException("This flat is not even booked!");
		}

		flatToCancelReservation.getOwner().removeOwnedFlat(flatToCancelReservation);

		List<ClientEntity> coOwnersList = flatToCancelReservation.getCoOwners();

		for (ClientEntity coOwner : coOwnersList) {
			coOwner.removeCoOwnedFlat(flatToCancelReservation);
		}

		flatToCancelReservation.setStatus(FlatStatus.FREE);

		return flatMapper.mapToTO(flatToCancelReservation);

	}

}
