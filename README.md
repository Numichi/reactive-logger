# Reactive-Logger

[![CircleCI](https://circleci.com/gh/Numichi/reactive-logger/tree/master.svg?style=shield)](https://circleci.com/gh/Numichi/reactive-logger/tree/develop)
[![Test Coverage](https://api.codeclimate.com/v1/badges/74b86e2f051a0c3c9e11/test_coverage)](https://codeclimate.com/github/Numichi/reactive-logger/test_coverage)
[![Apache-2.0](https://img.shields.io/badge/license-Apache--2.0-blue)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.numichi/reactive-logger.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.numichi%22%20AND%20a:%22reactive-logger%22)
![Tested on](https://img.shields.io/badge/tested%20on-jvm17-blue)
[![Reactor Project](https://img.shields.io/badge/supported-Java%20and%20Kotlin%20Coroutine%20with%20Reactor-blue)](https://projectreactor.io/)

- [Compatibility Matrix](#compatibility-matrix)
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
  - [Extended MDC scope in coroutine (withMDCContext)](#extended-mdc-scope-in-coroutine-withmdccontext)
- [Configuration](#configuration)
  - [Default values](#default-values)
  - [Hook configuration](#hook-configuration-from-v510)
  - [Spring support](#spring-support)
- [Other helper method](#other-helper-method)
  - [LoggerFactory](#loggerfactory)
- [FAQ](#faq)

# Compatibility Matrix

| Version (current)                                                                   | Compatibility | Min Java | GitHub Branch                                                                        | 
|-------------------------------------------------------------------------------------|---------------|----------|--------------------------------------------------------------------------------------|
| [6.0.0](https://central.sonatype.com/search?q=io.github.numichi%3Areactive-logger&smo=true) | Spring Boot 3 | Java 17  | master                                                                               |
| [4.0.5](https://mvnrepository.com/artifact/io.github.numichi/reactive-logger/4.0.5) (maintenance) | Spring Boot 2 | Java 11  | [SpringBoot-2.x.x](https://github.com/Numichi/reactive-logger/tree/SpringBoot-2.x.x) |

# Getting Started

## What is the source of motivation?

I think this description approaches
the [What Is a Good Pattern for Contextual Logging? (MDC)](https://projectreactor.io/docs/core/release/reference/#faq.mdc) well.
Furthermore, there is the
so-called [lifting solution example](https://github.com/archie-swif/webflux-mdc/blob/master/src/main/java/com/example/webfluxmdc/MdcContextLifter.java),
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

**There is a `kotlinx.coroutines.slf4j.MDCContext`.**
There is a `kotlinx.coroutines.slf4j.MDCContext`. Sure, just this `MDCContext` (not my MDCContext) is planned for non-reactive coroutine environment. So in the example, `MDC.put()` used ThreadLocal, not reactive context. (You can see [MDCContext original documentation](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-slf4j/kotlinx.coroutines.slf4j/-m-d-c-context/).) I don't recommend using it because you must manually (or another solution) ensure data transfer into the reactor context if you would like to switch between coroutine and reactor environments. The `await...` methods from reactor to coroutine or `mono { ... }`/ `flux {...}` from coroutine to reactor methods ensure transfer reactor context. Thus, MDC information will follow us if we write MDC information into the reactor context from the beginning. In this case, the suspend method will be our friend.

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

When using Log4j2 you should 2 things. You have to deactivate Logback and import Log4j2 dependency.

Many documentation can be found on the internet ([here](https://www.callicoder.com/spring-boot-log4j-2-example/) and [here](https://www.baeldung.com/spring-boot-logging)) on how can you change from
Logback to Log4j2.

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
main suffixes: `Logger` and `KLogger`. Where you used Logger that component use `org.slf4j.Logger` interface. Similar way, KLogger means that component use `io.github.oshai.kotlinlogging.KLogger`.

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
    private val CoroutineLogger logger = CoroutineLogger.getLogger(Example::class.java);
    
    suspend fun minimal() {
        logger.info("minimal") // suspend
    }

    suspend fun getAndLogUUID(): UUID {
        val uuid = UUID.randomUUID()
        logger.info(uuid.toString()) // suspend
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
Example output MDC information by "foo-example".
```json
{
  "foo": "bar",
  "bff9c950-59bf-4356-bd2e-a9b2451c4f06": "3792db45-58a8-4959-939a-b42271f15dca"
}
```

## Context modification and reading 
Under the key namespace, the basic requirement is that the value stays `Map<String, String>` type. You can modify it as you would like. Library provides the same support methods for specific Java/Kotlin and Reactive/Coroutine environments. Their use is not mandatory. It just makes the modification easier.

You can use `MDCContext` in Java or Reactor environment and specific functions in Coroutine.

There is an option to ignore the context key if you use only this library for modification. Thus, in this case, it is unnecessary to give in the following methods `contextKey` parameters. By default, this `contextKey` will be `"DEFAULT_REACTOR_CONTEXT_MDC_KEY"`. If you would like to use another default context key, see the [configuration](#default-values) section.

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
| Context               | modifyMdc          | `Context` | Similar to `MDCContext.modify`       |
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
    val mdc = readOrDefaultMdc() // suspend
    return mdc.contextKey as String // can throw ClassCastException
}
```

## MDC

It is a reactor context `Map<String, String>` representation that is not modifiable (like an immutable class). Every modification creates a new instance you can pass for context modification or open new context scope.

If you need new instance is required, you can create a new one by the constructor. Also, valid here is that if you do not configure the context key, the default context key value will be from the default configuration.

Available methods:

| method | can use operator mode in Kotlin             | description                                                                      |
|--------|---------------------------------------------|----------------------------------------------------------------------------------|
| get    | yes, example `mdc["foo"]`                   | Result value of map of context.                                                  |
| plus   | yes, example `mdc1 + map("foo" to "bar")`   | Can add new or can override value into the map.                                  |
| minus  | yes, example `mdc1 - arrayOf("foo", "bar")` | The new MDC instance will not contain the keys in the map. Like, remove by keys. |
| keep   | no                                          | Reverse like minus. Only keep their selected keys.                               |
| clean  | no                                          | Result current MDC instance, just map will be empty.                             |


## Extended MDC scope in coroutine (withMDCContext)

It is trivial how you can add new value into the reactor context (`Context.contextWrite()` or see [Context modification and reading](#context-modification-and-reading)), but this is not in the coroutine. You can call `readMdc()` or `coroutineContext[ReactorContext]` and modification `MDC` or `Map`, just how do you pass it to `withContext` conveniently? Library gives a helper method `withMDCContext()` that waits for one or more MDC instance and opens new context scope. 

```kotlin
suspend fun openNewScope() {
    withMDCContext(readMdc() + ("foo" to "bar")) {
        val barValue = readMdc()["foo"]
    }
}
```


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

## Hook configuration

The hook aims to transfer data into an MDC snapshot from the current context view. For instance, if you or another component saved data in a 
reactive context, you can configure the transfer of that data into the MDC snapshot for every log event. Therefore, the hook will be activated 
for each logging event and supplement the current MDC information.

There is a method for adding a hook: `Configuration.addHook(...)`.
If you use `Spring Boot`, you can use `@Bean` with `MDCContextHook` type and don't need used `Configuration` class directly.

When you create a hook, you can set its activation position. The `BEFORE` position means it will trigger before reading the current `ContextView`, 
defining the base snapshot. Afterward, it captures an MDC-like snapshot from the `ContextView`, adding it. This may override the pre-configured 
records (see the BEFORE event) or create a default snapshot. Logically, the AFTER hook will run afterward, allowing the addition or modification 
of this MDC-like snapshot.

❗**Important:** If any exception occurs in a hook function, the entire hook will be ignored.

| parameter | property                  | type                                             | description                           |
|-----------|---------------------------|--------------------------------------------------|---------------------------------------|
| position  | optional (default: AFTER) | Position (enum)                                  | When should it run?                   |
| hook      | required                  | Function2<ContextView, MDC, Map<String, String>> | This hook will be trigger every logs. |

| Function2 parameter | property     | type                | description                                                                    |
|---------------------|--------------|---------------------|--------------------------------------------------------------------------------|
| contextView         | non-nullable | ContextView         | Currently reactor context                                                      |
| mdc                 | non-nullable | MDC                 | Current MDC snapshot. Immutable. (If position is BEFORE, it will be empty)     |
| return value        | non-nullable | Map<String, String> | Write or overwrite into the snapshot. If key of map is null that will be skip. |

Kotlin example with Micrometer in Spring Boot 3+ ([source](https://github.com/micrometer-metrics/micrometer-samples/blob/main/webflux/src/main/java/com/example/micrometer/WebFluxApplication.java)):

```kotlin
import io.github.numichi.reactive.logger.hook.MDCContextHook
import io.micrometer.context.ContextSnapshot
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor
import io.micrometer.tracing.Tracer
import org.springframework.context.annotation.Configuration

@Configuration
class MDCContextHookConfiguration(private val tracer: Tracer) {

    @Bean
    fun hook(): MDCContextHook {
        return MDCContextHook { contextView: ContextView, _: MDC ->
            val map = mutableMapOf<String, String>()

            ContextSnapshot.setThreadLocalsFrom(contextView, ObservationThreadLocalAccessor.KEY).use {
                val context = this.tracer.currentSpan()?.context()
                map["traceId"] = context?.traceId() ?: "n/a"
                map["spanId"] = context?.spanId() ?: "n/a"
                map["parentId"] = context?.parentId() ?: "n/a"
            }

            map
        }
    }
}
```

## Spring support

### Default value

You also can configure via `application.properties` or `yml` default values. But, if you have configured the context key or scheduler via `Configuration`, your application.properties default setting will be skipped when it is not pre-defined value, because `Configuration` is the highest priority. In contrast, you can force setting in `application.properties` to override the current values with `forceUse` parameter set to true (default false).

```properties
reactive-logger.forceUse=true 
reactive-logger.contextKey=mdcContextKey
reactive-logger.scheduler=parallel
```

### Hook

Hooks can be configured via Spring Bean, and you can add more same beans. An MDCHook class must be used instead of a configuration class. This class has the same parameters as `addHook` or `addGenericHook` methods, and you can decide what type value is expected in Generic.

```java
// Java
import org.springframework.cloud.sleuth.TraceContext;

@Configuration
@ConditionalOnClass(TraceContext.class)
public class HookConfiguration {

    @Bean
    public MDCHook<TraceContext> traceContextHook() {
        return new MDCHook<>("hook-name", TraceContext.class, (traceContext, mdc) -> {
            Objects.requireNonNull(traceContext, "traceContext must not be null");
      
            Map<String, String> result = new HashMap<>();
            result.put("traceId", traceContext.traceId());
            result.put("spanId", traceContext.spanId());
            result.put("parentId", traceContext.parentId());
      
            return result;
        });
    }
}
```
```kotlin
// Kotlin
import org.springframework.cloud.sleuth.TraceContext

@Configuration
@ConditionalOnClass(TraceContext::class)
class LoggerHookConfiguration {

    @Bean
    fun traceContextHook(): MDCHook<TraceContext> {
        return MDCHook("traceContextHook", TraceContext::class.java) { traceContext, _ ->
            requireNotNull(traceContext) { "traceContext must not be null" }
      
            val map = mutableMapOf<String, String?>()
            map["traceId"] = traceContext.traceId()
            map["spanId"] = traceContext.spanId()
            map["parentId"] = traceContext.parentId()
      
            map
        }
}
```

# Other helper method

## LoggerFactory

There is `io.github.numichi.reactive.logger.LoggerFactory` similar to `org.slf4j.LoggerFactory` 
is available to Logger or KLogger creation.

⚠️ **_WARNING_**: `LoggerFactory` serves as a proxy for the `KotlinLogging` and `LoggerFactory` classes.  
These logger instances do not utilize the features of the `reactor-logger` library! 
If you wish to leverage the `reactor-logger` library features, make sure to use `ReactiveLogger`, 
`ReactiveKLogger`, `CoroutineLogger`, or `CoroutineKLogger` classes.

```java
// Java
Logger logger1 = org.slf4j.LoggerFactory.getLogger("foo");
Logger logger2 = io.github.numichi.reactive.logger.getLogger("foo");
```
```kotlin
// Java
val logger1: Logger = org.slf4j.LoggerFactory.getLogger("foo")
val logger2: Logger = io.github.numichi.reactive.logger.LoggerFactory.getLogger("foo")

val kLogger1: KLogger = io.github.oshai.kotlinlogging.KotlinLogging.logger(logger1)
val kLogger2: KLogger = io.github.oshai.kotlinlogging.KotlinLogging.logger(logger2)

val kLogger3: KLogger = io.github.numichi.reactive.logger.LoggerFactory.getKLogger("foo")
val kLogger4: KLogger = io.github.numichi.reactive.logger.LoggerFactory.getKLogger(logger1)
val kLogger5: KLogger = io.github.numichi.reactive.logger.LoggerFactory.getKLogger(logger2)
```

# FAQ

> **_QUESTION_**: Can I use both approaches?

Yes. For example, see a filter and controller in Kotlin coroutine-based reactive project. You cannot use approaches other than `Mono<Void>` in `WebFilter`, but you can use `suspend` method in controller.

> **_QUESTION_**: Can I define a custom appender, encoder and layout in logback or log4j2? Will there be a conflict with this library?

There will be no conflict because basically this library is a Map handler and slf4j caller. It does not deal with the slf4j implementation, so you can use log4j2, logback, etc... implementation and specific message formatter configuration. (Just in the test used log4j2 implementation and Lig4j2 plugin to log format.)
