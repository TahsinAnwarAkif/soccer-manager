package com.soccermanager.repository;

import com.soccermanager.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * @author akif
 * @since 3/18/22
 */
@Repository
public class CustomUserDetailsRepository implements UserDetailsService {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private MessageSourceAccessor msa;

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		try {
			return em.createQuery("FROM User u"
					+ " WHERE u.username = :username", UserDetails.class)
				.setParameter("username", username)
				.setMaxResults(1)
				.getSingleResult();
		} catch (Exception e) {
			throw new CustomException(FORBIDDEN, msa.getMessage("error.user.not.found", new String[]{username}));
		}
	}
}
