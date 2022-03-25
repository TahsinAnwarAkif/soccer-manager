package com.soccermanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.soccermanager.domain.Player;
import com.soccermanager.domain.Team;
import com.soccermanager.domain.Transfer;
import com.soccermanager.dto.TransferDisplayItemDto;
import com.soccermanager.dto.TransferRequestDto;
import com.soccermanager.exception.CustomException;
import com.soccermanager.repository.PlayerRepository;
import com.soccermanager.repository.TeamRepository;
import com.soccermanager.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;

import static com.soccermanager.util.CommonUtils.getRandomNumberBetweenRange;
import static com.soccermanager.util.Constants.TRANSFER_RAISE_MAX_PERCENT;
import static com.soccermanager.util.Constants.TRANSFER_RAISE_MIN_PERCENT;
import static com.soccermanager.util.DateUtils.getCurrentTimestamp;
import static com.soccermanager.util.JwtUtils.getLoggedInUserId;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * @author akif
 * @since 3/18/22
 */
@Service
public class TransferService {

	@Autowired
	private TransferRepository transferRepository;

	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private CacheService cacheService;

	@Autowired
	private MessageSourceAccessor msa;

	@Transactional
	public TransferDisplayItemDto save(final long id, final TransferRequestDto transferRequestDto) {
		Transfer transfer = new Transfer(transferRequestDto.getValue());
		transfer.setPlayer(playerRepository.get(id));

		long currentTimeStamp = getCurrentTimestamp();
		transfer.setCreated(currentTimeStamp);
		transfer.setUpdated(currentTimeStamp);

		transfer = transferRepository.save(transfer);

		cacheService.resetTransferListCache();

		return createDisplayItemDto(transfer);
	}

	@Transactional
	public Player apply(final long id) throws JsonProcessingException {
		final Transfer transfer = transferRepository.get(id);
		Player player = transfer.getPlayer();
		final long currentTimestamp = getCurrentTimestamp();

		assert player != null;

		final Team currentTeam = player.getTeam();
		currentTeam.setBudget(currentTeam.getBudget() + transfer.getValue());
		currentTeam.setValue(currentTeam.getValue() - player.getValue());
		currentTeam.setUpdated(currentTimestamp);
		teamRepository.update(currentTeam);

		final Team newTeam = teamRepository.get(getLoggedInUserId());
		final double valueToBeRaised = (getRandomNumberBetweenRange(TRANSFER_RAISE_MIN_PERCENT, TRANSFER_RAISE_MAX_PERCENT) / 100.0)
			* transfer.getValue();
		final long newValue = transfer.getValue() + (long) valueToBeRaised;

		newTeam.setBudget(newTeam.getBudget() - transfer.getValue());
		newTeam.setValue(newTeam.getValue() + newValue);
		newTeam.setUpdated(currentTimestamp);

		player.setTeam(teamRepository.update(newTeam));
		player.setTransfer(null);
		player.setValue(newValue);
		player.setUpdated(getCurrentTimestamp());

		player = playerRepository.update(player);

		cacheService.resetTransferListCache();

		return player;
	}

	public List<TransferDisplayItemDto> getList(final int offset, final int limit) {
		return transferRepository.getList(offset, limit);
	}

	public long getCount() {
		return transferRepository.getCount();
	}

	public void checkTransferRequestPossibility(final long id) throws JsonProcessingException {
		if (!playerRepository.isExistsInTeam(id, getLoggedInUserId())) {
			throw new CustomException(NOT_FOUND, msa.getMessage("error.player.not.found"));
		}

		if (transferRepository.isExists(id)) {
			throw new CustomException(BAD_REQUEST, msa.getMessage("error.transfer.player.exists"));
		}
	}

	public void checkTransferApplyPossibility(final long id) throws JsonProcessingException {
		if (!transferRepository.isExists(id)) {
			throw new CustomException(NOT_FOUND, msa.getMessage("error.transfer.invalid"));
		}

		if (transferRepository.isExistsForTeam(id, getLoggedInUserId())) {
			throw new CustomException(BAD_REQUEST, msa.getMessage("error.transfer.to.same.team"));
		}

		final long transferValue = transferRepository.getValue(id);
		final long teamBudget = teamRepository.getBudget(getLoggedInUserId());

		if (transferValue > teamBudget) {
			throw new CustomException(BAD_REQUEST, msa.getMessage("error.transfer.exceeding.budget",
				new Object[]{transferValue, teamBudget}));
		}
	}

	private TransferDisplayItemDto createDisplayItemDto(final Transfer transfer) {
		final Player player = transfer.getPlayer();
		final Team team = transfer.getPlayer().getTeam();

		return new TransferDisplayItemDto(BigInteger.valueOf(transfer.getPlayerId()),
			player.getFirstName(), player.getLastName(), player.getType().name(),
			player.getCountry().name(), player.getAge(), team.getName(), team.getCountry().name(),
			BigInteger.valueOf(transfer.getValue()), BigInteger.valueOf(transfer.getCreated()),
			BigInteger.valueOf(transfer.getUpdated()), transfer.getVersion());
	}
}
