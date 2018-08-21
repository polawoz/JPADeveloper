package com.capgemini.types;

import java.util.List;

import com.capgemini.domain.Address;
import com.capgemini.domain.enums.FlatStatus;

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
public class FlatTO {

	private Long id;
	private Double area;
	private Integer roomsCount;
	private Integer balconyCount;
	private Integer floorCount;
	private Address location;
	private FlatStatus status;
	private Double price;

	private Long buildingId;

	private Long ownerId;

	private List<Long> coOwnersId;

}
