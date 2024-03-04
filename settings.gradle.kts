rootProject.name = "reactive-logger"

pluginManagement {
    plugins {
        kotlin("jvm") version "1.9.22"
        kotlin("kapt") version "1.9.22"
        kotlin("plugin.spring") version "1.9.22"
        id("com.adarshr.test-logger") version "4.0.0"
        id("org.springframework.boot") version "3.2.3"
        id("io.spring.dependency-management") version "1.1.4"
    }
}

include("reactive-logger")
//include("reactive-logger-example-kotlin")
//include("reactive-logger-example-java")