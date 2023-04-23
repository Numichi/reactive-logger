plugins {
    java
    jacoco
    kotlin("jvm")
    kotlin("kapt")
    id("com.adarshr.test-logger")
    id("java-library")
    id("maven-publish")
    id("signing")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = project.property("group") as String
version = project.property("version") as String

val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileTestKotlin.kotlinOptions.jvmTarget = "17"

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

    implementation("io.projectreactor:reactor-core:3.5.5")
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("org.springframework.boot:spring-boot-starter:3.0.6") {
        exclude("org.yaml", "snakeyaml") // CVE-2022-1471
        exclude("org.springframework", "spring-expression ") // CVE-2023-20861
    }
    implementation("org.springframework:spring-expression:6.0.8")
    implementation("org.yaml:snakeyaml:2.0")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.4")
    api("io.github.microutils:kotlin-logging-jvm:3.0.5")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.0.6")
    kapt("org.springframework.boot:spring-boot-configuration-processor:3.0.6")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.0.6")
    testImplementation("io.projectreactor:reactor-test:3.5.5")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("org.apache.logging.log4j:log4j-core:2.20.0")
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("io.mockk:mockk:1.13.5")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
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
                        id.set(project.property("developerId") as String? ?: "")
                        name.set(project.property("developerName") as String? ?: "")
                        email.set(project.property("developerEmail") as String? ?: "")
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
                username = if (project.hasProperty("ossrhUsername")) {
                    project.property("ossrhUsername") as String
                } else {
                    "N/A"
                }

                password = if (project.hasProperty("ossrhPassword")) {
                    project.property("ossrhPassword") as String? ?: ""
                } else {
                    "N/A"
                }
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

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

jacoco {
    toolVersion = "0.8.7"
}

val jacocoIgnorePath = listOf(
    // unimportant
    "io/github/numichi/reactive/logger/spring/properties/**",
    "io/github/numichi/reactive/logger/spring/beans/helper/**",
    "io/github/numichi/reactive/logger/spring/handler/**",
)

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.9".toBigDecimal()
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