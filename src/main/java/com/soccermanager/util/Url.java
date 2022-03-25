package com.soccermanager.util;

/**
 * @author akif
 * @since 03/17/22
 */
public interface Url {

	String API_VERSION1 = "/api/v1";
	String API_CURRENT_VERSION = API_VERSION1;

	String USER = API_CURRENT_VERSION + "/user";
	String USER_SIGNIN = "/signin";
	String USER_SIGNUP = "/signup";

	String TEAM = API_CURRENT_VERSION + "/team";

	String PLAYER = API_CURRENT_VERSION + "/players";
	String PLAYER_UPDATE = "/{id}";
	String PLAYER_COUNT = "/count";

	String TRANSFER = API_CURRENT_VERSION + "/transfers";
	String TRANSFER_REQUEST = "/{playerId}/request";
	String TRANSFER_APPLY = "/{playerId}/apply";
	String TRANSFER_COUNT = "/count";
}
