package com.soccermanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author akif
 * @since 3/18/22
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class TransferDisplayItemDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private BigInteger playerId;
	private String playerFirstName;
	private String playerLastName;
	private String playerType;
	private String playerCountry;
	private int playerAge;

	private String teamName;
	private String teamCountry;

	private BigInteger transferValue;
	private BigInteger transferCreated;
	private BigInteger transferUpdated;
	private int transferVersion;
}
