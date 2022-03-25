package com.soccermanager.service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.soccermanager.util.Constants.TRANSFER_LIST_CACHE;

/**
 * @author akif
 * @since 3/22/22
 */
@Service
public class CacheService {

	@Autowired
	private CacheManager cacheManager;

	public void resetTransferListCache() {
		final Cache cache = cacheManager.getCache(TRANSFER_LIST_CACHE);

		cache.removeAll();
	}
}
