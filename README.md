[![Build Status](https://travis-ci.org/okihouse/spring-boot-security-with-redis.svg?branch=master)](https://travis-ci.org/okihouse/spring-boot-security-with-redis)

Description
=========

Spring Security is a great solution.
But it's very tricky to deal with it.

Especially, it is more difficult for issues such as user sessions handling.

This project has implemented Remember-Me implementation provided by Spring Security.
(Both with form login and ajax login)

Also used `Redis` to implement custom persist token management.

The interesting thing is that even if you restart the `WAS(like as tomcat)`,
the user information is saved and is not affected by the login.

스프링 부트 + 스프링 시큐리티로 구현한 Form 로그인 및 Ajax 로그인 입니다.
이 예제의 특이한 점은 스프링 시큐리티가 제공하는 remember-me 를 구현하였으며,
해당 기능으로 tomcat과 같은 was가 재기동(재시작) 해도 로그인 정보가 유지된다는 점입니다

Requirements
=====

* Java 1.8
* [Spring boot](http://projects.spring.io/spring-boot/) 1.2.8+ (spring-boot-starter-redis)
* [Redis](http://redis.io/) 2.4+

Watch Out!
=====

* Update host and port in `application.yml` for `Redis`.

Run
===

```bash
mvn spring-boot:run
```
