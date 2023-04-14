rootProject.name = "reactive-logger"

pluginManagement {
    plugins {
        kotlin("jvm") version "1.8.20"
        kotlin("plugin.spring") version "1.8.20"
        kotlin("kapt") version "1.8.20"
        id("com.adarshr.test-logger") version "3.2.0"
        id("org.springframework.boot") version "3.0.5"
        id("io.spring.dependency-management") version "1.1.0"
    }
}

include("reactive-logger")
include("reactive-logger-example-kotlin")
include("reactive-logger-example-java")