# reactive-logger

[![CircleCI](https://circleci.com/gh/Numichi/reactive-logger/tree/master.svg?style=shield)](https://circleci.com/gh/Numichi/reactive-logger/tree/develop)
[![Maintainability](https://api.codeclimate.com/v1/badges/3b8d1ff3b57491648f7d/maintainability)](https://codeclimate.com/github/Numichi/reactive-logger/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/3b8d1ff3b57491648f7d/test_coverage)](https://codeclimate.com/github/Numichi/reactive-logger/test_coverage)
[![Apache-2.0](https://img.shields.io/badge/license-Apache--2.0-blue)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.numichi/reactive-logger.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.numichi%22%20AND%20a:%22reactive-logger%22)
![Tested on](https://img.shields.io/badge/tested%20on-jvm11-blue)
[![Reactor Project](https://img.shields.io/badge/supported-Java%20and%20Kotlin%20Coroutine%20with%20Reactor-blue)](https://projectreactor.io/)

# What is the source of motivation?

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

# Usage

## Logger library

Because it is a layer on slf4j so you can use specific logger libraries like Backlog, Log4j2 and so on. By default, Spring uses the Backlog log library. So, if you don't would like to apply anything
else, you don't need additional dependencies.

### Log4j2 (optional)

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

# How use `reactive-logger` with examples

## Breaking change on v3.0.0

- min. JVM 11
- DefaultValues class rename to Configuration.
- Removed all `Configuration.configuration(...)` and configuration exception
- Added Hook mechanism. See below.
- Logger instance builder mechanism has been replaced by a `.getLogger()` method.

## Configuration

### Default values

The `reactive-logger` uses two default values. These are `defaultReactorContextMdcKey` and `defaultScheduler`.

- `defaultReactorContextMdcKey`: Data intended for MDC can be found under this key in the reactor context.
    - Default value: `DEFAULT_REACTOR_CONTEXT_MDC_KEY` (String)
- `defaultScheduler`: Scheduler used for logging.
    - Default value: `Schedulers.boundedElastic()` (Scheduler)

**Example configuration (Kotlin):**

```kotlin
fun main() {
  Configuration.reset() // resets parameters to defaults, so optimal here
  Configuration.defaultScheduler = Schedulers.parallel()
  Configuration.defaultReactorContextMdcKey = "mdcRecords"

  // ... spring boot start example
}
```

### Hook

> Configuration.addHook(...)
>
> Configuration.addGenericHook(...)

The purpose of the hook is to transfer non-manually configured values to the MDC. These value pairs can be inserted into the reactor context by a library independent of us. Of course, we can also fill
it algorithmically.

So, for every logging or MDC query, the hook checks if the reactor context has the keyword to search. If so, we can tell how we want to save it in the MDC through a specified function or lambda. If
this function gives to return an empty map or any exception is thrown the hook will do nothing with the data. If the context key does not exist then the first parameter will get null.

Important! Hooks can overwrite any current snapshot data stored in MDC. (It can not overwrite source data.)

**Hook's parameters:**

- `name`: name identifier, if you would to overwrite or delete it later.
- `contextKey`: One key of the Reactor context that we want to reach. It can be String, number value or any `.class`.
- `order`: The lowest value will be the first in the row and the last is -1. It executes the snapshot from the reactor context between -1 and 0, then continues with the value 0. If
  there is any MDC context key conflict, the new will override the old value.
- `hook`: This is lambda. What should do in this case? What key-value pairs should the MDC be filled with?

**About hook lambda**

There are 2 methods `addGenericHook` and `addHook`. The only difference is that while the first option can be specified in the type, the latter is `Any?` in any case.

- The first parameter is nullable `Any?` or `T?` depending on the method used. This value is null if configured key is not found in the reactor context or contains a different type than expected (only
  in `addGenericHook` case).
- The second parameter is currently a copy of the MDC snapshot that is being uploaded. Current content can be checked to see if there is a value that we want to overwrite or exists. This can be
  modified, but it has no effect on the snapshot MDC.
- The return value is a key-value map that will merge into the currently MDC snapshot. If this map is empty or created to throw any exception then merge will skip.

In terms of configuration, you can add and remove dynamically however hooks are cached. You do not need to clear the cache when you modify hooks (add or remove) because every modification will release
them and they will be re-caching.

#### Example

Suppose you have the following context in JSON format when you are logging:

```json
{
  "banana": "apple",
  "DEFAULT_REACTOR_CONTEXT_MDC_KEY": {
    "foo": "foo",
    "bar": "bar"
  }
}
```

Banana was added by a library independent of us, which may not exist in context. But, if it exists, we would like to see (for example) in the MDC map with uppercase transform with banana2 key. Like
MDC contain:

```json
{
  "foo": "foo",
  "bar": "bar",
  "banana2": "APPLE"
}
```

We have to add on application start (or configuration) this next hook:

```kotlin
// value is Any? or Object all time. So we need check type. If it is not String we can exit with exception.
Configuration.addHook("hookName", "banana", -1) { value: Any?, _: MDC ->
  if (it !is String) {
    throw IllegalArgumentException()
  }

  mapOf("banana2" to it.uppercase())
}
```

## Use Logger

The below classes are available. These are similar just different in environment or concept.

- CoroutineKLogger.getLogger(...) - used suspend functions and mu.KLogger concept
- CoroutineLogger.getLogger(...) - used suspend functions and slf4j Logger concept
- ReactorKLogger.getLogger(...) - used Mono and mu.KLogger concept
- ReactorLogger.getLogger(...) - used Mono and slf4j Logger concept

The first parameter is required like slf4j's `getLogger()` and others parameters can individually override default values in this case. So there is an opportunity for more MDC context keys could be
used and detected in the hook via MDC value (second parameter).

```kotlin
val logger = CoroutineLogger.getLogger(ExampleClass::class.java)
logger.info("log message")
```

## How to modify MDC content in reactor context

### in Reactor

`MDCContext.modifyContext` can support modify MDC content map in Reactor context. If you use multiple MDC content map in reactor context use second parameter to override default key.

```kotlin
.contextWrite {
  MDCContext.modifyContext(it) { mdc ->
    mdc["foo"] = "foo"
  }
}
```

If you define manually one or more content with `io.github.numichi.reactive.logger.MDC` you can use `MDCContext.put(Context, MDC...)`. This class also provides reading opportunities.

### in Coroutine

Similar to Reactor, MDC contents can be read and supplemented. Presented through example:

```kotlin
import io.github.numichi.reactive.logger.MDC

// ...
.contextWrite {
  val map = MDC()
  map["foo"] = foo
  putMdc(it, map) // return Context
}
```

```kotlin
val content = readMdc()
content["bar"] = "bar"
withMDCContext(content) {
  readMdc() // contain: {"bar": "bar"}
}
```
