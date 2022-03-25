package com.soccermanager.controller;

import com.soccermanager.dto.UserDto;
import com.soccermanager.service.UserService;
import com.soccermanager.util.JSONObject;
import com.soccermanager.util.Url;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.soccermanager.util.CommonUtils.getFieldErrorMsgMap;
import static com.soccermanager.util.JSONObject.create;
import static com.soccermanager.util.SwaggerDoc.*;

/**
 * @author akif
 * @since 03/17/22
 */
@RestController
@RequestMapping(Url.USER)
@Api(value = USER_CONTROLLER, description = USER_CONTROLLER_DESC)
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping(Url.USER_SIGNIN)
	@ApiOperation(value = USER_SIGNIN)
	@ApiResponses({
		@ApiResponse(code = 200, message = SUCCESS),
		@ApiResponse(code = 403, message = USER_NAME_PASS_INVALID_ERROR_403)
	})
	public JSONObject signin(@RequestBody @ApiParam(value = USER, required = true) final UserDto userDto) {
		return create()
			.status(HttpStatus.OK)
			.currentTimestamp()
			.body("jwt", userService.getJwt(userDto.getUsername(), userDto.getPassword()));
	}

	@PostMapping(Url.USER_SIGNUP)
	@ApiOperation(value = USER_SIGNUP)
	@ApiResponses({
		@ApiResponse(code = 200, message = USER_SIGNUP_SUCCESS),
		@ApiResponse(code = 400, message = BAD_INPUT_ERROR_400),
	})
	public JSONObject signup(@Valid @RequestBody @ApiParam(value = USER, required = true) final UserDto userDto,
							 final Errors errors) {

		userService.checkUsernameEmailAvailability(userDto);

		if (errors.hasErrors()) {
			return create()
				.status(HttpStatus.BAD_REQUEST)
				.currentTimestamp()
				.body("errors", getFieldErrorMsgMap(errors));
		}

		return create()
			.status(HttpStatus.OK)
			.currentTimestamp()
			.body("user", userService.save(userDto));
	}
}
