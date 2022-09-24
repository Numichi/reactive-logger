import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    jacoco
    kotlin("jvm") version "1.7.10"
    id("com.adarshr.test-logger") version "3.2.0"
    id("java-library")
    id("maven-publish")
    id("signing")
}

group = project.property("group") as String
version = project.property("version") as String

val compileTestKotlin: KotlinCompile by tasks
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

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.projectreactor:reactor-core:3.4.23")
    implementation("org.slf4j:slf4j-api:2.0.1")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.1.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.4")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("org.yaml:snakeyaml:1.32") // for Spring Boot Starter vulnerability resolver
    implementation("org.springframework.boot:spring-boot-starter:2.7.4")
    api("io.github.microutils:kotlin-logging-jvm:3.0.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.4")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testImplementation("io.projectreactor:reactor-test:3.4.23")
    testImplementation("org.apache.logging.log4j:log4j-core:2.19.0")
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.19.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("io.mockk:mockk:1.12.8")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

repositories {
    mavenCentral()
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

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

//tasks.getting(KotlinCompile::class) {
//    kotlinOptions {
//        jvmTarget = "11"
//    }
//}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

jacoco {
    toolVersion = "0.8.7"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}