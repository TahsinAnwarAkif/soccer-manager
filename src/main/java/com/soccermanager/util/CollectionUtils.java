package com.soccermanager.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author akif
 * @since 3/17/22
 */
public class CollectionUtils {

	public static <T> List<T> nullSafeList(List<T> list) {
		return isNotEmpty(list) ? list : new ArrayList<>();
	}

	public static <T> boolean isNotEmpty(Collection<T> collection) {
		return !isEmpty(collection);
	}

	public static <T> boolean isEmpty(Collection<T> collection) {
		return collection == null || collection.size() == 0;
	}
}
