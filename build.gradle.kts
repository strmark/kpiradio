import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    with(kpiLibs.versions) {
        id("org.springframework.boot") version springboot
        id("io.spring.dependency-management") version dependencymanagement
        id("com.github.ben-manes.versions") version manes
        id("org.owasp.dependencycheck") version owasp
        id("org.sonarqube") version sonarqube
        kotlin("jvm") version kotlin
        kotlin("plugin.spring") version kotlin
        kotlin("plugin.jpa") version kotlin
    }
}

allprojects {
    group = "nl.strmark"
    version = "0.0.1-SNAPSHOT"
    java.sourceCompatibility = JavaVersion.VERSION_17

    repositories {
        mavenCentral()
    }

    with(kpiLibs.versions) {
        dependencies {
            implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${jackson.get()}")
            implementation("com.h2database:h2:${h2db.get()}")
            implementation("io.github.oshai:kotlin-logging-jvm:${klogging.get()}")
            implementation("org.flywaydb:flyway-core:${flyway.get()}")
            implementation("org.flywaydb:flyway-community-db-support:${flyway.get()}")
            implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlin.get()}")
            implementation("org.jetbrains.kotlin:kotlin-stdlib:${kotlin.get()}")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${kotlinx.get()}")
            implementation("org.springframework.boot:spring-boot-starter-data-jpa:${springboot.get()}")
            implementation("org.springframework.boot:spring-boot-starter-validation:${springboot.get()}")
            implementation("org.springframework.boot:spring-boot-starter-web:${springboot.get()}")
            implementation("org.springframework.boot:spring-boot-starter-jetty:${springboot.get()}")
            modules {
                module("org.springframework.boot:spring-boot-starter-tomcat") {
                    replacedBy("org.springframework.boot:spring-boot-starter-jetty", "Use Jetty instead of Tomcat")
                }
            }
            implementation("org.jobrunr:jobrunr-spring-boot-3-starter:${jobrunr.get()}")
            implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${swagger.get()}")

            implementation("org.yaml:snakeyaml:${snakeyaml.get()}")

            developmentOnly("org.springframework.boot:spring-boot-devtools")
            testCompileOnly("org.junit.jupiter:junit-jupiter-api:${junit.get()}")
            testCompileOnly("org.junit.jupiter:junit-jupiter-engine:${junit.get()}")
            testImplementation("org.springframework.boot:spring-boot-starter-test:${springboot.get()}")
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Wrapper> {
    gradleVersion = "8.5"
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}

dependencyCheck {
    analyzers.assemblyEnabled = false
    analyzers.retirejs.enabled = false
}

sonarqube {
    properties {
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.organization", "strmark")
        property("sonar.projectKey", "strmark_kpiradio")
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}
