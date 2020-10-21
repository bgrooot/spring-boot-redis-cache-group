# Spring Boot Redis Cache Group
This is a framework for grouping and deleting caches running in Spring Boot and Redis.

## Features
- Read spring cache annotation.
- Use SpEL as the cache group key.
- This is useful when you need to clear the cache and it is difficult to know which cache key is cached. (Like paging a list)

## How To Use
- [Example Project](/example)
```java
public class BakeryService {

    //Add To Cache Group
    @CacheGroup(cacheable = @CacheableGroup(value = "TodayBakeryMenu", key = "#day", cacheName = "TodayBread"))
    @Cacheable(value = "TodayBread", key = "#day")
    public String getTodayBread(int day) {
        System.out.println("GET BREAD");
        return breads.get(breads.size() % day).name();
    }

    //Evict Cache Group
    @CacheGroup(evict = @CacheGroupEvict(value = "TodayBakeryMenu", key = "#day"))
    public void changeTodayMenuOrder(int day) {
        breads = toShuffledList(Bakery.Bread.values());
        cakes = toShuffledList(Bakery.Cake.values());
        sandwich = toShuffledList(Bakery.Sandwich.values());
    }
}
```

## How It Works
- Read the key of the `@Cacheble` annotation set in the `cacheName` attribute of `@CahebleGroup` and add it to the Redis SET.
- Set the TTL of the `@Cacheble` annotation in cache group.
- If that doesn't exist, it sets the default TTL of Spring Redis.
- If there is no default TTL, doesn't expire.