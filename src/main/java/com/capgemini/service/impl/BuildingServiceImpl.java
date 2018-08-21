package com.capgemini.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.dao.BuildingDao;
import com.capgemini.dao.FlatDao;
import com.capgemini.domain.BuildingEntity;
import com.capgemini.domain.FlatEntity;
import com.capgemini.mappers.BuildingMapper;
import com.capgemini.mappers.FlatMapper;
import com.capgemini.service.BuildingService;
import com.capgemini.types.BuildingTO;
import com.capgemini.types.FlatTO;

@Service
@Transactional
public class BuildingServiceImpl implements BuildingService{
	
	
	private BuildingDao buildingDao;
	
	private BuildingMapper buildingMapper;
	
	private FlatDao flatDao;
	
	private FlatMapper flatMapper;
	

	@Autowired
	public BuildingServiceImpl(BuildingDao buildingDao, BuildingMapper buildingMapper, FlatDao flatDao,
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
		// TODO Auto-generated method stub
		return null;
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
		
		return flatMapper.mapToTO(savedFlat);
	}

	@Override
	public FlatTO findFlatById(FlatTO flat) {

		FlatEntity foundFlat = flatDao.findOne(flat.getId());
		return flatMapper.mapToTO(foundFlat);
	}
	
	
	
	
	
	
	

}
