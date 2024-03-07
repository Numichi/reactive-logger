rootProject.name = "reactive-logger"

pluginManagement {
    plugins {
        kotlin("jvm") version "1.9.22"
        kotlin("kapt") version "1.9.22"
        kotlin("plugin.spring") version "1.9.22"
        id("org.springframework.boot") version "3.2.3"
        id("io.spring.dependency-management") version "1.1.4"
        id("org.jmailen.kotlinter") version "4.2.0" apply false
    }
}

include("reactive-logger")
include("reactive-logger-example-kotlin")
include("reactive-logger-example-java")