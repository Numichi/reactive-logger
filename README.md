# reactive-logger 
[![CircleCI](https://circleci.com/gh/Numichi/reactive-logger/tree/develop.svg?style=shield)](https://circleci.com/gh/Numichi/reactive-logger/tree/develop)
[![Maintainability](https://api.codeclimate.com/v1/badges/3b8d1ff3b57491648f7d/maintainability)](https://codeclimate.com/github/Numichi/reactive-logger/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/3b8d1ff3b57491648f7d/test_coverage)](https://codeclimate.com/github/Numichi/reactive-logger/test_coverage)
[![Apache-2.0](https://img.shields.io/badge/license-Apache--2.0-blue)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.numichi/reactive-logger.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.numichi%22%20AND%20a:%22reactive-logger%22)
![Tested on](https://img.shields.io/badge/tested%20on-jvm8-blue)
[![Reactor Project](https://img.shields.io/badge/supported-Java%20and%20Kotlin%20Coroutine%20with%20Reactor-blue)](https://projectreactor.io/)

_After releases, SonaType or mvnrepostiroy may not appear. Regardless, the package is available._

## Important
This is just a hobby project driven by my own personal motivation. There is no external sponsor behind the project.

## What is the source of motivation?
I think this description approaches the [What Is a Good Pattern for Contextual Logging? (MDC)](https://projectreactor.io/docs/core/release/reference/#faq.mdc) well. Furthermore, there is the so-called [lifting solution](https://www.novatec-gmbh.de/en/blog/how-can-the-mdc-context-be-used-in-the-reactive-spring-applications/), which I think is overkill. In this (lifting) example, he uses Kotlin, but with a Reactor API, not a coroutine. If you stay with Reactor API, there is no problem with lifting and it work. I mean, I didn't experience any pain, regardless of I used Kotlin or Java. But it is not valid for Kotlin Coroutine.

**My first problem with the lifting solution.**
It runs a hook for each reactor API. It generates a lot of unnecessary events and class creation. I have [tested](https://github.com/Numichi/reactive-java-mdc-investigation) it with a minimal controller with Spring Boot (see: GET /counter). MDC map copy had run about 129 times for only one request. Then let's count how many times will run on one of the more complex applications?

**My second problem on Kotlin Coroutine with lifting.**
It does not work. When you call a Reactor API, API will activate hook, and MDC ThreadLocal will be overridden. After it, coroutine gets run point back. Hooks are not taken effect in coroutine areas. If you would like to run a logger with slf4j in the coroutine area, according to slf4j, the MDC is empty, but CoroutineContext is not.

**Another side effect I have experienced.**
When I created a parallel request, my first request ended later than my second request. I have used a suspended delay in code, and I noticed the following: MDC context data slipping to another request. I think it is unhealthy and misinforms when you review logs.

**Goal:**
So, I have been working to create an API for Reactor and Coroutine what solve the above problems. It provides the same interfaces to both environments and follows Reactor MDC documentation.

## Overview
_Part of the documentation and description comes from: [README.md](https://github.com/johncfranco/reactive-logger/blob/develop/README.md)_

`reactive-logger` is a Java and Kotlin library adapting the `slf4j` logging library for reactive applications.
It treats the various message-writing methods in the slf4j `Logger` interface as blocking I/O by wrapping each to the reactive environment that invokes the method on a scheduler appropriate for blocking I/O.

The library has many goals:
* Provide reactive code with a familiar and natural logging interface analogous to the slf4j `Logger` interface in imperative code.
* Facilitate a novel approach to the three-way trade-off between reliability, performance, and resource consumption that application logs commonly face.
* Obey the rule restricting blocking I/O to bounded elastic schedulers without requiring a specific logging configuration to do so.
* Provide the appropriate language approach for Java Reactor or Kotlin Coroutine code.

## Documentation
- [Wiki](https://github.com/Numichi/reactive-logger/wiki/Documentation)
- [Kotlin example](https://github.com/Numichi/reactive-logger-kotlin-example/tree/main/src/main/kotlin/io/github/numichi/reactive/logger/kotlin/example)

## Dependency

### Maven
```xml
<dependency>
  <groupId>io.github.numichi</groupId>
  <artifactId>reactive-logger</artifactId>
  <version>VERSION</version>
</dependency>
```

### Gradle Groovy DSL
```gradle
implementation 'io.github.numichi:reactive-logger:VERSION'
```

### Gradle Kotlin DSL
```gradle
implementation("io.github.numichi:reactive-logger:VERSION")
```
