package com.capgemini.types;

import java.util.Date;
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
public class ClientTO {

	private Long id;
	private String lastName;
	private String firstName;
	private Address address;
	private String phoneNumber;
	private String eMail;
	private Date dateOfBirth;
	private List<Long> flatsOwnedIds;
	private List<Long> flatsCoOwnedIds;
	private Long version;

}
