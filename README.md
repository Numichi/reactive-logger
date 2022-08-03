# reactive-logger

[![CircleCI](https://circleci.com/gh/Numichi/reactive-logger/tree/develop.svg?style=shield)](https://circleci.com/gh/Numichi/reactive-logger/tree/develop)
[![Maintainability](https://api.codeclimate.com/v1/badges/3b8d1ff3b57491648f7d/maintainability)](https://codeclimate.com/github/Numichi/reactive-logger/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/3b8d1ff3b57491648f7d/test_coverage)](https://codeclimate.com/github/Numichi/reactive-logger/test_coverage)
[![Apache-2.0](https://img.shields.io/badge/license-Apache--2.0-blue)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.numichi/reactive-logger.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.numichi%22%20AND%20a:%22reactive-logger%22)
![Tested on](https://img.shields.io/badge/tested%20on-jvm8-blue)
[![Reactor Project](https://img.shields.io/badge/supported-Java%20and%20Kotlin%20Coroutine%20with%20Reactor-blue)](https://projectreactor.io/)

_After releases, SonaType or mvnrepostiroy may not appear. Regardless, the package is available._

# Important

- This is just a hobby project driven by self motivation. There is no external sponsor behind the project.
- I use this library in my projects, so I do it if I find a bug or extension. If you find a bug or you have a new feature idea, please open an issue.

# What is the source of motivation?

I think this description approaches
the [What Is a Good Pattern for Contextual Logging? (MDC)](https://projectreactor.io/docs/core/release/reference/#faq.mdc) well.
Furthermore, there is the
so-called [lifting solution](https://www.novatec-gmbh.de/en/blog/how-can-the-mdc-context-be-used-in-the-reactive-spring-applications/),
which I think is overkill. In this (lifting) example, he uses Kotlin, but with a Reactor API, not a coroutine. If you stay with Reactor API,
there is no problem with lifting and it work. I mean, I didn't experience any pain, regardless of I used Kotlin or Java. But it is not valid
for Kotlin Coroutine.

**My first problem with the lifting solution.**
It runs a hook for each reactor API. It generates a lot of unnecessary events and class creation. I
have [tested](https://github.com/Numichi/reactive-logger-my-problem/blob/main/src/main/kotlin/com/example/demo/controller/MdcController.kt) it with a minimal controller with Spring Boot.
MDC map copy had run about 129 times for only one request. Then let's count how many times will run on one of the more complex applications?

**My second problem on Kotlin Coroutine with lifting.**
It does not work. When you call a Reactor API, API will activate hook, and MDC ThreadLocal will be overridden. After it, coroutine gets run
point back. Hooks are not taken effect in coroutine areas. If you would like to run a logger with slf4j in the coroutine area, according to
slf4j, the MDC is empty, but CoroutineContext is not.

**Another side effect I have experienced.**
When I created a parallel request, my first request ended later than my second request. I have used a suspended delay in code, and I noticed
the following: MDC context data slipping to another request. I think it is unhealthy and misinforms when you review logs.

**Goal:**
So, I have been working to create an API for Reactor and Coroutine what solve the above problems. It provides the same interfaces to both
environments and follows Reactor MDC documentation.

# Overview

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

# Dependency

### Maven

```xml

<dependency>
  <groupId>io.github.numichi</groupId>
  <artifactId>reactive-logger</artifactId>
  <version>2.1.1</version>
</dependency>
```

### Gradle Groovy DSL

```groovy
implementation 'io.github.numichi:reactive-logger:2.1.1'
```

### Gradle Kotlin DSL

```kotlin
implementation("io.github.numichi:reactive-logger:2.1.1")
```

# Usage

## Logger library

Because it is a layer on slf4j so you can use specific logger libraries like Backlog, Log4j2 and so on. By default, Spring uses the Backlog log library. So, if you don't would like to apply anything
else, you don't need additional dependencies.

### Log4j2

When using Log4j2 you should 2 things. You have to deactivate Logback and import Log4j2 dependency. Configure log4j2.xml as required. But it is already
a [configuration](https://logging.apache.org/log4j/2.x/manual/configuration.html) of the logger library.

Many documentation can be found on the internet ([here](https://www.callicoder.com/spring-boot-log4j-2-example/) and [here](https://www.baeldung.com/spring-boot-logging)) on how can you change from
Logback to Log4j2. The articles mainly use XML configuration, so I presented an example with Gradle Kotlin DSL configuration.

```kotlin
configurations {
    all {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
}
```

# How use `reactive-logger` with examples
## Common
### DefaultValues
There is a common `DefaultValues` class for storing default values. This stores and you can configuration that:
- What should be the key string for MDC store in reactor context? Default: `DEFAULT_REACTOR_CONTEXT_MDC_KEY`
- What schedule should it use for logging mode? Default: `Schedulers.boundedElastic()`

**Important:** If you would like to configure, apply the reset first because it throws an AlreadyConfigurationException exception if already configured!

**Example configuration (Kotlin):**
```kotlin
fun main() {
    DefaultValues.reset()
    DefaultValues.configuration("other-key", Schedulers.parallel())
}
```

## Reactor

```kotlin
class Example {
    private val log = ReactiveLogger.builder()
      .withLogger(org.slf4j.LoggerFactory.getLogger(this::class.java)) // Optional parameter [org.slf4j.Logger]
      .withError(false) // Optional parameter [boolean] hide or throw exception under logging
      .withMDCContextKey("DEFAULT_REACTOR_CONTEXT_MDC_KEY") // Optional parameter, default from DefaultValues
      .withScheduler(Schedulers.boundedElastic()) // Optional parameter, default from DefaultValues
      .build()
  
    fun foo(msg: String): Mono<Void> {
        return Mono.just(msg)
            .flatMap { 
                log.info(it) // result: Mono<ContextView> // MDC: { "key": "example" }
            }
            .contextWrite {
                MDCContext.modifyContext(it) { mdc ->
                    mdc["key"] = "example"
                }
            }
            .then()
    }
}
```

## Coroutine

```kotlin
class Example {
    // or CoroutineKLogger
    private val customLog = CoroutineLogger.builder(CustomContext) { coroutineContext[it]?.customAttrWhatTypeIsContextView } 
      // same as below
  
    private val log = CoroutineLogger.reactorBuilder() // reactorBuilder() alias builder(ReactorContext) { coroutineContext[it]?.context }
      .withLogger(org.slf4j.LoggerFactory.getLogger(this::class.java)) // Optional parameter [org.slf4j.Logger]
      // same as below
  
    private val logK = CoroutineKLogger.reactorBuilder() // reactorBuilder() alias builder(ReactorContext) { coroutineContext[it]?.context }
      .withLogger(io.github.numichi.reactive.logger.LoggerFactory.getKLogger(this::class.java)) // Optional parameter [mu.Logger]
      .withError(false) // Optional parameter [boolean] hide or throw exception under logging
      .withMDCContextKey("DEFAULT_REACTOR_CONTEXT_MDC_KEY") // Optional parameter, default from DefaultValues
      .withScheduler(Schedulers.boundedElastic()) // Optional parameter, default from DefaultValues
      .build()
  
    suspend fun foo(msg: String) {
        val mdc = readMdc()
        mdc["key"] = "example"
        withMDCContext(mdc) {
            customLog.info("bar") // suspended // MDC: { "key": "example" }
            log.info("bar") // suspended // MDC: { "key": "example" }
            logK.info { "bar" } // suspended // MDC: { "key": "example" }
        }

        customLog.info("bar") // suspended // MDC: {}
        log.info("bar") // suspended // MDC: {}
        logK.info { "bar" } // suspended // MDC: {}
    }
}
```
