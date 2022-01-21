plugins {
    java
    jacoco
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
    implementation(project(":reactive-logger-common"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("io.projectreactor:reactor-test:3.4.14")
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.1")
    testImplementation("org.mockito:mockito-core:4.2.0")


    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)

}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

jacoco {
    toolVersion = "0.8.7"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}