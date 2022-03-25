package com.soccermanager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.soccermanager.dto.PlayerDto;
import com.soccermanager.service.PlayerService;
import com.soccermanager.util.JSONObject;
import com.soccermanager.util.Url;
import com.soccermanager.validator.CountryValidator;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.soccermanager.util.CommonUtils.getFieldErrorMsgMap;
import static com.soccermanager.util.JSONObject.create;
import static com.soccermanager.util.SwaggerDoc.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author akif
 * @since 3/18/22
 */
@RestController
@RequestMapping(Url.PLAYER)
@Api(value = PLAYER_CONTROLLER, description = PLAYER_CONTROLLER_DESC)
public class PlayerController {

	@Autowired
	private PlayerService playerService;

	@Autowired
	private CountryValidator countryValidator;

	@GetMapping
	@ApiOperation(value = PLAYER_SHOW)
	@ApiResponses({
		@ApiResponse(code = 200, message = SUCCESS),
		@ApiResponse(code = 403, message = ACCESS_DENIED_ERROR_403)
	})
	public JSONObject showByTeam(@RequestParam(defaultValue = "0") @ApiParam(value = OFFSET) final int offset,
								 @RequestParam(defaultValue = "0") @ApiParam(value = LIMIT) final int limit) throws JsonProcessingException {

		return create()
			.status(OK)
			.currentTimestamp()
			.body("playerList", playerService.getListByTeam(offset, limit));
	}

	@GetMapping(Url.PLAYER_COUNT)
	@ApiOperation(value = PLAYER_COUNT_SHOW)
	@ApiResponses({
		@ApiResponse(code = 200, message = SUCCESS),
		@ApiResponse(code = 403, message = ACCESS_DENIED_ERROR_403)
	})
	public JSONObject showCountByTeam() throws JsonProcessingException {
		return create()
			.status(OK)
			.currentTimestamp()
			.body("count", playerService.getCountByTeam());
	}

	@PutMapping(Url.PLAYER_UPDATE)
	@ApiOperation(value = PLAYER_UPDATE)
	@ApiResponses({
		@ApiResponse(code = 200, message = SUCCESS),
		@ApiResponse(code = 400, message = BAD_INPUT_ERROR_400),
		@ApiResponse(code = 403, message = ACCESS_DENIED_ERROR_403),
		@ApiResponse(code = 404, message = PLAYER_NOT_FOUND_IN_TEAM_ERROR_404)
	})
	public JSONObject update(@PathVariable @ApiParam(name = ID, required = true) final long id,
							 @Valid @RequestBody @ApiParam(name = PLAYER, required = true) final PlayerDto playerDto,
							 final Errors errors) throws JsonProcessingException {

		playerService.checkExistenceInTeam(id);
		countryValidator.validate(playerDto.getCountry(), errors);

		if (errors.hasErrors()) {
			return create()
				.status(BAD_REQUEST)
				.currentTimestamp()
				.body("errors", getFieldErrorMsgMap(errors));
		}

		return create()
			.status(OK)
			.currentTimestamp()
			.body("player", playerService.update(id, playerDto));
	}
}
