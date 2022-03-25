package com.soccermanager.controller;

import com.soccermanager.dto.PlayerDto;
import com.soccermanager.dto.UserDto;
import com.soccermanager.repository.PlayerRepository;
import com.soccermanager.repository.UserRepository;
import com.soccermanager.util.CommonUtils;
import com.soccermanager.util.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Map;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static com.soccermanager.util.CommonUtils.*;
import static com.soccermanager.util.JwtUtils.extractUsername;
import static com.soccermanager.util.TestConstants.*;
import static com.soccermanager.util.Url.*;
import static java.lang.Long.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author akif
 * @since 3/21/22
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Order(3)
public class PlayerControllerTest {

	private static String jwt;
	private static long userId;

	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void showWithoutAuthentication() throws Exception {
		mockMvc
			.perform(
				get(PLAYER)
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isForbidden());
	}

	@Test
	public void showWithAuthentication() throws Exception {
		setupJwt();
		assertEquals(TEST_USER_USERNAME, extractUsername(jwt));

		final ResultActions resultActions = mockMvc
			.perform(
				get(PLAYER)
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.header(AUTHORIZATION, "Bearer " + jwt));

		resultActions.andExpect(status().isOk());

		final MvcResult result = resultActions.andReturn();
		final JSONObject json = getObjectFromJson(result.getResponse().getContentAsString(), JSONObject.class);
		final List<?> list = CommonUtils.getObjectFromJsonBody(json, "playerList");

		assertEquals(list.size(), playerRepository.getCountByTeam(userId));
	}

	@Test
	public void showCountWithoutAuthentication() throws Exception {
		mockMvc
			.perform(
				get(PLAYER + PLAYER_COUNT)
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isForbidden());
	}

	@Test
	public void showCountWithAuthentication() throws Exception {
		setupJwt();
		assertEquals(TEST_USER_USERNAME, extractUsername(jwt));

		final ResultActions resultActions = mockMvc
			.perform(
				get(PLAYER + PLAYER_COUNT)
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.header(AUTHORIZATION, "Bearer " + jwt));

		resultActions.andExpect(status().isOk());

		final MvcResult result = resultActions.andReturn();
		final JSONObject json = getObjectFromJson(result.getResponse().getContentAsString(), JSONObject.class);
		final Integer count = getObjectFromJsonBody(json, "count");

		assertEquals(valueOf(count), playerRepository.getCountByTeam(userId));
	}

	@Test
	public void updateWithoutAuthentication() throws Exception {
		mockMvc
			.perform(
				put(PLAYER + PLAYER_UPDATE, 1L)
					.content(getStringFromObject(new PlayerDto(TEST_PLAYER_FIRSTNAME, TEST_PLAYER_LASTNAME, TEST_PLAYER_COUNTRY)))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isForbidden());
	}

	@Test
	public void updateWithAuthentication() throws Exception {
		setupJwt();
		assertEquals(TEST_USER_USERNAME, extractUsername(jwt));

		final long updatingPlayerId = playerRepository.getAnyIdByTeam(userId);
		final ResultActions resultActions = mockMvc
			.perform(
				put(PLAYER + PLAYER_UPDATE, updatingPlayerId)
					.content(getStringFromObject(new PlayerDto(TEST_PLAYER_FIRSTNAME, TEST_PLAYER_LASTNAME, TEST_PLAYER_COUNTRY)))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.header(AUTHORIZATION, "Bearer " + jwt));

		resultActions.andExpect(status().isOk());

		final MvcResult result = resultActions.andReturn();
		final JSONObject json = getObjectFromJson(result.getResponse().getContentAsString(), JSONObject.class);
		final Map<String, Object> playerMap = getObjectFromJsonBody(json, "player");

		assertEquals(String.valueOf(playerMap.get("id")), String.valueOf(updatingPlayerId));
		assertEquals(playerMap.get("firstName"), TEST_PLAYER_FIRSTNAME);
		assertEquals(playerMap.get("lastName"), TEST_PLAYER_LASTNAME);
		assertEquals(playerMap.get("country"), TEST_PLAYER_COUNTRY);
	}

	@SuppressWarnings("unchecked")
	private void setupJwt() throws Exception {
		if (jwt != null) {
			return;
		}

		if (userId == 0L) {
			userId = userRepository.getIdByUsername(TEST_USER_USERNAME);
		}

		final ResultActions resultActions = mockMvc
			.perform(
				post(USER + USER_SIGNIN)
					.content(getStringFromObject(new UserDto(TEST_USER_USERNAME, TEST_USER_EMAIL, TEST_USER_PASSWORD)))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON));

		resultActions.andExpect(status().isOk());

		final MvcResult result = resultActions.andReturn();
		final JSONObject json = getObjectFromJson(result.getResponse().getContentAsString(), JSONObject.class);
		jwt = getObjectFromJsonBody(json, "jwt");
	}
}
