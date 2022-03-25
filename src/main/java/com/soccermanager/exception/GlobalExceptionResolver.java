package com.soccermanager.exception;

import com.soccermanager.util.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.soccermanager.util.JSONObject.create;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @author akif
 * @since 3/17/22
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionResolver {

	@ExceptionHandler({CustomException.class})
	public JSONObject handleCustomException(final CustomException e) {
		return create()
			.status(e.getStatus())
			.currentTimestamp()
			.body("error", e.getLocalizedMessage());
	}

	@ExceptionHandler(Exception.class)
	public JSONObject handleException(final Exception e) {
		log.error("Exception occurred: " + e);
		e.printStackTrace();

		return create()
			.status(BAD_REQUEST)
			.currentTimestamp()
			.body("error", e.getLocalizedMessage());
	}
}
