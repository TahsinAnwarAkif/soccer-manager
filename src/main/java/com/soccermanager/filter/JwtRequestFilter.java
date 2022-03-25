package com.soccermanager.filter;

import com.soccermanager.exception.CustomException;
import com.soccermanager.util.CollectionUtils;
import com.soccermanager.util.JSONObject;
import com.soccermanager.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.soccermanager.util.CommonUtils.getStringFromObject;
import static com.soccermanager.util.JSONObject.create;
import static com.soccermanager.util.JwtUtils.isValidJwt;
import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * @author akif
 * @since 3/17/22
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private MessageSourceAccessor msa;

	@Override
	protected void doFilterInternal(final HttpServletRequest request,
									final HttpServletResponse response,
									final FilterChain filterChain) throws ServletException, IOException {

		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);

			return;
		}

		try {
			final String jwt = authHeader.substring(7);
			final String username = JwtUtils.extractUsername(jwt);

			final List<Map<String, String>> authorityList = JwtUtils.getAuthorityList(jwt);
			final Set<SimpleGrantedAuthority> simpleGrantedAuthorityList = CollectionUtils.nullSafeList(authorityList)
				.stream()
				.map(m -> new SimpleGrantedAuthority(m.get("authority")))
				.collect(Collectors.toSet());

			if (isValidJwt(jwt)) {
				final UsernamePasswordAuthenticationToken usernamePasswordAuthToken = new UsernamePasswordAuthenticationToken(username,
					null, simpleGrantedAuthorityList);
				usernamePasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthToken);
			} else {
				throw new CustomException(FORBIDDEN, msa.getMessage("error.insufficient.privilege"));
			}
		} catch (Exception e) {
			final JSONObject json = create()
				.status(FORBIDDEN)
				.currentTimestamp()
				.body("error", msa.getMessage("error.insufficient.privilege"));

			response.setContentType("application/json");
			response.getWriter().write(getStringFromObject(json));

			return;
		}

		filterChain.doFilter(request, response);
	}
}
