[![Build Status](https://travis-ci.org/okihouse/spring-boot-security-with-redis.svg?branch=master)](https://travis-ci.org/okihouse/spring-boot-security-with-redis)

Demo
=========

https://youtu.be/YwP1v1VYZes

[![Demo](http://i3.ytimg.com/vi/YwP1v1VYZes/maxresdefault.jpg)](https://youtu.be/YwP1v1VYZes "Demo")


Description
=========

If you start a new project, you will be concerned about security.

Typically, web projects often require you to create both a website and a restful apI at the same time.

This sample project satisfies both website security and restful apI security.

Take a look at it now.


보통 웹 프로젝트를 구성할 경우, Web Application 을 먼저 생성합니다. (웹 사이트 우선이라고 생각하면 편합니다)

웹 사이트 구성이 완료된 후, 안드로이드와 아이폰 클라이언트 작업을 시작할 때, 따로 Rest API 를 만들지 않고 

미리 생성한 Web Application 에서 Rest API 를 제공하는 것이 작업을 줄여주기 때문입니다.

위와같이 작업을 할 경우 보안작업이 나뉘어서 진행되게 됩니다.

웹 사이트의 경우 다음과 같은 기본 기능들이 필요합니다. (기본적인것만 나열해보겠습니다.)
1. Form Login or Ajax Login 
2. Remember-Me 
3. Session Management
4. Authentication with role(authority)
5. CSRF Protection

Restful API 의 경우 다음의 기능들이 필요합니다. (기본적인것만 나열해보겠습니다.)
1. Token Authentication 
2. CORS
3. Authentication with role(authority)

둘다 공통으로 사용하는 것도 있지만 가장 중요한 인증방식은 조금씩 다릅니다.

그러므로, 둘 다 기능에 맞게 구현해 줘야 합니다. 

해당 프로젝트는 가장 기본적인 두 기능을 모두 제공합니다.


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
