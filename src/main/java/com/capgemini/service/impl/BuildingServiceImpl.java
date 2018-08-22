package com.capgemini.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.dao.BuildingRepository;
import com.capgemini.dao.FlatRepository;
import com.capgemini.domain.BuildingEntity;
import com.capgemini.domain.FlatEntity;
import com.capgemini.exception.CannotPerformActionException;
import com.capgemini.mappers.BuildingMapper;
import com.capgemini.mappers.FlatMapper;
import com.capgemini.service.BuildingService;
import com.capgemini.types.BuildingTO;
import com.capgemini.types.ClientTO;
import com.capgemini.types.FlatTO;

@Service
@Transactional
public class BuildingServiceImpl implements BuildingService{
	
	
	private BuildingRepository buildingDao;
	
	private BuildingMapper buildingMapper;
	
	private FlatRepository flatDao;
	
	private FlatMapper flatMapper;
	

	@Autowired
	public BuildingServiceImpl(BuildingRepository buildingDao, BuildingMapper buildingMapper, FlatRepository flatDao,
			FlatMapper flatMapper) {
		this.buildingDao = buildingDao;
		this.buildingMapper = buildingMapper;
		this.flatDao = flatDao;
		this.flatMapper = flatMapper;
	}

	@Override
	public BuildingTO addBuilding(BuildingTO newBuilding) {
		
		BuildingEntity buildingEntity= buildingDao.save(buildingMapper.mapToEntity(newBuilding));
		
		return buildingMapper.mapToTO(buildingEntity);
	}

	@Override
	public BuildingTO findBuildingById(BuildingTO building) {
		
		BuildingEntity buildingEntity = buildingDao.findOne(building.getId());
		
	
	
		return buildingMapper.mapToTO(buildingEntity);
	}

	@Override
	public List<BuildingTO> findAll() {
		
		List<BuildingEntity> buildingEntityList = buildingDao.findAll();
		
		
		return buildingMapper.mapToTOList(buildingEntityList);
	}

	@Override
	public FlatTO addFlat(FlatTO newFlat, BuildingTO building) {

		BuildingEntity foundBuilding = buildingDao.findOne(building.getId());
		FlatEntity flatEntity= flatMapper.mapToEntity(newFlat);
		foundBuilding.addFlat(flatEntity);
		FlatEntity savedFlat = flatDao.save(flatEntity);
		foundBuilding.setFlatCount(foundBuilding.getFlatCount()+1);
		
		return flatMapper.mapToTO(savedFlat);
	}

	@Override
	public FlatTO findFlatById(FlatTO flat) {

		FlatEntity foundFlat = flatDao.findOne(flat.getId());
		return flatMapper.mapToTO(foundFlat);
	}

	@Override
	public FlatTO updateFlat(FlatTO flat) {
		
		FlatEntity flatToUpdate = flatDao.findOne(flat.getId());
		
		if(flat.getVersion()!=flatToUpdate.getVersion()){
			throw new CannotPerformActionException("Optimistic locking exception!");
		}

		return flatMapper.update(flat, flatToUpdate);
	}

	@Override
	public BuildingTO updateBuilding(BuildingTO building) {

		BuildingEntity buildingToUpdate = buildingDao.findOne(building.getId());
		
		if(building.getVersion()!=buildingToUpdate.getVersion()){
			throw new CannotPerformActionException("Optimistic locking exception!");
		}
		
		return buildingMapper.update(building, buildingToUpdate);
	}

	@Override
	public BuildingTO removeBuilding(BuildingTO building) {
		
		buildingDao.delete(building.getId());
		
		return building;
	}

	@Override
	public FlatTO removeFlat(FlatTO flat) {

		flatDao.delete(flat.getId());
		
		return flat;
	}

	@Override
	public Double countPricesSumOfFlatsBoughtByClient(ClientTO client) {
		
		return buildingDao.countPricesSumOfFlatsBoughtByClient(client.getId());
	}

	@Override
	public Double countAveragePriceOfFlatInTheBuilding(BuildingTO building) {
		
		return buildingDao.countAveragePriceOfFlatInTheBuilding(building.getId());
	}
	
	
	
	
	
	
	

}
