package com.example;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootApplication
public class SpringSecurityExerciseApplication {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityExerciseApplication.class, args);
	}

	@PostConstruct
	public void connection(){
		try {
			stringRedisTemplate.getConnectionFactory().getConnection();
		} catch (Exception e) {
			System.out.println("-------------------------------------------------------------------------------------------");
			System.out.println("- Redis host and port is not availables. please check out application configuration file. -");
			System.out.println("-------------------------------------------------------------------------------------------");
			System.exit(-1);
		}
	}

}
