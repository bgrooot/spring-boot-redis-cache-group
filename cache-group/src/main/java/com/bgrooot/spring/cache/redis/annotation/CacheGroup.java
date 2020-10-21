package com.bgrooot.spring.cache.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CacheGroup {

    CacheableGroup[] cacheable() default {};
    CacheGroupEvict[] evict() default {};
}