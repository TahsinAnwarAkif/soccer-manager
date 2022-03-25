package com.soccermanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.soccermanager.domain.Country;
import com.soccermanager.domain.Player;
import com.soccermanager.domain.PlayerType;
import com.soccermanager.domain.Team;
import com.soccermanager.dto.PlayerDto;
import com.soccermanager.exception.CustomException;
import com.soccermanager.repository.PlayerRepository;
import com.soccermanager.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.soccermanager.domain.PlayerType.*;
import static com.soccermanager.util.CommonUtils.getRandomCountry;
import static com.soccermanager.util.CommonUtils.getRandomNumberBetweenRange;
import static com.soccermanager.util.Constants.*;
import static com.soccermanager.util.DateUtils.getCurrentTimestamp;
import static com.soccermanager.util.JwtUtils.getLoggedInUserId;
import static com.soccermanager.util.StringUtils.getInitCappedStr;
import static com.soccermanager.util.StringUtils.isNotEmpty;
import static java.lang.String.valueOf;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * @author akif
 * @since 3/17/22
 */
@Service
public class PlayerService {

	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private TransferRepository transferRepository;

	@Autowired
	private CacheService cacheService;

	@Autowired
	private MessageSourceAccessor msa;

	@Transactional
	public Player update(final long id, final PlayerDto playerDto) {
		Player player = playerRepository.get(id);

		assert player != null;

		if (isNotEmpty(playerDto.getFirstName())) {
			player.setFirstName(playerDto.getFirstName());
		}

		if (isNotEmpty(playerDto.getLastName())) {
			player.setLastName(playerDto.getLastName());
		}

		if (isNotEmpty(playerDto.getCountry())) {
			player.setCountry(Country.valueOf(playerDto.getCountry().toUpperCase()));
		}

		player.setUpdated(getCurrentTimestamp());

		player = playerRepository.update(player);

		if (transferRepository.isExists(id)) {
			cacheService.resetTransferListCache();
		}

		return player;
	}

	public List<Player> getListByTeam(final int offset, final int limit) throws JsonProcessingException {
		final List<Player> playerList = playerRepository.getListByTeam(getLoggedInUserId(), offset, limit);
		playerList.forEach(player -> player.setPresentInTransferList(transferRepository.isExists(player.getId())));

		return playerList;
	}

	public List<Player> getPreparedInitialList(final Team team) {
		final long currentTimestamp = getCurrentTimestamp();
		final List<Player> playerList = new ArrayList<>();
		final Map<PlayerType, Integer> playerTypeCountMap = new LinkedHashMap<>();

		for (int idx = 1; idx <= TEAM_INITIAL_PLAYER_COUNT; idx++) {
			final PlayerType playerType;

			if (idx <= 3) {
				playerType = GOALKEEPER;
			} else if (idx <= 9) {
				playerType = DEFENDER;
			} else if (idx <= 15) {
				playerType = MIDFIELDER;
			} else {
				playerType = ATTACKER;
			}

			final int count = playerTypeCountMap.getOrDefault(playerType, 1);
			final Player player = new Player(getInitCappedStr(playerType.name()), valueOf(count), playerType, getRandomCountry(),
				getRandomNumberBetweenRange(PLAYER_MIN_AGE, PLAYER_MAX_AGE), PLAYER_INITIAL_VALUE);
			player.setTeam(team);
			player.setCreated(currentTimestamp);
			player.setUpdated(currentTimestamp);

			playerList.add(player);

			playerTypeCountMap.put(playerType, count + 1);
		}

		return playerList;
	}

	public long getCountByTeam() throws JsonProcessingException {
		return playerRepository.getCountByTeam(getLoggedInUserId());
	}

	public void checkExistenceInTeam(final long playerId) throws JsonProcessingException {
		if (!playerRepository.isExistsInTeam(playerId, getLoggedInUserId())) {
			throw new CustomException(NOT_FOUND, msa.getMessage("error.player.not.found"));
		}
	}
}
