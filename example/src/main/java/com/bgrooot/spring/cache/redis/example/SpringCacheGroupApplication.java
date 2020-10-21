package com.bgrooot.spring.cache.redis.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class SpringCacheGroupApplication implements CommandLineRunner {

    private final BakeryService bakeryService;

    public SpringCacheGroupApplication(BakeryService bakeryService) {
        this.bakeryService = bakeryService;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringCacheGroupApplication.class, args);
    }

    @Override
    public void run(String...args) {
        int day = 1;
        System.out.println(bakeryService.getTodayBread(day));
        System.out.println(bakeryService.getTodayCake(day));
        System.out.println(bakeryService.getTodaySandwich(day));
        System.out.println();

        System.out.println(bakeryService.getTodayBread(day));
        System.out.println(bakeryService.getTodayCake(day));
        System.out.println(bakeryService.getTodaySandwich(day));
        System.out.println();

        bakeryService.changeTodayMenuOrder(1);

        System.out.println(bakeryService.getTodayBread(day));
        System.out.println(bakeryService.getTodayCake(day));
        System.out.println(bakeryService.getTodaySandwich(day));
    }
}