# Reactive-Logger

[![CircleCI](https://circleci.com/gh/Numichi/reactive-logger/tree/master.svg?style=shield)](https://circleci.com/gh/Numichi/reactive-logger/tree/develop)
[![Test Coverage](https://api.codeclimate.com/v1/badges/74b86e2f051a0c3c9e11/test_coverage)](https://codeclimate.com/github/Numichi/reactive-logger/test_coverage)
[![Apache-2.0](https://img.shields.io/badge/license-Apache--2.0-blue)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.numichi/reactive-logger.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.numichi%22%20AND%20a:%22reactive-logger%22)
![Tested on](https://img.shields.io/badge/tested%20on-jvm11-blue)
[![Reactor Project](https://img.shields.io/badge/supported-Java%20and%20Kotlin%20Coroutine%20with%20Reactor-blue)](https://projectreactor.io/)


## What is the source of motivation?

I think this description approaches
the [What Is a Good Pattern for Contextual Logging? (MDC)](https://projectreactor.io/docs/core/release/reference/#faq.mdc) well.
Furthermore, there is the
so-called [lifting solution](https://www.novatec-gmbh.de/en/blog/how-can-the-mdc-context-be-used-in-the-reactive-spring-applications/),
which I think is overkill. In this (lifting) example, he/she uses Kotlin, but with a Reactor API, not a coroutine. If you stay with Reactor API, there is no problem with lifting and it work. I didn't
experience any problem, regardless of I used Kotlin or Java. But it is not valid for Kotlin Coroutine.

**My first problem with the lifting solution.**
It runs a hook for each reactor API. It generates a lot of unnecessary events and class creation. I
have tested it with a minimal controller with Spring Boot.
MDC map copy had run about 129 times for only one request. Then let's count how many times will run on one of the more complex applications?

**My second problem on Kotlin Coroutine with lifting.**
It does not work. When you call a Reactor API, API will activate hook, and MDC ThreadLocal will be overridden. After it, coroutine scope gets regain control. Hooks are not taken effect in coroutine
areas. If you would like to run a logger with slf4j in the coroutine area, according to slf4j, the MDC is empty, but CoroutineContext is
not. [[See example](https://github.com/Numichi/reactive-logger-my-problem/blob/main/src/main/kotlin/com/example/demo/controller/MdcController.kt)]

**Another side effect I have experienced.**
When I created a parallel request, my first request ended later than my second request. I have used a suspended delay in code, and I noticed
the following: MDC context data slipping to another request. I think it is unhealthy and misinforms when you review logs.

**Goal:**
So, I have been working to create an API for Reactor and Coroutine what solve the above problems. It provides the same interfaces to both
environments and follows [Reactor MDC FAQ](https://projectreactor.io/docs/core/release/reference/#faq.mdc).

## Overview

_Part of the documentation and description comes from: [README.md](https://github.com/johncfranco/reactive-logger/blob/develop/README.md)_

`reactive-logger` is a Java and Kotlin library adapting the `slf4j` logging library for reactive applications. It treats the various
message-writing methods in the slf4j `Logger` interface as blocking I/O by wrapping each to the reactive environment that invokes the method
on a scheduler appropriate for blocking I/O.

The library has many goals:

* Provide reactive code with a familiar and natural logging interface analogous to the slf4j `Logger` interface in imperative code.
* Facilitate a novel approach to the three-way trade-off between reliability, performance, and resource consumption that application logs
  commonly face.
* Obey the rule restricting blocking I/O to bounded elastic schedulers without requiring a specific logging configuration to do so.
* Provide the appropriate language approach for Java Reactor or Kotlin Coroutine code.

## Get Started

### Dependency

#### Maven

```xml

<dependency>
  <groupId>io.github.numichi</groupId>
  <artifactId>reactive-logger</artifactId>
  <version>VERSION</version>
</dependency>
```

#### Gradle Groovy DSL

```groovy
implementation 'io.github.numichi:reactive-logger:VERSION'
```

#### Gradle Kotlin DSL

```kotlin
implementation("io.github.numichi:reactive-logger:VERSION")
```


### Logger library (optinal)

Because it is a layer on slf4j so you can use specific logger libraries like Backlog, Log4j2 and so on. By default, Spring uses the Backlog log library. So, if you don't would like to apply anything
else, you don't need additional dependencies.

#### Log4j2 configuration

When using Log4j2 you should 2 things. You have to deactivate Logback and import Log4j2 dependency. Configure log4j2.xml as required. But it is already
a [configuration](https://logging.apache.org/log4j/2.x/manual/configuration.html) of the logger library.

Many documentation can be found on the internet ([here](https://www.callicoder.com/spring-boot-log4j-2-example/) and [here](https://www.baeldung.com/spring-boot-logging)) on how can you change from
Logback to Log4j2. The articles mainly use XML configuration, so I presented an example with Gradle Kotlin DSL configuration.

```kotlin
// Gradle Kotlin DSL
configurations {
    all {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
}
```

# How use `reactive-logger`
Incoming **v4.0.0**. Along with this, the description will also be rewritten. Until then, read the README of the previous commits.