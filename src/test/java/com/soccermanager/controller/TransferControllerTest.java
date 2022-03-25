package com.soccermanager.controller;

import com.soccermanager.dto.TransferRequestDto;
import com.soccermanager.dto.UserDto;
import com.soccermanager.repository.PlayerRepository;
import com.soccermanager.repository.TeamRepository;
import com.soccermanager.repository.TransferRepository;
import com.soccermanager.repository.UserRepository;
import com.soccermanager.service.UserService;
import com.soccermanager.util.JSONObject;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author akif
 * @since 3/21/22
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Order(4)
@TestMethodOrder(OrderAnnotation.class)
public class TransferControllerTest {

	private static String jwt;
	private static long userId;
	private static long transferringPlayerId;

	@Autowired
	private TransferRepository transferRepository;

	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@Order(1)
	public void showWithoutAuthentication() throws Exception {
		mockMvc
			.perform(
				get(TRANSFER)
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isForbidden());
	}

	@Test
	@Order(2)
	public void showWithAuthentication() throws Exception {
		setupCommonJwt();
		assertEquals(TEST_USER_USERNAME, extractUsername(jwt));

		final ResultActions resultActions = mockMvc
			.perform(
				get(TRANSFER)
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.header(AUTHORIZATION, "Bearer " + jwt));

		resultActions.andExpect(status().isOk());

		final MvcResult result = resultActions.andReturn();
		final JSONObject json = getObjectFromJson(result.getResponse().getContentAsString(), JSONObject.class);
		final List<?> list = getObjectFromJsonBody(json, "transferList");

		assertEquals(list.size(), transferRepository.getCount());
	}

	@Test
	@Order(3)
	public void showCountWithoutAuthentication() throws Exception {
		mockMvc
			.perform(
				get(TRANSFER + TRANSFER_COUNT)
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isForbidden());
	}

	@Test
	@Order(4)
	public void showCountWithAuthentication() throws Exception {
		setupCommonJwt();
		assertEquals(TEST_USER_USERNAME, extractUsername(jwt));

		final ResultActions resultActions = mockMvc
			.perform(
				get(TRANSFER + TRANSFER_COUNT)
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.header(AUTHORIZATION, "Bearer " + jwt));

		resultActions.andExpect(status().isOk());

		final MvcResult result = resultActions.andReturn();
		final JSONObject json = getObjectFromJson(result.getResponse().getContentAsString(), JSONObject.class);
		final Integer count = getObjectFromJsonBody(json, "count");

		assertEquals(valueOf(count), transferRepository.getCount());
	}

	@Test
	@Order(5)
	public void requestWithoutAuthentication() throws Exception {
		mockMvc
			.perform(
				post(TRANSFER + TRANSFER_REQUEST, 1L)
					.content(getStringFromObject(new TransferRequestDto(TEST_TRANSFER_VALUE)))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isForbidden());
	}

	@Test
	@Order(6)
	public void requestWithAuthentication() throws Exception {
		setupCommonJwt();
		assertEquals(TEST_USER_USERNAME, extractUsername(jwt));

		final long transferReqCount = transferRepository.getCount();
		final String status;

		if (!playerRepository.isExistsInTeam(transferringPlayerId, userId)) {
			status = "404 NOT_FOUND";
		} else if (transferRepository.isExists(transferringPlayerId)) {
			status = "400 BAD_REQUEST";
		} else {
			status = "200 OK";
		}

		final ResultActions resultActions = mockMvc
			.perform(
				post(TRANSFER + TRANSFER_REQUEST, transferringPlayerId)
					.content(getStringFromObject(new TransferRequestDto(TEST_TRANSFER_VALUE)))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.header(AUTHORIZATION, "Bearer " + jwt));

		resultActions.andExpect(status().isOk());

		final MvcResult result = resultActions.andReturn();
		final JSONObject json = getObjectFromJson(result.getResponse().getContentAsString(), JSONObject.class);

		assertEquals(status, json.get("status"));

		if ("200 OK".equals(status)) {
			final Map<String, Object> transferMap = getObjectFromJsonBody(json, "transfer");

			assertEquals(String.valueOf(transferMap.get("playerId")), String.valueOf(transferringPlayerId));
			assertEquals(transferRepository.getCount(), transferReqCount + 1);
		}
	}

	@Test
	@Order(7)
	public void applyWithoutAuthentication() throws Exception {
		mockMvc
			.perform(
				post(TRANSFER + TRANSFER_APPLY, 1L)
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isForbidden());
	}

	@Test
	@Order(8)
	public void applyWithAuthentication() throws Exception {
		if (!userRepository.isUsernameOrEmailTaken(TEST_USER2_USERNAME, TEST_USER2_EMAIL)) {
			userService.save(new UserDto(TEST_USER2_USERNAME, TEST_USER2_EMAIL, TEST_USER2_PASSWORD));
		}

		final long userId = userRepository.getIdByUsername(TEST_USER2_USERNAME);

		setupJwtForTransferApply();
		assertEquals(TEST_USER2_USERNAME, extractUsername(jwt));

		final long transferReqCount = transferRepository.getCount();
		final String status;

		if (!transferRepository.isExists(transferringPlayerId)) {
			status = "404 NOT_FOUND";
		} else if (transferRepository.isExistsForTeam(transferringPlayerId, userId)) {
			status = "400 BAD_REQUEST";
		} else {
			final long transferValue = transferRepository.getValue(transferringPlayerId);
			final long teamBudget = teamRepository.getBudget(userId);

			if (transferValue > teamBudget) {
				status = "400 BAD_REQUEST";
			} else {
				status = "200 OK";
			}
		}

		final ResultActions resultActions = mockMvc
			.perform(
				post(TRANSFER + TRANSFER_APPLY, transferringPlayerId)
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.header(AUTHORIZATION, "Bearer " + jwt));

		resultActions.andExpect(status().isOk());

		final MvcResult result = resultActions.andReturn();
		final JSONObject json = getObjectFromJson(result.getResponse().getContentAsString(), JSONObject.class);

		assertEquals(status, json.get("status"));

		if ("200 OK".equals(status)) {
			final Map<String, Object> playerMap = getObjectFromJsonBody(json, "player");

			assertEquals(String.valueOf(playerMap.get("id")), String.valueOf(transferringPlayerId));
			assertEquals(transferRepository.getCount(), transferReqCount - 1);
		}
	}

	@SuppressWarnings("unchecked")
	private void setupCommonJwt() throws Exception {
		if (jwt != null) {
			return;
		}

		if (userId == 0L) {
			userId = userRepository.getIdByUsername(TEST_USER_USERNAME);
		}

		if (transferringPlayerId == 0L) {
			transferringPlayerId = playerRepository.getAnyIdByTeam(userId);
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

	private void setupJwtForTransferApply() throws Exception {
		final ResultActions resultActions = mockMvc
			.perform(
				post(USER + USER_SIGNIN)
					.content(getStringFromObject(new UserDto(TEST_USER2_USERNAME, TEST_USER2_EMAIL, TEST_USER2_PASSWORD)))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON));

		resultActions.andExpect(status().isOk());

		final MvcResult result = resultActions.andReturn();
		final JSONObject json = getObjectFromJson(result.getResponse().getContentAsString(), JSONObject.class);

		jwt = getObjectFromJsonBody(json, "jwt");
	}
}
