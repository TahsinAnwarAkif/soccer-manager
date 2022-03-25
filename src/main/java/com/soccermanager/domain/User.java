package com.soccermanager.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.Fetch;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;

import static javax.persistence.CascadeType.ALL;
import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE;
import static org.hibernate.annotations.FetchMode.SELECT;

/**
 * @author akif
 * @since 3/18/22
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Cacheable
@Cache(usage = READ_WRITE)
public class User extends Persistent implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@OneToOne(mappedBy = "user", optional = false, cascade = ALL, orphanRemoval = true)
	@Fetch(SELECT)
	@Cache(usage = READ_WRITE)
	private Team team;

	@Column(nullable = false, updatable = false, unique = true)
	private String username;

	@Column(nullable = false, updatable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	private boolean enabled;

	public User(String username, String email, String password) {
		this();

		this.username = username;
		this.email = email;
		this.password = password;
	}

	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return isEnabled();
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return isEnabled();
	}

	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return isEnabled();
	}
}
