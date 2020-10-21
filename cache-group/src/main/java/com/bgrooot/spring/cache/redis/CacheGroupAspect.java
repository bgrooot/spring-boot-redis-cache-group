package com.bgrooot.spring.cache.redis;

import com.bgrooot.spring.cache.redis.annotation.CacheGroup;
import com.bgrooot.spring.cache.redis.service.CacheEvictGroupService;
import com.bgrooot.spring.cache.redis.service.CacheableGroupService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Order(value = Ordered.HIGHEST_PRECEDENCE + 1)
public class CacheGroupAspect {

    private final CacheableGroupService cacheableGroupService;
    private final CacheEvictGroupService cacheEvictGroupService;

    public CacheGroupAspect(RedisCacheManager redisCacheManager,
                            CacheGroupRedisCacheManager cacheGroupRedisCacheManager,
                            CacheProperties cacheProperties,
                            StringRedisTemplate stringRedisTemplate) {

        this.cacheableGroupService = new CacheableGroupService(redisCacheManager, cacheProperties, stringRedisTemplate, cacheGroupRedisCacheManager);
        this.cacheEvictGroupService = new CacheEvictGroupService(cacheProperties, stringRedisTemplate, cacheGroupRedisCacheManager);
    }

    @Pointcut("@annotation(com.bgrooot.spring.cache.redis.annotation.CacheGroup)")
    public void cacheGroupAnnotation() {}

    @After("cacheGroupAnnotation()")
    public void after(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        CacheGroup cacheGroup = method.getAnnotation(CacheGroup.class);

        try {
            cacheableGroupService.run(cacheGroup, method, joinPoint.getArgs());
            cacheEvictGroupService.run(cacheGroup, method, joinPoint.getArgs());

        } catch (Exception ex) {
            log.error("[CacheGroupAspect ERROR]", ex);
        }
    }
}