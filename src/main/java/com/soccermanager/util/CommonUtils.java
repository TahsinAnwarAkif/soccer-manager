package com.soccermanager.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soccermanager.domain.Country;
import org.springframework.validation.Errors;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.soccermanager.util.StringUtils.getInitCappedStr;

/**
 * @author akif
 * @since 3/17/22
 */
public class CommonUtils {

	private static final ObjectMapper OBJECT_MAPPER;
	private static final Random RANDOM_GEN;

	static {
		OBJECT_MAPPER = new ObjectMapper();
		RANDOM_GEN = new Random();
	}

	public static HttpServletRequest getRequest() {
		return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
			.getRequest();
	}

	public static String getValidCountryListStr() {
		return Arrays.stream(Country.values())
			.map(c -> getInitCappedStr(c.name()))
			.collect(Collectors.joining(", "));
	}

	public static Map<String, Object> getFieldErrorMsgMap(final Errors errors) {
		final Map<String, Object> fieldErrorMsgMap = new LinkedHashMap<>();

		errors.getFieldErrors()
			.forEach(error ->
				fieldErrorMsgMap.put(error.getField(), error.getDefaultMessage()));

		return fieldErrorMsgMap;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getObjectFromJsonBody(JSONObject json, String key) {
		return (T) ((Map<String, Object>) json.get("body")).get(key);
	}

	public static <T> T getObjectFromJson(final String json, final Class<T> tClass) throws JsonProcessingException {
		return OBJECT_MAPPER.readValue(json, tClass);
	}

	public static <T> String getStringFromObject(final T object) throws JsonProcessingException {
		return OBJECT_MAPPER.writeValueAsString(object);
	}

	public static Country getRandomCountry() {
		return Arrays.asList(Country.values()).get(RANDOM_GEN.nextInt(Country.values().length));
	}

	public static int getRandomNumberBetweenRange(int min, int max) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}
}
