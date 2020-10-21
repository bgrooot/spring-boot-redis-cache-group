package com.bgrooot.spring.cache.redis.service;

import com.bgrooot.spring.cache.redis.CacheGroupRedisCacheManager;
import com.bgrooot.spring.cache.redis.annotation.CacheGroup;
import com.bgrooot.spring.cache.redis.annotation.CacheableGroup;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class CacheableGroupService extends AbstractCacheGroupService {

    private final RedisCacheManager redisCacheManager;

    public CacheableGroupService(RedisCacheManager redisCacheManager,
                                 CacheProperties cacheProperties,
                                 StringRedisTemplate stringRedisTemplate,
                                 CacheGroupRedisCacheManager cacheGroupRedisCacheManager) {

        super(cacheProperties, stringRedisTemplate, cacheGroupRedisCacheManager);
        this.redisCacheManager = redisCacheManager;
    }

    @Override
    public void run(CacheGroup cacheGroup, Method method, Object[] args) {
        CacheableGroup[] cacheableGroups = cacheGroup.cacheable();
        for (CacheableGroup cacheableGroup : cacheableGroups) {
            MethodBasedEvaluationContext methodBasedEvaluationContext = createMethodBasedEvaluationContext(method, args);
            String cacheGroupNameKey = getCacheName(methodBasedEvaluationContext, cacheableGroup.value(), cacheableGroup.key());

            Cacheable cacheable = getCacheableAnnotation(method, cacheableGroup.cacheName());
            String cacheableNameKey = getCacheName(methodBasedEvaluationContext, cacheable.value()[0], cacheable.key());
            long defaultTtlSecond = getCacheTtlSecond(cacheable.key());

            stringRedisTemplate.opsForSet().add(cacheGroupNameKey, cacheableNameKey);

            if (defaultTtlSecond > 0) {
                stringRedisTemplate.expire(cacheGroupNameKey, getCacheTtlSecond(cacheable.key()), TimeUnit.SECONDS);
            }
        }
    }

    private Cacheable getCacheableAnnotation(Method method, String cacheName) {
        return getCacheableAnnotationFromCaching(method, cacheName)
                .orElse(getCacheableAnnotationFromCacheable(method, cacheName).orElseThrow(NullPointerException::new));
    }

    private Optional<Cacheable> getCacheableAnnotationFromCaching(Method method, String cacheName) {
        try {
            Caching caching = method.getAnnotation(Caching.class);
            return Arrays.stream(caching.cacheable())
                    .filter(cacheable -> isIncludeCacheName(cacheable, cacheName))
                    .findFirst();

        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private Optional<Cacheable> getCacheableAnnotationFromCacheable(Method method, String cacheName) {
        try {
            Cacheable cacheable = method.getAnnotation(Cacheable.class);
            cacheable = isIncludeCacheName(cacheable, cacheName) ? cacheable : null;
            return Optional.ofNullable(cacheable);

        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private boolean isIncludeCacheName(Cacheable cacheable, String cacheName) {
        return Arrays.asList(cacheable.value()).contains(cacheName);
    }

    private long getCacheTtlSecond(String key) {
        long defaultTtlSecond = Optional.ofNullable(cacheProperties.getRedis().getTimeToLive())
                .map(Duration::getSeconds)
                .orElse(0L);

        RedisCache redisCache = (RedisCache) redisCacheManager.getCache(key);
        return Optional.ofNullable(redisCache)
                .map(cache -> cache.getCacheConfiguration().getTtl().getSeconds())
                .orElse(defaultTtlSecond);
    }
}