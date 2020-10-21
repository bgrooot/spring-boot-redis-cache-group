package com.bgrooot.spring.cache.redis.service;

import com.bgrooot.spring.cache.redis.CacheGroupRedisCacheManager;
import com.bgrooot.spring.cache.redis.annotation.CacheGroupEvict;
import com.bgrooot.spring.cache.redis.annotation.CacheGroup;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class CacheEvictGroupService extends AbstractCacheGroupService {

    public CacheEvictGroupService(CacheProperties cacheProperties,
                                  StringRedisTemplate stringRedisTemplate,
                                  CacheGroupRedisCacheManager cacheGroupRedisCacheManager) {

        super(cacheProperties, stringRedisTemplate, cacheGroupRedisCacheManager);
    }

    @Override
    public void run(CacheGroup cacheGroup, Method method, Object[] args) {
        CacheGroupEvict[] cacheGroupEvicts = cacheGroup.evict();
        for (CacheGroupEvict cacheGroupEvict : cacheGroupEvicts) {
            MethodBasedEvaluationContext methodBasedEvaluationContext = createMethodBasedEvaluationContext(method, args);
            String cacheGroupNameKey = getCacheName(methodBasedEvaluationContext, cacheGroupEvict.value(), cacheGroupEvict.key());

            Set<String> memberKeySet = stringRedisTemplate.opsForSet().members(cacheGroupNameKey);
            memberKeySet = Optional.ofNullable(memberKeySet).orElse(Collections.emptySet());
            if (!memberKeySet.isEmpty()) {
                stringRedisTemplate.opsForSet().remove(cacheGroupNameKey, memberKeySet.toArray());
                stringRedisTemplate.delete(memberKeySet);
            }
        }
    }
}