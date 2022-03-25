package com.soccermanager.service;

import com.soccermanager.domain.User;
import com.soccermanager.dto.UserDto;
import com.soccermanager.exception.CustomException;
import com.soccermanager.repository.CustomUserDetailsRepository;
import com.soccermanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.soccermanager.util.DateUtils.getCurrentTimestamp;
import static com.soccermanager.util.JwtUtils.generateJwt;
import static com.soccermanager.util.StringUtils.isNotEmpty;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * @author akif
 * @since 03/17/22
 */
@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CustomUserDetailsRepository userDetailsRepository;

	@Autowired
	private TeamService teamService;

	@Autowired
	private AuthenticationManager authManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MessageSourceAccessor msa;

	@Transactional
	public User save(final UserDto userDto) {
		final User user = new User(userDto.getUsername(), userDto.getEmail(), userDto.getPassword());
		user.setTeam(teamService.getPreparedInitialTeam(user));
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setEnabled(true);

		final long currentTimestamp = getCurrentTimestamp();
		user.setCreated(currentTimestamp);
		user.setUpdated(currentTimestamp);

		return userRepository.save(user);
	}

	public void checkUsernameEmailAvailability(final UserDto userDto) {
		if (isNotEmpty(userDto.getUsername()) && userRepository.isUsernameTaken(userDto.getUsername())) {
			throw new CustomException(BAD_REQUEST, msa.getMessage("error.user.username.taken"));
		}

		if (isNotEmpty(userDto.getEmail()) && userRepository.isEmailTaken(userDto.getEmail())) {
			throw new CustomException(BAD_REQUEST, msa.getMessage("error.user.email.taken"));
		}
	}

	public String getJwt(final String username, final String password) {
		final Authentication authentication;

		try {
			authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (Exception e) {
			throw new CustomException(FORBIDDEN, msa.getMessage("error.user.invalid.username.password"));
		}

		return generateJwt(userDetailsRepository.loadUserByUsername(username), authentication);
	}
}
