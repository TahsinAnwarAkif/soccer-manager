package com.soccermanager.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;

import javax.persistence.*;

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
public class Player extends Persistent {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(optional = false, fetch = LAZY)
	@JoinColumn(name = "team_id", nullable = false)
	@JsonIgnore
	private Team team;

	@OneToOne(mappedBy = "player", optional = false, fetch = LAZY, orphanRemoval = true)
	@JsonIgnore
	private Transfer transfer;

	@Column(nullable = false)
	private String firstName;

	@Column(nullable = false)
	private String lastName;

	@Column(nullable = false)
	@Enumerated(value = STRING)
	private PlayerType type;

	@Column(nullable = false)
	@Enumerated(value = STRING)
	private Country country;

	private int age;
	private long value;

	@Transient
	private boolean presentInTransferList;

	public Player(String firstName, String lastName, PlayerType type, Country country, int age, long value) {
		this();

		this.firstName = firstName;
		this.lastName = lastName;
		this.type = type;
		this.country = country;
		this.age = age;
		this.value = value;
	}
}
