package com.soccermanager.repository;

import com.soccermanager.domain.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 * @author akif
 * @since 3/17/22
 */
@Repository
public class UserRepository {

	@PersistenceContext
	private EntityManager em;

	@Transactional
	public User save(final User user) {
		em.persist(user);
		em.flush();

		return user;
	}

	public long getIdByUsername(final String username) {
		try {
			return em.createQuery("SELECT id"
					+ " FROM User"
					+ " WHERE username = :username", Long.class)
				.setParameter("username", username)
				.setMaxResults(1)
				.getSingleResult();
		} catch (NoResultException e) {
			return -1L;
		}
	}

	public boolean isUsernameTaken(final String username) {
		return isUsernameOrEmailTaken(username, null);
	}

	public boolean isEmailTaken(final String email) {
		return isUsernameOrEmailTaken(null, email);
	}

	public boolean isUsernameOrEmailTaken(final String username, final String email) {
		try {
			return em.createQuery("SELECT 1"
					+ " FROM User u"
					+ " WHERE u.username = :username OR u.email = :email", Integer.class)
				.setParameter("username", username)
				.setParameter("email", email)
				.setMaxResults(1)
				.getSingleResult() != null;
		} catch (NoResultException e) {
			return false;
		}
	}
}
