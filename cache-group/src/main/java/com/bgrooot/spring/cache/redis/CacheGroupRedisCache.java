package com.bgrooot.spring.cache.redis;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;

public class CacheGroupRedisCache extends RedisCache {

    public CacheGroupRedisCache(String name, RedisCacheWriter redisCacheWriter, RedisCacheConfiguration redisCacheConfiguration) {
        super(name, redisCacheWriter, redisCacheConfiguration);
    }

    public String createCacheKey(Object key) {
        return super.createCacheKey(key);
    }
}