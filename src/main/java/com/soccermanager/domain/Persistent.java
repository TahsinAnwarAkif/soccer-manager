package com.soccermanager.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.io.Serializable;

/**
 * @author akif
 * @since 03/17/22
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class Persistent implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(updatable = false)
	@JsonProperty
	private long created;

	@JsonProperty
	private long updated;

	@Version
	@JsonProperty
	private int version;
}
