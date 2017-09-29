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
