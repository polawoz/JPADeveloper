package com.capgemini.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.dao.BuildingDao;
import com.capgemini.domain.BuildingEntity;
import com.capgemini.mappers.BuildingMapper;
import com.capgemini.service.BuildingService;
import com.capgemini.types.BuildingTO;

@Service
@Transactional
public class BuildingServiceImpl implements BuildingService{
	
	
	private BuildingDao buildingDao;
	
	private BuildingMapper buildingMapper;
	
	@Autowired
	public BuildingServiceImpl(BuildingDao buildingDao, BuildingMapper buildingMapper){
		this.buildingDao=buildingDao;
		this.buildingMapper=buildingMapper;
		
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
	
	
	
	
	
	
	

}
