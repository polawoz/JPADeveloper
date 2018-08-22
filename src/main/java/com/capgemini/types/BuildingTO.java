package com.capgemini.types;

import java.util.List;

import com.capgemini.domain.Address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BuildingTO {
	
	
	
	private Long id;
	private String description;
	private Address location;
	private Integer storeysNumber;
	private Boolean hasElevator;
	private Integer flatCount;
	private List<Long> flats;
	private Long version;
	
	
	

}
