package com.soccermanager.repository;

import com.soccermanager.domain.Team;
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
public class TeamRepository {

	@PersistenceContext
	private EntityManager em;

	@Transactional
	public Team update(final Team team) {
		return em.merge(team);
	}

	public Team get(final long id) {
		return em.find(Team.class, id);
	}

	public long getBudget(final long id) {
		try {
			return em.createQuery("SELECT t.budget"
					+ " FROM Team t"
					+ " WHERE t.userId = :id", Long.class)
				.setParameter("id", id)
				.setMaxResults(1)
				.getSingleResult();
		} catch (NoResultException e) {
			return -1L;
		}
	}
}
