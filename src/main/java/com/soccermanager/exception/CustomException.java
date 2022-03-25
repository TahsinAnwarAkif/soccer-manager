package com.soccermanager.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author akif
 * @since 03/17/22
 */
@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {

	private final HttpStatus status;
	private final String message;
}
