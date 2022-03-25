package com.soccermanager.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import java.io.Serializable;

import static com.soccermanager.util.SwaggerDoc.MIN_1_NOTES;

/**
 * @author akif
 * @since 3/19/22
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class TransferRequestDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Min(1)
	@ApiModelProperty(required = true, notes = MIN_1_NOTES)
	private long value;
}
