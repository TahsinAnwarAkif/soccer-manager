package com.soccermanager.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE;

/**
 * @author akif
 * @since 03/17/22
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Cacheable
@Cache(usage = READ_WRITE)
public class Team extends Persistent {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "teamIdGenerator")
	@GenericGenerator(name = "teamIdGenerator", strategy = "foreign",
		parameters = @org.hibernate.annotations.Parameter(name = "property", value = "user"))
	private long userId;

	@OneToOne(optional = false, fetch = LAZY)
	@PrimaryKeyJoinColumn
	@JsonIgnore
	private User user;

	@OneToMany(mappedBy = "team", cascade = ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Player> playerList;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	@Enumerated(value = STRING)
	private Country country;

	private long budget;
	private long value;

	public Team(String name, Country country, long budget, long value) {
		this();

		this.name = name;
		this.country = country;
		this.budget = budget;
		this.value = value;
	}
}
