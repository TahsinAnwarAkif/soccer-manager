package com.soccermanager.util;


import java.util.Arrays;

/**
 * @author akif
 * @since 3/17/22
 */
public class StringUtils {

	public static String getInitCappedStr(String str) {
		if (isEmpty(str)) {
			return str;
		}

		return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
	}

	public static boolean isNotEmpty(String... strs) {
		if (strs.length == 0) {
			throw new IllegalArgumentException("Invalid Param!");
		}

		return Arrays.stream(strs).noneMatch(StringUtils::isEmpty);
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}
}
