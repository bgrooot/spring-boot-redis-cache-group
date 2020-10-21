package com.bgrooot.spring.cache.redis;

import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@ComponentScan
@EnableAspectJAutoProxy
@ConditionalOnClass(RedisOperations.class)
public class CacheGroupConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CacheGroupRedisCacheManagerHolder cacheGroupRedisCacheManagerHolder(RedisConnectionFactory redisConnectionFactory) {
        CacheGroupRedisCacheManager cacheGroupRedisCacheManager = new CacheGroupRedisCacheManager(
                RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),
                RedisCacheConfiguration.defaultCacheConfig());

        return new CacheGroupRedisCacheManagerHolder(cacheGroupRedisCacheManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheGroupAspect CacheGroupCacheGroupAspect(RedisCacheManager cacheManager,
                                                       CacheGroupRedisCacheManagerHolder CacheGroupRedisCacheManagerHolder,
                                                       CacheProperties cacheProperties,
                                                       StringRedisTemplate stringRedisTemplate) {

        return new CacheGroupAspect(
                cacheManager,
                CacheGroupRedisCacheManagerHolder.getCacheGroupRedisCacheManager(),
                cacheProperties,
                stringRedisTemplate);
    }
}