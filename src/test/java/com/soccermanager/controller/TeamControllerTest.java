package com.soccermanager.controller;

import com.soccermanager.dto.TeamDto;
import com.soccermanager.dto.UserDto;
import com.soccermanager.repository.UserRepository;
import com.soccermanager.util.JSONObject;
import com.soccermanager.validator.CountryValidator;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.validation.Errors;

import java.util.Map;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static com.soccermanager.util.CommonUtils.*;
import static com.soccermanager.util.JwtUtils.extractUsername;
import static com.soccermanager.util.TestConstants.*;
import static com.soccermanager.util.Url.*;
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
@Order(2)
public class TeamControllerTest {

	private static String jwt;
	private static long userId;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CountryValidator countryValidator;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void showWithoutAuthentication() throws Exception {
		mockMvc
			.perform(
				get(TEAM)
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
				get(TEAM)
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.header(AUTHORIZATION, "Bearer " + jwt));

		resultActions.andExpect(status().isOk());

		final MvcResult result = resultActions.andReturn();
		final JSONObject json = getObjectFromJson(result.getResponse().getContentAsString(), JSONObject.class);
		final Map<String, Object> teamMap = getObjectFromJsonBody(json, "team");

		assertEquals(String.valueOf(teamMap.get("userId")), String.valueOf(userId));
	}

	@Test
	public void updateWithoutAuthentication() throws Exception {
		mockMvc
			.perform(
				put(TEAM)
					.content(getStringFromObject(new TeamDto(TEST_TEAM_NAME_UPDATED, TEST_TEAM_COUNTRY_UPDATED)))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isForbidden());
	}

	@Test
	public void updateWithAuthentication() throws Exception {
		setupJwt();
		assertEquals(TEST_USER_USERNAME, extractUsername(jwt));

		countryValidator.validate(TEST_TEAM_COUNTRY_UPDATED + "123", Mockito.any(Errors.class));

		final ResultActions resultActions = mockMvc
			.perform(
				put(TEAM)
					.content(getStringFromObject(new TeamDto(TEST_TEAM_NAME_UPDATED, TEST_TEAM_COUNTRY_UPDATED)))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.header(AUTHORIZATION, "Bearer " + jwt));

		resultActions.andExpect(status().isOk());

		final MvcResult result = resultActions.andReturn();
		final JSONObject json = getObjectFromJson(result.getResponse().getContentAsString(), JSONObject.class);
		final Map<String, Object> teamMap = getObjectFromJsonBody(json, "team");

		assertEquals(String.valueOf(teamMap.get("userId")), String.valueOf(userId));
		assertEquals(teamMap.get("name"), TEST_TEAM_NAME_UPDATED);
		assertEquals(teamMap.get("country"), TEST_TEAM_COUNTRY_UPDATED);
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
