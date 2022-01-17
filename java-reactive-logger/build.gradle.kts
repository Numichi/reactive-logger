plugins {
    java
    id("com.adarshr.test-logger") version "3.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.projectreactor:reactor-core:3.4.14")
    implementation("org.slf4j:slf4j-api:1.7.33")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("io.projectreactor:reactor-test:3.4.14")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}