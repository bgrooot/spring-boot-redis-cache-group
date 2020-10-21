package com.bgrooot.spring.cache.redis.service;

import com.bgrooot.spring.cache.redis.CacheGroupRedisCacheManager;
import com.bgrooot.spring.cache.redis.annotation.CacheGroup;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;

public abstract class AbstractCacheGroupService {

    protected CacheProperties cacheProperties;
    protected StringRedisTemplate stringRedisTemplate;
    protected CacheGroupRedisCacheManager cacheGroupRedisCacheManager;
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public AbstractCacheGroupService(CacheProperties cacheProperties,
                                     StringRedisTemplate stringRedisTemplate,
                                     CacheGroupRedisCacheManager cacheGroupRedisCacheManager) {

        this.cacheProperties = cacheProperties;
        this.stringRedisTemplate = stringRedisTemplate;
        this.cacheGroupRedisCacheManager = cacheGroupRedisCacheManager;
    }

    abstract public void run(CacheGroup cacheGroup, Method method, Object[] args);

    protected MethodBasedEvaluationContext createMethodBasedEvaluationContext(Method method, Object[] args) {
        return new MethodBasedEvaluationContext(
                null,
                method,
                args,
                parameterNameDiscoverer);
    }

    protected String getCacheName(MethodBasedEvaluationContext methodBasedEvaluationContext, String name, String key) {
        Expression expression = expressionParser.parseExpression(key);
        Object cacheGroupKey = expression.getValue(methodBasedEvaluationContext);
        return cacheGroupRedisCacheManager.getCacheKey(name, cacheGroupKey);
    }
}