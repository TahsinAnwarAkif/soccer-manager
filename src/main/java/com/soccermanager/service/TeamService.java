package com.soccermanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.soccermanager.domain.Country;
import com.soccermanager.domain.Team;
import com.soccermanager.domain.User;
import com.soccermanager.dto.TeamDto;
import com.soccermanager.repository.TeamRepository;
import com.soccermanager.repository.TransferRepository;
import com.soccermanager.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.soccermanager.util.CommonUtils.getRandomCountry;
import static com.soccermanager.util.Constants.*;
import static com.soccermanager.util.DateUtils.getCurrentTimestamp;
import static com.soccermanager.util.JwtUtils.getLoggedInUserId;

/**
 * @author akif
 * @since 3/17/22
 */
@Service
public class TeamService {

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private TransferRepository transferRepository;

	@Autowired
	private PlayerService playerService;

	@Autowired
	private CacheService cacheService;

	@Transactional
	public Team update(final TeamDto teamDto) throws JsonProcessingException {
		Team team = teamRepository.get(getLoggedInUserId());

		if (StringUtils.isNotEmpty(teamDto.getName())) {
			team.setName(teamDto.getName());
		}

		if (StringUtils.isNotEmpty(teamDto.getCountry())) {
			team.setCountry(Country.valueOf(teamDto.getCountry().toUpperCase()));
		}

		team.setUpdated(getCurrentTimestamp());

		team = teamRepository.update(team);

		if (transferRepository.isExistsForAnyTeamPlayer(getLoggedInUserId())) {
			cacheService.resetTransferListCache();
		}

		return team;
	}

	public Team get(final long id) {
		return teamRepository.get(id);
	}

	public Team getPreparedInitialTeam(final User user) {
		final Team team = new Team(TEAM_INITIAL_NAME, getRandomCountry(), TEAM_INITIAL_BUDGET, TEAM_INITIAL_VALUE);
		team.setUser(user);
		team.setPlayerList(playerService.getPreparedInitialList(team));

		final long currentTimestamp = getCurrentTimestamp();
		team.setCreated(currentTimestamp);
		team.setUpdated(currentTimestamp);

		return team;
	}
}
