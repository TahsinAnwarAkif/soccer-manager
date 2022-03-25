package com.soccermanager.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

import static com.soccermanager.util.Constants.USERNAME_REGEX;
import static com.soccermanager.util.SwaggerDoc.*;

/**
 * @author akif
 * @since 3/17/22
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class UserDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotBlank
	@Size(min = 1, max = 50)
	@Pattern(regexp = USERNAME_REGEX, message = "{error.user.username.invalid}")
	@ApiModelProperty(required = true, notes = USER_USERNAME_NOTES)
	private String username;

	@NotBlank
	@Size(min = 1, max = 255)
	@Email
	@ApiModelProperty(notes = USER_EMAIL_NOTES)
	private String email;

	@NotBlank
	@Size(min = 1, max = 1024)
	@ApiModelProperty(required = true, notes = USER_PASSWORD_NOTES)
	private String password;

	public UserDto(String username, String password) {
		this();

		this.username = username;
		this.password = password;
	}
}
