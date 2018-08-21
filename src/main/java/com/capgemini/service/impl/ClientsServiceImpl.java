package com.capgemini.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.dao.ClientDao;
import com.capgemini.dao.FlatDao;
import com.capgemini.domain.ClientEntity;
import com.capgemini.domain.FlatEntity;
import com.capgemini.domain.enums.FlatStatus;
import com.capgemini.mappers.ClientMapper;
import com.capgemini.mappers.FlatMapper;
import com.capgemini.service.ClientsService;
import com.capgemini.types.ClientTO;
import com.capgemini.types.FlatTO;

@Service
@Transactional
public class ClientsServiceImpl implements ClientsService {

	private ClientDao clientDao;

	private ClientMapper clientMapper;

	private FlatDao flatDao;

	private FlatMapper flatMapper;

	@Autowired
	public ClientsServiceImpl(ClientDao clientDao, ClientMapper clientMapper, FlatDao flatDao, FlatMapper flatMapper) {
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

		ClientEntity ownerEntity = clientDao.findOne(owner.getId());

		ownerEntity.addOwnedFlat(flatEntity);

		for (ClientTO coOwner : coOwners) {
			ClientEntity client = clientDao.findOne(coOwner.getId());
			if (client != null) {
				client.addCoOwnedFlat(flatEntity);
			}
		}

		flatEntity.setStatus(FlatStatus.BOOKED);

		// tu moze byc zle
		return flatMapper.mapToTO(flatEntity);
	}

	@Override
	public FlatTO buyFlatAfterReservation(FlatTO flat) {

		FlatEntity flatEntity = flatDao.findOne(flat.getId());

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

		// tu moze byc zle
		return flatMapper.mapToTO(flatEntity);
	}

}
