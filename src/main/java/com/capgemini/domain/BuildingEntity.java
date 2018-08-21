package com.capgemini.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;




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
@Table(name="BUILDING")
public class BuildingEntity extends AbstractEntity implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	

	@Column(nullable=true)
	private String description;
	@Embedded
	@Column(nullable=false)
	private Address location;
	@Column(nullable=false)
	private Integer storeysNumber;
	@Column(nullable=false)
	private Boolean hasElevator;
	@Column(nullable=false)
	private Integer flatCount;
	
	@OneToMany(mappedBy="building")
	private List<FlatEntity> flats;
	
	
	public void addFlat(FlatEntity flat){
		flats.add(flat);
		flat.setBuilding(this);
		
	}
	
	

}
