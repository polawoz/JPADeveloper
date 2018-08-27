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

		BuildingEntity newEntity = BuildingEntity.builder().description(newBuilding.getDescription())
				.location(createCopy(newBuilding.getLocation())).storeysNumber(newBuilding.getStoreysNumber())
				.hasElevator(newBuilding.getHasElevator()).flatCount(newBuilding.getFlatCount())
				.flats(new ArrayList<FlatEntity>())
				.build();

		newEntity.setVersion(newBuilding.getVersion());

		return newEntity;
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
		
		if(building==null){
			return null;
		}

		BuildingTO buildingTO = BuildingTO.builder().id(building.getId()).description(building.getDescription())
				.location(createCopy(building.getLocation())).storeysNumber(building.getStoreysNumber())
				.hasElevator(building.getHasElevator()).flatCount(building.getFlatCount())
				.flats(building.getFlats() == null ? new ArrayList<Long>() : mapCollectionToLong(building.getFlats()))
				.version(building.getVersion()).build();

		return buildingTO;
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
	
	

	public BuildingTO update(BuildingTO buildingTO, BuildingEntity buildingToUpdate) {

		if(buildingTO==null || buildingToUpdate==null){
			return null;
		}
		
		if (buildingTO.getDescription() != null) {
			buildingToUpdate.setDescription(buildingTO.getDescription());
		}

		if (buildingTO.getLocation() != null) {
			buildingToUpdate.setLocation(createCopy(buildingTO.getLocation()));
		}

		if (buildingTO.getStoreysNumber() != null) {
			buildingToUpdate.setStoreysNumber(buildingTO.getStoreysNumber());
		}

		if (buildingTO.getHasElevator() != null) {
			buildingToUpdate.setHasElevator(buildingTO.getHasElevator());
		}

		if (buildingTO.getFlatCount() != null) {
			buildingToUpdate.setFlatCount(buildingTO.getFlatCount());
		}

		return mapToTO(buildingToUpdate);
	}
	
	
	

}
