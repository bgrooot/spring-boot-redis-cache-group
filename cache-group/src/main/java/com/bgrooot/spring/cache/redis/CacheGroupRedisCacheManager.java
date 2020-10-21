package com.bgrooot.spring.cache.redis;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.util.Optional;

public class CacheGroupRedisCacheManager extends RedisCacheManager {

    private final RedisCacheWriter redisCacheWriter;
    private final RedisCacheConfiguration redisCacheConfiguration;

    public CacheGroupRedisCacheManager(RedisCacheWriter cacheWriter,
                                       RedisCacheConfiguration redisCacheConfiguration) {

        super(cacheWriter, redisCacheConfiguration);
        this.redisCacheWriter = cacheWriter;
        this.redisCacheConfiguration = redisCacheConfiguration;
    }

    public String getCacheKey(String name, Object key) {
        CacheGroupRedisCache cacheGroupRedisCache = (CacheGroupRedisCache) super.getCache(name);
        return Optional.ofNullable(cacheGroupRedisCache)
                .map(cache -> cache.createCacheKey(key))
                .orElseThrow(NullPointerException::new);
    }

    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        return new CacheGroupRedisCache(name, redisCacheWriter, cacheConfig != null ? cacheConfig : redisCacheConfiguration);
    }
}