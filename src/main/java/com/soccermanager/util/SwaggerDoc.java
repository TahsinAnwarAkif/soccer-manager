package com.soccermanager.util;

/**
 * @author akif
 * @since 3/20/22
 */
public interface SwaggerDoc {

	String SUCCESS = "Successful";
	String BAD_INPUT_ERROR_400 = "Error While Processing Input Fields";
	String ACCESS_DENIED_ERROR_403 = "Access Denied";

	String USER = "user";
	String USER_CONTROLLER = "UserController";
	String USER_CONTROLLER_DESC = "APIs for User Signin/Signup";
	String USER_SIGNIN = "User Signin";
	String USER_NAME_PASS_INVALID_ERROR_403 = "Invalid Username and/or Password provided";
	String USER_SIGNUP = "User Signup";
	String USER_SIGNUP_SUCCESS = "Successful, please signin to get your auth-token";
	String USER_USERNAME_NOTES = "Size = (1 ~ 50), should not contain any whitespace"
		+ ", should not be already taken";
	String USER_EMAIL_NOTES = "Required for Signup only. Size = (1 ~ 255), should be well-formatted"
		+ ", should not be already taken";
	String USER_PASSWORD_NOTES = "Size = (1 ~ 1024)";

	String TEAM = "team";
	String TEAM_CONTROLLER = "TeamController";
	String TEAM_CONTROLLER_DESC = "APIs for Showing/Updating Own Team";
	String TEAM_SHOW = "Team Info Show";
	String TEAM_UPDATE = "Team Info Update";

	String PLAYER = "player";
	String PLAYER_CONTROLLER = "PlayerController";
	String PLAYER_CONTROLLER_DESC = "APIs for Showing/Updating Own Team Players";
	String PLAYER_SHOW = "Team Player Info Show";
	String PLAYER_COUNT_SHOW = "Team Player Count Show";
	String PLAYER_UPDATE = "Team Player Info Update";

	String TRANSFER = "transfer";
	String TRANSFER_CONTROLLER = "TransferController";
	String TRANSFER_CONTROLLER_DESC = "APIs for Showing/Requesting/Applying Player Transfers";
	String TRANSFER_SHOW = "Transferable Player Show";
	String TRANSFER_COUNT_SHOW = "Transferable Player Count Show";
	String TRANSFER_REQUEST = "Own Team Player Transfer Request";
	String TRANSFER_APPLY = "Player Transfer Apply to Own Team";
	String TRANSFER_BAD_INPUT_OR_ALREADY_EXISTS_ERROR_400 = "Error While Processing Input Fields"
		+ " / Player already exists in Transferable Player List";
	String TRANSFER_OWN_PLAYER_OR_INSUFFICIENT_BUDGET_ERROR_400 = "Own Player Transfer to Own Team Not Allowed"
		+ " / Player Transfer Value Cannot exceed Own Team Budget";
	String PLAYER_NOT_FOUND_IN_TRANSFER_LIST_ERROR_404 = "Player Not Found in Transferable Player List";

	String ID = "id";
	String PLAYER_ID = "playerId";
	String OFFSET = "offset";
	String LIMIT = "limit";
	String PLAYER_NOT_FOUND_IN_TEAM_ERROR_404 = "Player Not Found in Team";
	String COUNTRY_NOTES = "Size= (1 ~ 64), should be valid";
	String SIZE_MIN_1_MAX_255_NOTES = "Size = (1 ~ 255)";
	String MIN_1_NOTES = "Min = 1";
}
