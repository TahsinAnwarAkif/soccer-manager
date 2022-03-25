package com.soccermanager.util;

/**
 * @author akif
 * @since 3/17/22
 */
public interface Constants {

	String USERNAME_REGEX = "^[^\\s]+$";

	String TEAM_INITIAL_NAME = "My Team";
	long TEAM_INITIAL_BUDGET = 5000000L;
	long TEAM_INITIAL_VALUE = 20000000L;
	long TEAM_INITIAL_PLAYER_COUNT = 20L;

	long PLAYER_INITIAL_VALUE = 1000000L;
	int PLAYER_MIN_AGE = 18;
	int PLAYER_MAX_AGE = 40;

	int TRANSFER_RAISE_MIN_PERCENT = 10;
	int TRANSFER_RAISE_MAX_PERCENT = 100;
	String TRANSFER_LIST_CACHE = "transferListCache";
}
