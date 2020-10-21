package com.bgrooot.spring.cache.redis.annotation;

public @interface CacheableGroup {

    String value();
    String key();
    String cacheName();
}