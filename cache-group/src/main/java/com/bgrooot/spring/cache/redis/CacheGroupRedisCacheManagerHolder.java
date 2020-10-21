package com.bgrooot.spring.cache.redis;

import lombok.Getter;

@Getter
public class CacheGroupRedisCacheManagerHolder {

    private final CacheGroupRedisCacheManager cacheGroupRedisCacheManager;

    public CacheGroupRedisCacheManagerHolder(CacheGroupRedisCacheManager cacheGroupRedisCacheManager) {
        this.cacheGroupRedisCacheManager = cacheGroupRedisCacheManager;
    }
}