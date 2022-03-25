package com.soccermanager.repository;

import com.soccermanager.domain.Player;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

/**
 * @author akif
 * @since 3/17/22
 */
@Repository
public class PlayerRepository {

	@PersistenceContext
	private EntityManager em;

	@Transactional
	public Player update(final Player player) {
		return em.merge(player);
	}

	public Player get(final long id) {
		return em.find(Player.class, id);
	}

	public long getAnyIdByTeam(final long teamId) {
		try {
			return em.createQuery("SELECT p.id"
					+ " FROM Player p"
					+ " WHERE p.team.id = :teamId", Long.class)
				.setParameter("teamId", teamId)
				.setMaxResults(1)
				.getSingleResult();
		} catch (NoResultException e) {
			return -1L;
		}
	}

	public List<Player> getListByTeam(final long teamId, final int offset, final int limit) {
		final TypedQuery<Player> query = em.createQuery("FROM Player p"
				+ " WHERE p.team.id = :teamId"
				+ " ORDER BY p.firstName, p.lastName", Player.class)
			.setParameter("teamId", teamId);

		if (offset != 0) {
			query.setFirstResult(offset);
		}

		if (limit != 0) {
			query.setMaxResults(limit);
		}

		return query.getResultList();
	}

	public long getCountByTeam(final long teamId) {
		return em.createQuery("SELECT COUNT(*)"
				+ " FROM Player p"
				+ " WHERE p.team.id = :teamId", Long.class)
			.setParameter("teamId", teamId)
			.setMaxResults(1)
			.getSingleResult();
	}

	public boolean isExistsInTeam(final long playerId, final long teamId) {
		try {
			return em.createQuery("SELECT 1"
					+ " FROM Player p"
					+ " WHERE p.id = :playerId AND p.team.id = :teamId", Integer.class)
				.setParameter("teamId", teamId)
				.setParameter("playerId", playerId)
				.setMaxResults(1)
				.getSingleResult() != null;
		} catch (NoResultException e) {
			return false;
		}
	}
}
