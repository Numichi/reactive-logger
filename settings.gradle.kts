rootProject.name = "reactive-logger"

pluginManagement {
    plugins {
        kotlin("jvm") version "1.7.21"
        kotlin("plugin.spring") version "1.7.21"
        id("com.adarshr.test-logger") version "3.2.0"
        id("org.springframework.boot") version "2.7.6"
        id("io.spring.dependency-management") version "1.1.0"
    }
}

include("reactive-logger")
include("reactive-logger-example-kotlin")
include("reactive-logger-example-java")