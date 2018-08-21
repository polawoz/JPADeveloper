package com.capgemini.mappers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.capgemini.domain.Address;
import com.capgemini.domain.ClientEntity;
import com.capgemini.domain.FlatEntity;
import com.capgemini.types.ClientTO;
import com.capgemini.types.FlatTO;

@Component
public class FlatMapper {

	public FlatEntity mapToEntity(FlatTO newFlat) {

		if (newFlat == null) {
			return null;
		}

	
		
		return FlatEntity.builder()
				//.id(newFlat.getId())
				.area(newFlat.getArea())
				.roomsCount(newFlat.getRoomsCount())
				.balconyCount(newFlat.getBalconyCount())
				.floorCount(newFlat.getFloorCount())
				.location(createCopy(newFlat.getLocation()))
				.status(newFlat.getStatus())
				.price(newFlat.getPrice())
				.build();
	}

	private Address createCopy(Address address) {

		if (address == null) {
			return null;
		}

		return Address.builder().street(address.getStreet()).buildingNumber(address.getBuildingNumber())
				.flatNumber(address.getFlatNumber()).postalCode(address.getPostalCode()).town(address.getTown())
				.country(address.getCountry()).build();
	}

	public FlatTO mapToTO(FlatEntity flat) {

		return FlatTO.builder()
				.id(flat.getId())
				.area(flat.getArea())
				.roomsCount(flat.getRoomsCount())
				.balconyCount(flat.getBalconyCount())
				.floorCount(flat.getFloorCount())
				.location(createCopy(flat.getLocation()))
				.status(flat.getStatus())
				.price(flat.getPrice())
				.buildingId(flat.getBuilding().getId())
				.ownerId(flat.getOwner()==null ? null: flat.getOwner().getId())
				.coOwnersId(mapCollectionToLong(flat.getCoOwners()))
				.build();
	}

	public List<Long> mapCollectionToLong(List<ClientEntity> coOwnersList) {

		List<Long> coOwnersListLong = new ArrayList<>();

		if (coOwnersList == null) {
			return coOwnersListLong;
		}

		for (ClientEntity owner : coOwnersList) {
			coOwnersListLong.add(owner.getId());

		}
		return coOwnersListLong;

	}
	

	

	public List<FlatTO> mapToTOList(List<FlatEntity> flatEntityList) {

		List<FlatTO> mappedList = new ArrayList<>();

		for (FlatEntity f : flatEntityList) {
			mappedList.add(mapToTO(f));
		}

		return mappedList;
	}

	public Iterable<Long> mapCollectionFromTOtoIds(List<ClientTO> coOwnersList) {


		List<Long> coOwnersListLong = new ArrayList<>();

		if (coOwnersList == null) {
			return coOwnersListLong;
		}

		for (ClientTO owner : coOwnersList) {
			coOwnersListLong.add(owner.getId());

		}
		return coOwnersListLong;

		
	}



}
