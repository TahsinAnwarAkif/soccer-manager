package com.soccermanager.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.io.Serializable;

import static com.soccermanager.util.SwaggerDoc.COUNTRY_NOTES;
import static com.soccermanager.util.SwaggerDoc.SIZE_MIN_1_MAX_255_NOTES;

/**
 * @author akif
 * @since 3/18/22
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class TeamDto implements Serializable {

	public static final long serialVersionUID = 1L;

	@Size(min = 1, max = 255)
	@ApiModelProperty(notes = SIZE_MIN_1_MAX_255_NOTES)
	private String name;

	@Size(min = 1, max = 64)
	@ApiModelProperty(notes = COUNTRY_NOTES)
	private String country;
}
