package com.soccermanager.controller;

import com.soccermanager.dto.UserDto;
import com.soccermanager.repository.UserRepository;
import com.soccermanager.util.JSONObject;
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

import java.util.Map;

import static com.soccermanager.util.CommonUtils.*;
import static com.soccermanager.util.JwtUtils.extractUsername;
import static com.soccermanager.util.TestConstants.*;
import static com.soccermanager.util.Url.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author akif
 * @since 3/21/22
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Order(1)
@TestMethodOrder(OrderAnnotation.class)
public class UserControllerTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@Order(1)
	@SuppressWarnings("unchecked")
	public void signup() throws Exception {
		final String status;

		if (userRepository.isUsernameOrEmailTaken(TEST_USER_USERNAME, TEST_USER_EMAIL)) {
			status = "400 BAD_REQUEST";
		} else {
			status = "200 OK";
		}

		final ResultActions resultActions = this.mockMvc
			.perform(
				post(USER + USER_SIGNUP)
					.content(getStringFromObject(new UserDto(TEST_USER_USERNAME, TEST_USER_EMAIL, TEST_USER_PASSWORD)))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON));

		resultActions.andExpect(status().isOk());

		final MvcResult result = resultActions.andReturn();
		final JSONObject json = getObjectFromJson(result.getResponse().getContentAsString(), JSONObject.class);

		assertEquals(status, json.get("status"));

		if ("200 OK".equals(status)) {
			final Map<String, Object> userMap = getObjectFromJsonBody(json, "user");
			final long userId = userRepository.getIdByUsername(TEST_USER_USERNAME);

			assertEquals(String.valueOf(userMap.get("id")), String.valueOf(userId));
			assertEquals(userMap.get("username"), TEST_USER_USERNAME);
			assertEquals(userMap.get("email"), TEST_USER_EMAIL);
		}
	}

	@Test
	@Order(2)
	@SuppressWarnings("unchecked")
	public void signin() throws Exception {
		final ResultActions resultActions = this.mockMvc
			.perform(
				post(USER + USER_SIGNIN)
					.content(getStringFromObject(new UserDto(TEST_USER_USERNAME, TEST_USER_PASSWORD)))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON));

		resultActions.andExpect(status().isOk());

		final MvcResult result = resultActions.andReturn();
		final JSONObject json = getObjectFromJson(result.getResponse().getContentAsString(), JSONObject.class);
		final String jwt = getObjectFromJsonBody(json, "jwt");

		assertEquals(TEST_USER_USERNAME, extractUsername(jwt));
	}
}
