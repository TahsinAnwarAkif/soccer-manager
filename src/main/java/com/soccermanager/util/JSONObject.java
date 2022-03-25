package com.soccermanager.util;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.soccermanager.util.DateUtils.getCurrentTimestamp;

/**
 * @author akif
 * @since 3/17/22
 */
@NoArgsConstructor
public class JSONObject extends LinkedHashMap<String, Object> implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String, Object> body;

	public static JSONObject create() {
		return new JSONObject();
	}

	public JSONObject status(HttpStatus status) {
		this.put("status", status.toString());

		return this;
	}

	public JSONObject body(String key, Object value) {
		if (this.body == null || this.body.isEmpty()) {
			this.body = new LinkedHashMap<>();

			this.put("body", this.body);
		}

		this.body.put(key, value);

		return this;
	}

	public JSONObject currentTimestamp() {
		this.put("timestamp", getCurrentTimestamp());

		return this;
	}
}
