package com.capgemini.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.dao.BuildingRepository;
import com.capgemini.dao.FlatRepository;
import com.capgemini.domain.BuildingEntity;
import com.capgemini.domain.FlatEntity;
import com.capgemini.domain.enums.FlatStatus;
import com.capgemini.exception.CannotPerformActionException;
import com.capgemini.mappers.BuildingMapper;
import com.capgemini.mappers.FlatMapper;
import com.capgemini.service.BuildingService;
import com.capgemini.types.BuildingTO;
import com.capgemini.types.ClientTO;
import com.capgemini.types.FlatSearchParamsTO;
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
	public List<BuildingTO> findAllBuildings() {
		
		List<BuildingEntity> buildingEntityList = buildingDao.findAll();
		
		
		return buildingMapper.mapToTOList(buildingEntityList);
	}

	@Override
	public FlatTO addFlat(FlatTO newFlat, BuildingTO building) {
		
		
		
		
		BuildingEntity foundBuilding = buildingDao.findOne(building.getId());
		if(foundBuilding==null){
			throw new CannotPerformActionException("There is no building with that id!");
		}
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
	public FlatTO removeFlat(FlatTO flat) {

		flatDao.delete(flat.getId());
		Integer beforeDeleteFlatCount = buildingDao.findOne(flat.getBuildingId()).getFlatCount();
		buildingDao.findOne(flat.getBuildingId()).setFlatCount(beforeDeleteFlatCount-1);
		
		return flat;
	}
	//TODO
	@Override
	public Double countPricesSumOfFlatsBoughtByClient(ClientTO client) {
		
		return buildingDao.countPricesSumOfFlatsBoughtByClient(client.getId());
	}

	@Override
	public Double countAveragePriceOfFlatInTheBuilding(BuildingTO building) {
		
		return buildingDao.countAveragePriceOfFlatInTheBuilding(building.getId());
	}

	@Override
	public List<FlatTO> findUnsoldFlatsByCriteria(FlatSearchParamsTO flatSearchParamsTO) {
		List<FlatEntity> foundFlats = flatDao.findUnsoldFlatsByCriteria(flatSearchParamsTO);

		
		return flatMapper.mapToTOList(foundFlats);
	}

	@Override
	public Long countNumberOfFlatsByStatus(FlatStatus flatStatus, Long buildingId) {
		
		return buildingDao.countNumberOfFlatsByStatus(flatStatus, buildingId);
	}

	//TODO
	@Override
	public List<BuildingTO> findBuildingsWithMaxmimumNumberFreeFlats() {

		List<BuildingEntity> foundBuildings = buildingDao.findBuildingsWithMaxmimumNumberFreeFlats();
		
		
		return buildingMapper.mapToTOList(foundBuildings);
	}

	@Override
	public Long countFreeFlatFromBuilding(BuildingTO building) {


		
		
		
		return buildingDao.countFreeFlatFromBuilding(building.getId());
	}
	
	
	
	
	
	
	

}
