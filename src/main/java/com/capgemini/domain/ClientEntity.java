package com.capgemini.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
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
@Table(name="CLIENT")
public class ClientEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	@Column(nullable=false)
	private String lastName;
	@Column(nullable=false)
	private String firstName;
	@Embedded
	@Column(nullable=false)
	private Address address;
	@Column(nullable=false)
	private String phoneNumber;
	@Column(nullable=false)
	private String eMail;
	@Column(nullable=false)
	private Date dateOfBirth;
	
	@OneToMany(mappedBy="owner")
	private List<FlatEntity> flatsOwned;
	
	@ManyToMany
	@JoinTable(
    		name="CLIENT_FLAT",
    		joinColumns={ @JoinColumn(name="client_id")},
    		inverseJoinColumns = { @JoinColumn(name="flat_id") }
			)
	private List<FlatEntity> flatsCoOwned;
	
	
	
	
	
	
	

}
