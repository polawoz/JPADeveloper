package com.capgemini.domain;


import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.capgemini.domain.enums.FlatStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="FLAT")
public class FlatEntity {
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	@Column(nullable=false)
	private Double area;
	@Column(nullable=false)
	private Integer roomsCount;
	@Column(nullable=false)
	private Integer balconyCount;
	@Column(nullable=false)
	private Integer floorCount;
	@Column(nullable=false)
	private Address location;
	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private FlatStatus status;
	@Column(nullable=false)
	private Double price;

	@ManyToOne
	private BuildingEntity building;
	
	@ManyToOne
	private ClientEntity owner;
	
	@ManyToMany(mappedBy="flatsCoOwned")
	private List<ClientEntity> coOwners;
	
	
	
	
	

}
