package com.soccermanager.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.soccermanager.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.soccermanager.util.CommonUtils.getRequest;
import static com.soccermanager.util.DateUtils.getCurrentDate;
import static org.apache.commons.lang3.time.DateUtils.addMinutes;

/**
 * @author akif
 * @since 3/18/22
 */
public class JwtUtils {

	private static final String SECRET_KEY = "secret";

	public static String generateJwt(final UserDetails userDetails, final Authentication authentication) {
		final Map<String, Object> claimMap = new HashMap<>();
		claimMap.put("principal", authentication.getPrincipal());

		return createJwt(claimMap, userDetails.getUsername());
	}

	public static Boolean isValidJwt(final String jwt) {
		final String username = extractUsername(jwt);

		return (username.equals(extractUsername(jwt)) && !isJwtExpired(jwt));
	}

	public static String getJwtFromRequest() {
		return getRequest().getHeader(HttpHeaders.AUTHORIZATION).substring(7);
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, String>> getAuthorityList(final String jwt) {
		return (List<Map<String, String>>)
			((Map<String, Object>) extractAllClaims(jwt).get("principal")).get("authorities");
	}

	public static long getLoggedInUserId() throws JsonProcessingException {
		return getLoggedInUser().getId();
	}

	public static String extractUsername(final String jwt) {
		return extractClaim(jwt, Claims::getSubject);
	}

	public static Date extractExpiration(final String jwt) {
		return extractClaim(jwt, Claims::getExpiration);
	}

	public static <T> T extractClaim(final String jwt, final Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(jwt);

		return claimsResolver.apply(claims);
	}

	@SuppressWarnings("unchecked")
	private static User getLoggedInUser() throws JsonProcessingException {
		final String jwt = getJwtFromRequest();
		final String json = CommonUtils.getStringFromObject((Map<String, Object>) extractAllClaims(jwt).get("principal"));

		return CommonUtils.getObjectFromJson(json, User.class);
	}

	private static String createJwt(final Map<String, Object> claims, final String username) {
		final Date currentDate = getCurrentDate();

		return Jwts.builder()
			.setClaims(claims)
			.setSubject(username)
			.setIssuedAt(currentDate)
			.setExpiration(addMinutes(currentDate, 15))
			.signWith(SignatureAlgorithm.HS256, SECRET_KEY)
			.compact();
	}

	private static Claims extractAllClaims(final String jwt) {
		return Jwts.parser()
			.setSigningKey(SECRET_KEY)
			.parseClaimsJws(jwt)
			.getBody();
	}

	private static Boolean isJwtExpired(final String jwt) {
		return extractExpiration(jwt).before(getCurrentDate());
	}
}
