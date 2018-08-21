package com.capgemini.mappers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.capgemini.domain.Address;
import com.capgemini.domain.ClientEntity;
import com.capgemini.domain.FlatEntity;
import com.capgemini.types.ClientTO;

@Component
public class ClientMapper {

	public ClientEntity mapToEntity(ClientTO newClient) {

		if (newClient == null) {
			return null;
		}

		return ClientEntity.builder().lastName(newClient.getLastName()).firstName(newClient.getFirstName())
				.address(createCopy(newClient.getAddress())).phoneNumber(newClient.getPhoneNumber())
				.eMail(newClient.getEMail()).dateOfBirth(newClient.getDateOfBirth())
				.flatsOwned(newClient.getFlatsOwnedIds() == null ? new ArrayList<FlatEntity>()
						: new ArrayList<FlatEntity>())
				.flatsCoOwned(newClient.getFlatsCoOwnedIds() == null ? new ArrayList<FlatEntity>()
						: new ArrayList<FlatEntity>())
				.build();
	}

	public Address createCopy(Address address) {

		if (address == null) {
			return null;
		}

		return Address.builder().street(address.getStreet()).buildingNumber(address.getBuildingNumber())
				.flatNumber(address.getFlatNumber()).postalCode(address.getPostalCode()).town(address.getTown())
				.country(address.getCountry()).build();
	}

	public ClientTO mapToTO(ClientEntity client) {

		return ClientTO.builder()
				.id(client.getId())
				.lastName(client.getLastName()).firstName(client.getFirstName())
				.address(createCopy(client.getAddress())).phoneNumber(client.getPhoneNumber()).eMail(client.getEMail())
				.dateOfBirth(client.getDateOfBirth())
				.flatsOwnedIds(client.getFlatsOwned() == null ? new ArrayList<Long>()
						: mapCollectionToLong(client.getFlatsOwned()))
				.flatsCoOwnedIds(client.getFlatsCoOwned() == null ? new ArrayList<Long>()
						: mapCollectionToLong(client.getFlatsCoOwned()))
				.build();
	}

	public List<Long> mapCollectionToLong(List<FlatEntity> flatsList) {

		List<Long> flatsListLong = new ArrayList<>();

		if (flatsList == null) {
			return flatsListLong;
		}

		for (FlatEntity flat : flatsList) {
			flatsListLong.add(flat.getId());

		}
		return flatsListLong;

	}

	public List<ClientTO> mapToTOList(List<ClientEntity> clientEntityList) {

		List<ClientTO> mappedList = new ArrayList<>();

		for (ClientEntity c : clientEntityList) {
			mappedList.add(mapToTO(c));
		}

		return mappedList;
	}

}