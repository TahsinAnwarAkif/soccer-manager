package com.soccermanager.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE;

/**
 * @author akif
 * @since 3/17/22
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Cacheable
@Cache(usage = READ_WRITE)
public class Transfer extends Persistent {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "playerIdGenerator")
	@GenericGenerator(name = "playerIdGenerator", strategy = "foreign",
		parameters = @org.hibernate.annotations.Parameter(name = "property", value = "player"))
	private long playerId;

	@OneToOne(optional = false, fetch = LAZY)
	@PrimaryKeyJoinColumn
	@JsonIgnore
	private Player player;

	private long value;

	public Transfer(long value) {
		this();

		this.value = value;
	}
}
