package com.capgemini.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractEntity {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEntity.class);
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creation_time")
	private Date created;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time")
	private Date updated;

	@Version
	private Long version;

	@PrePersist
	protected void onCreate() {
		created = new Date();
	}

	@PreUpdate
	protected void onUpdate() {
		updated = new Date();
	}
	
	@PostLoad
	protected void onLoad() {
		LOGGER.info(this.getClass().getSimpleName() + " with id: " + this.getId() + " creation time: " + this.getCreated());
		LOGGER.info(this.getClass().getSimpleName() + " with id: " + this.getId() + " update time: " + this.getUpdated());
		LOGGER.info(this.getClass().getSimpleName() + " with id: " + this.getId() + " current version: " + this.getVersion());
	
	}

}
