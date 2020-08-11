[![Build Status](https://travis-ci.org/okihouse/spring-boot-security-with-redis-jwt-for-restapi-webapp.svg?branch=master)](https://travis-ci.org/okihouse/spring-boot-security-with-redis)

Demo
=========

https://youtu.be/YwP1v1VYZes

[![Demo](http://i3.ytimg.com/vi/YwP1v1VYZes/maxresdefault.jpg)](https://youtu.be/YwP1v1VYZes "Demo")


Description
=========

Web Application Authentication
1. Form Login or Ajax Login 
2. Remember-Me 
3. Session Management
4. Authentication with role(authority)
5. CSRF Protection

Restful API Authentication
1. Token Authentication 
2. CORS
3. Authentication with role(authority)

Requirements
=====

* Java 1.8
* [Spring boot](http://projects.spring.io/spring-boot/) 1.2.8+ (spring-boot-starter-redis)
* [Redis](http://redis.io/) 2.4+
* lombok

Watch Out!
=====

* Update host and port in `application.yml` for `Redis`.

Run
===

```bash
mvn spring-boot:run
```
