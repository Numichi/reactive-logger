# Reactive-Logger

[![CircleCI](https://circleci.com/gh/Numichi/reactive-logger/tree/master.svg?style=shield)](https://circleci.com/gh/Numichi/reactive-logger/tree/develop)
[![Test Coverage](https://api.codeclimate.com/v1/badges/74b86e2f051a0c3c9e11/test_coverage)](https://codeclimate.com/github/Numichi/reactive-logger/test_coverage)
[![Apache-2.0](https://img.shields.io/badge/license-Apache--2.0-blue)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.numichi/reactive-logger.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.numichi%22%20AND%20a:%22reactive-logger%22)
![Tested on](https://img.shields.io/badge/tested%20on-jvm11-blue)
[![Reactor Project](https://img.shields.io/badge/supported-Java%20and%20Kotlin%20Coroutine%20with%20Reactor-blue)](https://projectreactor.io/)

- [Getting Started](#what-is-the-source-of-motivation)
    - [What is the source of motivation?](#what-is-the-source-of-motivation)
    - [Overview](#overview)
- [Quick Start](#getting-started)
    - [Dependency](#dependency)
    - [Logger library (optional)](#logger-library-optional)
    - [Logging minimal usage](#logging-minimal-usage)
    - [How does it storage MDC information?](#how-does-it-storage-mdc-information)
    - [Context modification and reading](#context-modification-and-reading)
    - [MDC](#mdc)
- [Configuration](#getting-started)
    - [Default values](#getting-started)
    - [Hook](#getting-started)
    - [Spring support](#getting-started)

# Getting Started

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

---

# Quick Start

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

```groovy
implementation 'io.github.numichi:reactive-logger:VERSION'
```

### Gradle Kotlin DSL

```kotlin
implementation("io.github.numichi:reactive-logger:VERSION")
```

## Logger library (optional)

Because it is a layer on slf4j so you can use specific logger libraries like Backlog, Log4j2 and so on. By default, Spring uses the Backlog log library. So, if you don't would like to apply anything
else, you don't need additional dependencies.

### Log4j2 configuration

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

## Logging minimal usage

There are two main categories: `Reactive` and `Coroutine`. You can use Reactive in Project Reactor (Java or Kotlin) code-based, Coroutine mainly in Kotlin Coroutine environment. There are also two
main suffixes: `Logger` and `KLogger`. Where you used Logger that component use `org.slf4j.Logger` interface. Similar way, KLogger means that component use `mu.KLogger`.

> **_NOTE:_** You can not use Coroutine in Java code. Recommend using just ReactorLogger in Java code-based projects.

They are available below. These are not implemented Logger or KLogger but, environment-specifically they contain the same logging methods. In the case of reactive, the logging methods
return `Mono<Void>`. In the case of coroutine, all methods are `suspend` types.

- ReactiveLogger
- ReactiveKLogger
- CoroutineLogger
- CoroutineKLogger

> **_NOTE:_** Logically, By definition, MDC information should be stored in the Reactive Context and not at the ThreadLocal level. So, reactor context can be reached by subscription (in reactor) or suspend method (in coroutine).

> **_NOTE:_** Every log is done in a special wrap where all MDC information is cloned into ThreadLocal because slf4j MDC uses ThreadLocal, and ThreadLocal data will be cleaned after the log event.

If you want to add some MDC information to the context, check [Context modification and reading](#context-modification-and-reading) point.

```java
// Java example
class Example {
    private final ReactiveLogger logger = ReactiveLogger.getLogger(Example.class);
    
    public Mono<Void> minimal() {
        return logger.info("minimal")
    }
    
    public Mono<UUID> getAndLogUUID() {
        return Mono.just(UUID.randomUUID())
            .flatMap(uuid -> logger.info(uuid.toString()).thenReturn(uuid));
    }
}
```
```kotlin
// Kotlin example
class Example {
    private val CoroutineLogger logger = CoroutineLogger.getLogger(Example.class);
    
    suspend fun minimal() {
        logger.info("minimal") // suspended
    }

    suspend fun getAndLogUUID(): UUID {
        val uuid = UUID.randomUUID()
        logger.info(uuid.toString()) // suspended
        return uuid
    }
}
```

## How does it storage MDC information?
Every MDC information is stored in the Reactor context. All MDC information can be stored under a predefined one or more key namespace. You can set it manually or entrust the library to handle it. Mostly `<String, String>` key-value map can be stored in. Thus, the key namespace follows the MDC requirement. If it finds a different type the read from reactor context, `toString()` will be run.

Recommend context structure:
- Java: `Map<Object, Map<String, String>>`
- Kotlin: `Map<Any, Map<String, String?>>`

> **_NOTE:_** If key is null in Java, it will be filtered. In Kotlin is null-safely.

> **_NOTE:_** If it does not find Map under the namespace key, It will throw a ReadException.

```java
// example key namespaces: foo-example and bar-example
Context.of(
    Map.of(
        "foo-example",
        Map.of(
            "foo", "bar",
            UUID.randomUUID(), UUID.randomUUID(),
        ),
        "bar-example",
        Map.of(
            "baz", "foo",
            UUID.randomUUID(), UUID.randomUUID()
        )
    )
);
```
```json
// example output MDC information by "foo-example"
{
  "foo": "bar",
  "bff9c950-59bf-4356-bd2e-a9b2451c4f06": "3792db45-58a8-4959-939a-b42271f15dca"
}
```

## Context modification and reading 
Under the key namespace, the basic requirement is that the value stays `Map<String, String>` type. You can modify it as you would like. Library provides the same support methods for specific Java/Kotlin and Reactive/Coroutine environments. Their use is not mandatory. It just makes the modification easier.

You can use `MDCContext` in Java or Reactor environment and specific functions in Coroutine.

There is an option to ignore the context key if you use only this library for modification. Thus, in this case, it is unnecessary to give in the following methods `contextKey` parameters. By default, this `contextKey` will be `"DEFAULT_REACTOR_CONTEXT_MDC_KEY"`. If you would like to use another default context key, see the configuration section.

> **_NOTE:_** `io.github.numichi.reactive.logger.MDC` class represent one namespace with MDC information. Typically, some methods return with this, and you can manage your content through it.

| MDCContext method name | return      | description                                                                                                                                                    |
|------------------------|-------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| put                    | `Context`   | It will load data using MDC objects into the target context.                                                                                                   |
| modify                 | `Context`   | It uses `Function<MDC, MDC>` or insert transferred information into the target context.                                                                        |
| read                   | `Mono<MDC>` | Returns the current MDC based on the current context. It will throw ReadException if does not exist context key.                                               |
| read                   | `MDC`       | Returns the current MDC based on the transferred context view. It will throw ReadException if does not exist context key. Need a `ContextView` into parameter. |
| readOrDefault          | `Mono<MDC>` | Similar to reading, just gives a default MDC if context key does not exist.                                                                                    |
| readOrDefault          | `MDC`       | Similar to reading, just gives a default MDC if context key does not exist. Need a `ContextView` into parameter.                                               |
| snapshot               | `Mono<MDC>` | Similar to reading, but supplemented with Hook information.                                                                                                    |
| snapshot               | `MDC`       | Similar to reading, but supplemented with Hook information. Need a `ContextView` into parameter.                                                               |

```java
// example
public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    return chain.filter(exchange)
        .flatMap(())
        .contextWrite(context -> MDCContext.modify(context, mdc -> mdc.plus(Map.of("foo", UUID.randomUUID().toString())))) // context key will "DEFAULT_REACTOR_CONTEXT_MDC_KEY"
        .contextWrite(context -> MDCContext.modify(context, Map.of("bar", UUID.randomUUID().toString()))) // context key will "DEFAULT_REACTOR_CONTEXT_MDC_KEY"
        .contextWrite(context -> MDCContext.modify(context, "another-namespace", Tuples.of("baz", UUID.randomUUID().toString()))); // context key will "another-namespace"
}

public Mono<String> getCurrentContextKey() {
    return MDCContext.readOrDefault()
        .flatMap(mdc -> {
            try {
                return Mono.just((String) mdc.getContextKey());
            } catch (ClassCastException e) {
                return Mono.error(e);
            }
        });
}
```

You can find coroutine specific functions in `io.github.numichi.reactive.logger.coroutine` package. There are two categories, one of them Context and ContextView extended functions, another standard Kotlin function.

| functions name   | is suspend                            | return | description                           |
|------------------|---------------------------------------|--------|---------------------------------------|
| readMdc          | depend on need a `ContextView` or not | `MDC`  | Similar to `MDCContext.read`          |
| readOrDefaultMdc | depend on need a `ContextView` or not | `MDC`  | Similar to `MDCContext.readOrDefault` |
| snapshotMdc      | depend on need a `ContextView` or not | `MDC`  | Similar to `MDCContext.snapshot`      |

| source type           | extended functions | return    | description                          |
|-----------------------|--------------------|-----------|--------------------------------------|
| Context               | Context.modifyMdc  | `Context` | Similar to `MDCContext.modify`       |
| Context & ContextView | getMdc             | `MDC`     | equals with above readMdc()          |
| Context & ContextView | getOrDefaultMdc    | `MDC`     | equals with above readOrDefaultMdc() |
| Context & ContextView | snapshotMdc        | `MDC`     | equals with above snapshotMdc()      |

```kotlin
// example
override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
    return chain.filter(exchange)
        .contextWrite { context ->
            context.modifyMdc { mdc -> mdc.plus(mapOf("foo" to UUID.randomUUID().toString())) } // context key will "DEFAULT_REACTOR_CONTEXT_MDC_KEY"
        }
        .contextWrite { context ->
            context.modifyMdc(mapOf("bar" to UUID.randomUUID().toString())) // context key will "DEFAULT_REACTOR_CONTEXT_MDC_KEY"
        }
        .contextWrite { context ->
            context.modifyMdc("another-namespace", Tuples.of("baz", UUID.randomUUID().toString())) // context key will "another-namespace"
        }
}

suspend fun getCurrentContextKey(): String {
    val mdc readOrDefaultMdc() // suspend
    return mdc.contextKey as String // throw ClassCastException
}
```

## MDC
document is being written

---
# Configuration

## Default values

### Context key

The "contextKey" tells the library which in key namespace it should be stored in the context. Some methods contain `contextKey` parameters where you can individually set locally as desired. If you do not use this parameter, it will be by default: `"DEFAULT_REACTOR_CONTEXT_MDC_KEY"`

If you want to use a different value as the default, you can specify it as follows. This can also be modified via application.properties in Spring Boot, but it will be activated after autoconfiguration. Therefore, this autoconfiguration strives for high priority over other configurations. More details in [Spring support](#spring-support). If you want to be sure of configuration because you also use static log initialization which activates before Spring autoconfiguration, then use the below example. If you don't trust even this, enter it manually into `.getLogger()` second `String` parameter. Example: `ReactiveLogger.getLogger("logger-name", "your-context-key");`

> **_NOTE:_** My tests, the setting below was also valid for static creation.

```java
import io.github.numichi.reactive.logger.Configuration;

// Java
public static void main(String[] args) {
    Configuration.setDefaultReactorContextMdcKey("another-default-key");
    // ...
}
```
```kotlin
// Kotlin
import io.github.numichi.reactive.logger.Configuration

fun main(args: Array<String>) {
    Configuration.defaultReactorContextMdcKey = "another-default-key"
    // ...
}
```

### Scheduler

Every log event uses a `Scheduler` method. It can be configured like above [Context key](#context-key) including Spring configuration too. Default method: `Schedulers.boundedElastic()`

That is also valid here if you want to be sure of configuration because you also use static log initialization which activates before Spring autoconfiguration then use the below example or `.getLogger()` parameter.

```java
// Java
import io.github.numichi.reactive.logger.Configuration;
import io.github.numichi.reactive.logger.SchedulerOptions;

public static void main(String[] args) {
    Configuration.setDefaultScheduler(Schedulers.parallel());
    // or
    Configuration.setDefaultScheduler(SchedulerOptions.PARALLEL);
    // ..
}
```
```kotlin
// Kotlin
import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.SchedulerOptions

fun main(args: Array<String>) {
  Configuration.defaultScheduler = Schedulers.parallel()
  // or
  Configuration.setDefaultScheduler(SchedulerOptions.PARALLEL)
  // ...
}
```

## Hook
document is being written

## Spring support
document is being written
