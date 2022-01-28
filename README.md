# reactive-logger 
[![CircleCI](https://circleci.com/gh/Numichi/reactive-logger/tree/develop.svg?style=shield)](https://circleci.com/gh/Numichi/reactive-logger/tree/develop)
[![Maintainability](https://api.codeclimate.com/v1/badges/3b8d1ff3b57491648f7d/maintainability)](https://codeclimate.com/github/Numichi/reactive-logger/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/3b8d1ff3b57491648f7d/test_coverage)](https://codeclimate.com/github/Numichi/reactive-logger/test_coverage)
[![Apache-2.0](https://img.shields.io/badge/license-Apache--2.0-blue)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.numichi/reactive-logger.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.numichi%22%20AND%20a:%22reactive-logger%22)
![Tested on](https://img.shields.io/badge/tested%20on-jvm8-blue)
[![Reactor Project](https://img.shields.io/badge/supported-Java%20and%20Kotlin%20Coroutine%20with%20Reactor-blue)](https://projectreactor.io/)

_After releases, SonaType or mvnrepostiroy may not appear. Regardless, the package is available._

## What is the source of motivation?
I think this description approaches the [What Is a Good Pattern for Contextual Logging? (MDC)](https://projectreactor.io/docs/core/release/reference/#faq.mdc) well. Furthermore, there is the so-called [lifting solution](https://www.novatec-gmbh.de/en/blog/how-can-the-mdc-context-be-used-in-the-reactive-spring-applications/), which I think is overkill. In this (lifing) example, he/she is using Kotlin, but with a Reactor API, not a coroutine. This will cause problems, see later. If you stay with Reactor API, there is no problem with lifting. I mean, I didn't experience it.

The lifting solution runs a hook for each reactor event. This generates a lot of unnecessary events and class creation. I have [tested](https://github.com/Numichi/reactive-java-mdc-investigation) the lifting solution with a minimal controller with Spring Boot _(see: GET /counter)_. MDC map copy had run about 129 times for only one request. Then let's count, how many times will run on one of the more complex applications? 

Another question: This lifting work with coroutine? Answer: not really, if you use Reactor API, It breaks the contents of the MDC so next time an slf4j logger will not see MDC contents again in a coroutine. MDC and CoroutineContext are two different universes. Lifting will not be triggered by any event of coroutine. I was surprised by the multiple parallel requests, and a suspended delay resulted in MDC context data slipping to another request, which is unhealthy.

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
