# reactive-logger 
[![CircleCI](https://circleci.com/gh/Numichi/reactive-logger/tree/develop.svg?style=shield)](https://circleci.com/gh/Numichi/reactive-logger/tree/develop)
[![Maintainability](https://api.codeclimate.com/v1/badges/3b8d1ff3b57491648f7d/maintainability)](https://codeclimate.com/github/Numichi/reactive-logger/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/3b8d1ff3b57491648f7d/test_coverage)](https://codeclimate.com/github/Numichi/reactive-logger/test_coverage)
[![Apache-2.0](https://img.shields.io/badge/license-Apache--2.0-blue)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.numichi/reactive-logger.svg?label=Maven%20Central)](https://repo1.maven.org/maven2/io/github/numichi/reactive-logger/)
![Tested on](https://img.shields.io/badge/tested%20on-jvm8-blue)
[![Reactor Project](https://img.shields.io/badge/supported-Java%20and%20Kotlin%20Coroutine%20with%20Reactor-blue)](https://projectreactor.io/)

_After releases, SonaType or mvnrepostiroy may not appear. Regardless, the package is available._

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
[Wiki](https://github.com/Numichi/reactive-logger/wiki/Documentation)

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
