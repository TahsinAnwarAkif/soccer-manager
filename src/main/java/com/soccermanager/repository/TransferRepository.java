package com.soccermanager.repository;

import com.soccermanager.domain.Transfer;
import com.soccermanager.dto.TransferDisplayItemDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import static com.soccermanager.util.Constants.TRANSFER_LIST_CACHE;

/**
 * @author akif
 * @since 3/18/22
 */
@Repository
public class TransferRepository {

	@PersistenceContext
	private EntityManager em;

	@Transactional
	public Transfer save(final Transfer transfer) {
		em.persist(transfer);
		em.flush();

		return transfer;
	}

	public Transfer get(final long id) {
		return em.find(Transfer.class, id);
	}

	@SuppressWarnings("unchecked")
	@Cacheable(value = TRANSFER_LIST_CACHE, key = "T(java.util.Objects).hash(#p0,#p1)")
	public List<TransferDisplayItemDto> getList(final int offset, final int limit) {
		final Query query = em.createNativeQuery("SELECT tr.player_id AS playerId"
			+ ", p.first_name AS playerFirstName, p.last_name AS playerLastName"
			+ ", p.type AS playerType, p.country AS playerCountry, p.age AS playerAge"
			+ ", te.name AS teamName, te.country AS teamCountry, tr.value AS transferValue"
			+ ", tr.created AS transferCreated, tr.updated AS transferUpdated, tr.version AS transferVersion"
			+ " FROM Transfer tr"
			+ " JOIN Player p ON p.id = tr.player_id"
			+ " JOIN Team te  ON te.user_id = p.team_id"
			+ " ORDER BY tr.value DESC");

		if (offset != 0) {
			query.setFirstResult(offset);
		}

		if (limit != 0) {
			query.setMaxResults(limit);
		}

		final List<Object[]> itemList = query.getResultList();

		return itemList
			.stream()
			.map(this::createDisplayItemDto)
			.collect(Collectors.toList());
	}

	public long getValue(final long id) {
		try {
			return em.createQuery("SELECT value"
					+ " FROM Transfer t"
					+ " WHERE t.playerId = :id", Long.class)
				.setParameter("id", id)
				.setMaxResults(1)
				.getSingleResult();
		} catch (NoResultException e) {
			return -1L;
		}
	}

	public long getCount() {
		return em.createQuery("SELECT COUNT(*)"
				+ " FROM Transfer", Long.class)
			.getSingleResult();
	}

	public boolean isExists(final long id) {
		try {
			return em.createQuery("SELECT 1"
					+ " FROM Transfer t"
					+ " WHERE t.playerId = :id", Integer.class)
				.setParameter("id", id)
				.setMaxResults(1)
				.getSingleResult() != null;
		} catch (NoResultException e) {
			return false;
		}
	}

	public boolean isExistsForTeam(final long playerId, long teamId) {
		try {
			return em.createQuery("SELECT 1"
					+ " FROM Transfer tr"
					+ " JOIN Player p ON p.id = tr.playerId"
					+ " JOIN Team te ON te.userId = p.team.id"
					+ " WHERE tr.playerId = :playerId"
					+ " AND p.team.id = :teamId", Integer.class)
				.setParameter("playerId", playerId)
				.setParameter("teamId", teamId)
				.setMaxResults(1)
				.getSingleResult() != null;
		} catch (NoResultException e) {
			return false;
		}
	}

	public boolean isExistsForAnyTeamPlayer(final long teamId) {
		try {
			return em.createQuery("SELECT 1"
					+ " FROM Transfer tr"
					+ " JOIN Player p ON p.id = tr.playerId"
					+ " WHERE p.team.id = :teamId", Integer.class)
				.setParameter("teamId", teamId)
				.setMaxResults(1)
				.getSingleResult() != null;
		} catch (NoResultException e) {
			return false;
		}
	}

	private TransferDisplayItemDto createDisplayItemDto(Object[] item) {
		return new TransferDisplayItemDto((BigInteger) item[0], (String) item[1],
			(String) item[2], (String) item[3],
			(String) item[4], (Integer) item[5],
			(String) item[6], (String) item[7],
			(BigInteger) item[8], (BigInteger) item[9],
			(BigInteger) item[10], (Integer) item[11]);
	}
}
