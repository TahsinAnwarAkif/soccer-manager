package com.soccermanager.validator;

import com.soccermanager.domain.Country;
import com.soccermanager.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import static com.soccermanager.util.CommonUtils.getValidCountryListStr;

/**
 * @author akif
 * @since 3/18/22
 */
@Component
public class CountryValidator {

	@Autowired
	private MessageSourceAccessor msa;

	public void validate(final String countryStr, final Errors errors) {
		if (StringUtils.isEmpty(countryStr)) {
			return;
		}

		Country country;

		try {
			country = Country.valueOf(countryStr.toUpperCase());
		} catch (IllegalArgumentException e) {
			country = null;
		}

		if (country == null) {
			final String errorMsg = msa.getMessage("error.team.invalid.country", new String[]{getValidCountryListStr()});

			if (errors != null) {
				errors.rejectValue("country", errorMsg, errorMsg);
			}
		}
	}
}
