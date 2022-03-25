package com.soccermanager.util;

import java.util.Date;

/**
 * @author akif
 * @since 3/17/22
 */
public class DateUtils {

	public static long getCurrentTimestamp() {
		return getCurrentDate().getTime() / 1000;
	}

	public static Date getCurrentDate() {
		return new Date();
	}
}
