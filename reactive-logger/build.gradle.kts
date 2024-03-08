plugins {
    java
    jacoco
    kotlin("jvm") version "1.9.22"
    kotlin("kapt") version "1.9.22"
    id("java-library")
    id("maven-publish")
    id("signing")
    id("org.jmailen.kotlinter") version "4.2.0"
}

group = project.property("group") as String
version = project.property("version") as String

val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileTestKotlin.kotlinOptions.jvmTarget = "17"

val developerId = project.properties["developerId"] as String? ?: ""
val developerName = project.properties["developerName"] as String? ?: ""
val developerEmail = project.properties["developerEmail"] as String? ?: ""
val ossrhUsername = project.properties["ossrhUsername"] as String? ?: "N/A"
val ossrhPassword = project.properties["ossrhPassword"] as String? ?: "N/A"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
    withJavadocJar()
}

// Disable LogBack for test
configurations {
    testing {
        all {
            exclude("org.springframework.boot", "spring-boot-starter-logging")
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("io.projectreactor:reactor-core:3.6.3")
    implementation("org.slf4j:slf4j-api:2.0.12")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("org.springframework.boot:spring-boot-starter:3.2.3")
    implementation("org.springframework.boot:spring-boot-autoconfigure:3.2.3")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.8.0")
    api("io.github.oshai:kotlin-logging-jvm:6.0.3")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.2.3")
    kapt("org.springframework.boot:spring-boot-configuration-processor:3.2.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.3")
    testImplementation("io.projectreactor:reactor-test:3.6.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("org.apache.logging.log4j:log4j-api:2.23.0")
    testImplementation("org.apache.logging.log4j:log4j-core:2.23.0")
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.23.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    testImplementation("io.mockk:mockk:1.13.10")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

publishing {
    publications {
        create<MavenPublication>("main") {
            groupId = project.property("group") as String? ?: ""
            artifactId = "reactive-logger"
            version = project.property("version") as String? ?: ""
            from(components["java"])

            pom {
                name.set("Reactive logger layer for slf4j")
                description.set("A Java & Kotlin library adapting slf4j for reactive applications")
                url.set("https://github.com/Numichi/reactive-logger")
                inceptionYear.set("2022")

                developers {
                    developer {
                        id.set(developerId)
                        name.set(developerName)
                        email.set(developerEmail)
                    }
                }

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://opensource.org/licenses/Apache-2.0")
                    }
                }

                scm {
                    connection.set("scm:git:git:github.com/Numichi/reactive-logger.git")
                    developerConnection.set("scm:git:ssh://github.com/Numichi/reactive-logger.git")
                    url.set("https://github.com/Numichi/reactive-logger")
                }
            }
        }
    }

    repositories {
        maven {
            name = "OSSRH"
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }

            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            val isReleaseVersion = !version.toString().contains(Regex("(SNAPSHOT|BETA|ALPHA|DEVELOP|DEV)"))

            url = if (isReleaseVersion) {
                uri(releasesRepoUrl)
            } else {
                uri(snapshotsRepoUrl)
            }
        }
    }
}

signing {
    sign(publishing.publications["main"])
}

tasks.build {
    dependsOn("formatKotlin")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

jacoco {
    toolVersion = "0.8.11"
}

val jacocoIgnorePath = listOf(
    // unimportant
    "io/github/numichi/reactive/logger/coroutine/extend/**",
    "io/github/numichi/reactive/logger/reactor/extend/**",
    "io/github/numichi/reactive/logger/spring/properties/**",
    "io/github/numichi/reactive/logger/spring/beans/helper/**",
    "io/github/numichi/reactive/logger/spring/handler/**",
)

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.85".toBigDecimal()
            }
        }
    }
}

tasks.jacocoTestReport {
    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it) {
            exclude(jacocoIgnorePath)
        }
    }))

    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}