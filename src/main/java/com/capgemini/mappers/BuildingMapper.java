package com.capgemini.mappers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.capgemini.domain.Address;
import com.capgemini.domain.BuildingEntity;
import com.capgemini.domain.FlatEntity;
import com.capgemini.types.BuildingTO;

@Component
public class BuildingMapper {

	public BuildingEntity mapToEntity(BuildingTO newBuilding) {

		if (newBuilding == null) {
			return null;
		}

		return BuildingEntity.builder().description(newBuilding.getDescription())
				.location(createCopy(newBuilding.getLocation())).storeysNumber(newBuilding.getStoreysNumber())
				.hasElevator(newBuilding.getHasElevator()).flatCount(newBuilding.getFlatCount())
				.flats(newBuilding.getFlats() == null ? new ArrayList<FlatEntity>() : new ArrayList<FlatEntity>())
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

	public BuildingTO mapToTO(BuildingEntity building) {

		return BuildingTO.builder().description(building.getDescription()).location(createCopy(building.getLocation()))
				.storeysNumber(building.getStoreysNumber()).hasElevator(building.getHasElevator())
				.flatCount(building.getFlatCount())
				.flats(building.getFlats() == null ? new ArrayList<Long>() : mapCollectionToLong(building.getFlats()))
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

	public List<BuildingTO> mapToTOList(List<BuildingEntity> buildingEntityList) {

		List<BuildingTO> mappedList = new ArrayList<>();

		for (BuildingEntity b : buildingEntityList) {
			mappedList.add(mapToTO(b));
		}

		return mappedList;
	}

}
