package com.capgemini.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class FlatSearchParamsTO {
	
	
	private Double areaMin;
	private Double areaMax;
	private Integer roomsCountMin;
	private Integer roomsCountMax;
	private Integer balconyCountMin;
	private Integer balconyCountMax;
	
	

}


