package com.soccermanager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.soccermanager.dto.TeamDto;
import com.soccermanager.service.TeamService;
import com.soccermanager.util.JSONObject;
import com.soccermanager.util.Url;
import com.soccermanager.validator.CountryValidator;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.soccermanager.util.CommonUtils.getFieldErrorMsgMap;
import static com.soccermanager.util.JSONObject.create;
import static com.soccermanager.util.JwtUtils.getLoggedInUserId;
import static com.soccermanager.util.SwaggerDoc.*;

/**
 * @author akif
 * @since 3/18/22
 */
@RestController
@RequestMapping(Url.TEAM)
@Api(value = TEAM_CONTROLLER, description = TEAM_CONTROLLER_DESC)
public class TeamController {

	@Autowired
	private TeamService teamService;

	@Autowired
	private CountryValidator countryValidator;

	@GetMapping
	@ApiOperation(value = TEAM_SHOW)
	@ApiResponses({
		@ApiResponse(code = 200, message = SUCCESS),
		@ApiResponse(code = 403, message = ACCESS_DENIED_ERROR_403)
	})
	public JSONObject show() throws JsonProcessingException {
		return create()
			.status(HttpStatus.OK)
			.currentTimestamp()
			.body("team", teamService.get(getLoggedInUserId()));
	}

	@PutMapping
	@ApiOperation(value = TEAM_UPDATE)
	@ApiResponses({
		@ApiResponse(code = 200, message = SUCCESS),
		@ApiResponse(code = 400, message = BAD_INPUT_ERROR_400),
		@ApiResponse(code = 403, message = ACCESS_DENIED_ERROR_403)
	})
	public JSONObject update(@Valid @RequestBody @ApiParam(name = TEAM, required = true) final TeamDto teamDto,
							 final Errors errors) throws JsonProcessingException {

		countryValidator.validate(teamDto.getCountry(), errors);

		if (errors.hasErrors()) {
			return create()
				.status(HttpStatus.BAD_REQUEST)
				.currentTimestamp()
				.body("errors", getFieldErrorMsgMap(errors));
		}

		return create()
			.status(HttpStatus.OK)
			.currentTimestamp()
			.body("team", teamService.update(teamDto));
	}
}
