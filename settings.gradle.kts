rootProject.name = "reactive-logger"

pluginManagement {
    plugins {
        kotlin("jvm") version "1.7.20"
        kotlin("plugin.spring") version "1.7.20"
        id("com.adarshr.test-logger") version "3.2.0"
        id("org.springframework.boot") version "2.7.4"
        id("io.spring.dependency-management") version "1.0.14.RELEASE"
    }
}

include("reactive-logger")
include("reactive-logger-example-kotlin")
include("reactive-logger-example-java")