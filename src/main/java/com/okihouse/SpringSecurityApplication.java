package com.okihouse;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootApplication
public class SpringSecurityApplication extends SpringBootServletInitializer {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityApplication.class, args);
    }

    @PostConstruct
    public void connection() {
        try {
            stringRedisTemplate.getConnectionFactory().getConnection();
        } catch (Exception e) {
            System.out.println("-------------------------------------------------------------------------------------------");
            System.out.println("- Redis host and port is not availables. please check application configuration file. -");
            System.out.println("-------------------------------------------------------------------------------------------");
            System.exit(-1);
        }
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(SpringSecurityApplication.class);
    }

}
