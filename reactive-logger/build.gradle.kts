plugins {
    java
    jacoco
    kotlin("jvm")
    id("com.adarshr.test-logger")
    id("java-library")
    id("maven-publish")
    id("signing")
}

group = project.property("group") as String
version = project.property("version") as String

val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileTestKotlin.kotlinOptions.jvmTarget = "11"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
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

    implementation("io.projectreactor:reactor-core:3.5.0")
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("org.springframework.boot:spring-boot-starter:2.7.6")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.4")
    api("io.github.microutils:kotlin-logging-jvm:3.0.4")

    testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.6")
    testImplementation("io.projectreactor:reactor-test:3.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
    testImplementation("org.apache.logging.log4j:log4j-core:2.19.0")
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.19.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("io.mockk:mockk:1.13.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.1")
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
            groupId = project.property("group") as String
            artifactId = "reactive-logger"
            version = project.property("version") as String
            from(components["java"])

            pom {
                name.set("Reactive logger layer for slf4j")
                description.set("A Java & Kotlin library adapting slf4j for reactive applications")
                url.set("https://github.com/Numichi/reactive-logger")
                inceptionYear.set("2022")

                developers {
                    developer {
                        id.set(project.property("developerId") as String)
                        name.set(project.property("developerName") as String)
                        email.set(project.property("developerEmail") as String)
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
                    project.property("ossrhPassword") as String
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