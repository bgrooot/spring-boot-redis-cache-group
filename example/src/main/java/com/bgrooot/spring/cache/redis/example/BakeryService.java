package com.bgrooot.spring.cache.redis.example;

import com.bgrooot.spring.cache.redis.annotation.CacheGroupEvict;
import com.bgrooot.spring.cache.redis.annotation.CacheGroup;
import com.bgrooot.spring.cache.redis.annotation.CacheableGroup;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class BakeryService {

    private List<Bakery.Bread> breads = toShuffledList(Bakery.Bread.values());
    private List<Bakery.Cake> cakes = toShuffledList(Bakery.Cake.values());
    private List<Bakery.Sandwich> sandwich = toShuffledList(Bakery.Sandwich.values());

    @CacheGroup(cacheable = @CacheableGroup(value = "TodayBakeryMenu", key = "#day", cacheName = "TodayBread"))
    @Cacheable(value = "TodayBread", key = "#day")
    public String getTodayBread(int day) {
        System.out.println("GET BREAD");
        return breads.get(breads.size() % day).name();
    }

    @CacheGroup(cacheable = @CacheableGroup(value = "TodayBakeryMenu", key = "#day", cacheName = "TodayCake"))
    @Cacheable(value = "TodayCake", key = "#day")
    public String getTodayCake(int day) {
        System.out.println("GET CAKE");
        return cakes.get(cakes.size() % day).name();
    }

    @CacheGroup(cacheable = @CacheableGroup(value = "TodayBakeryMenu", key = "#day", cacheName = "TodaySandwich"))
    @Cacheable(value = "TodaySandwich", key = "#day")
    public String getTodaySandwich(int day) {
        System.out.println("GET SANDWICH");
        return sandwich.get(sandwich.size() % day).name();
    }

    @CacheGroup(evict = @CacheGroupEvict(value = "TodayBakeryMenu", key = "#day"))
    public void changeTodayMenuOrder(int day) {
        breads = toShuffledList(Bakery.Bread.values());
        cakes = toShuffledList(Bakery.Cake.values());
        sandwich = toShuffledList(Bakery.Sandwich.values());
    }

    private <T> List<T> toShuffledList(T[] t) {
        List<T> list = Arrays.asList(t);
        Collections.shuffle(list);
        return list;
    }
}