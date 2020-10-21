package com.bgrooot.spring.cache.redis.annotation;

public @interface CacheGroupEvict {

    String value();
    String key();
}