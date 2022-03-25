package com.soccermanager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.soccermanager.dto.TransferRequestDto;
import com.soccermanager.service.PlayerService;
import com.soccermanager.service.TransferService;
import com.soccermanager.util.JSONObject;
import com.soccermanager.util.Url;
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
@RequestMapping(Url.TRANSFER)
@Api(value = TRANSFER_CONTROLLER, description = TRANSFER_CONTROLLER_DESC)
public class TransferController {

	@Autowired
	private TransferService transferService;

	@Autowired
	private PlayerService playerService;

	@GetMapping
	@ApiOperation(value = TRANSFER_SHOW)
	@ApiResponses({
		@ApiResponse(code = 200, message = SUCCESS),
		@ApiResponse(code = 403, message = ACCESS_DENIED_ERROR_403)
	})
	public JSONObject show(@RequestParam(defaultValue = "0") @ApiParam(value = OFFSET) final int offset,
						   @RequestParam(defaultValue = "0") @ApiParam(value = LIMIT) final int limit) {

		return create()
			.status(OK)
			.currentTimestamp()
			.body("transferList", transferService.getList(offset, limit));
	}

	@GetMapping(Url.TRANSFER_COUNT)
	@ApiOperation(value = TRANSFER_COUNT_SHOW)
	@ApiResponses({
		@ApiResponse(code = 200, message = SUCCESS),
		@ApiResponse(code = 403, message = ACCESS_DENIED_ERROR_403)
	})
	public JSONObject showCount() {
		return create()
			.status(OK)
			.currentTimestamp()
			.body("count", transferService.getCount());
	}

	@PostMapping(Url.TRANSFER_REQUEST)
	@ApiOperation(value = TRANSFER_REQUEST)
	@ApiResponses({
		@ApiResponse(code = 200, message = SUCCESS),
		@ApiResponse(code = 400, message = TRANSFER_BAD_INPUT_OR_ALREADY_EXISTS_ERROR_400),
		@ApiResponse(code = 403, message = ACCESS_DENIED_ERROR_403),
		@ApiResponse(code = 404, message = PLAYER_NOT_FOUND_IN_TEAM_ERROR_404)
	})
	public JSONObject request(@PathVariable @ApiParam(name = PLAYER_ID, required = true) final long playerId,
							  @Valid
							  @RequestBody
							  @ApiParam(name = TRANSFER, required = true) final TransferRequestDto transferRequestDto,
							  final Errors errors) throws JsonProcessingException {

		transferService.checkTransferRequestPossibility(playerId);

		if (errors.hasErrors()) {
			return create()
				.status(BAD_REQUEST)
				.currentTimestamp()
				.body("errors", getFieldErrorMsgMap(errors));
		}

		return create()
			.status(OK)
			.currentTimestamp()
			.body("transfer", transferService.save(playerId, transferRequestDto));
	}

	@PostMapping(Url.TRANSFER_APPLY)
	@ApiOperation(value = TRANSFER_APPLY)
	@ApiResponses({
		@ApiResponse(code = 200, message = SUCCESS),
		@ApiResponse(code = 400, message = TRANSFER_OWN_PLAYER_OR_INSUFFICIENT_BUDGET_ERROR_400),
		@ApiResponse(code = 403, message = ACCESS_DENIED_ERROR_403),
		@ApiResponse(code = 404, message = PLAYER_NOT_FOUND_IN_TRANSFER_LIST_ERROR_404)
	})
	public JSONObject apply(@PathVariable
							@ApiParam(name = PLAYER_ID, required = true) final long playerId) throws JsonProcessingException {

		transferService.checkTransferApplyPossibility(playerId);

		return create()
			.status(OK)
			.currentTimestamp()
			.body("player", transferService.apply(playerId));
	}
}
