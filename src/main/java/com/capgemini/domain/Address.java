package com.capgemini.domain;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Address {

	private String street;
	private String buildingNumber;
	private String flatNumber;
	private String postalCode;
	private String town;
	private String country;

}
