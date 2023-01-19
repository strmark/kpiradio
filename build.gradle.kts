import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.8.0"
    `version-catalog`
    id("org.springframework.boot") version "3.0.1"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.flywaydb.flyway") version "9.12.0"
    id("com.github.ben-manes.versions") version "0.44.0"
    id("org.sonarqube") version "3.5.0.2730"
    id("org.owasp.dependencycheck") version "8.0.1"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
}

group = "nl.strmark"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
    val versions = kpiLibs.versions
    implementation("com.fasterxml.jackson:jackson-bom:${versions.jackson.get()}")
    implementation("com.h2database:h2:${versions.h2db.get()}")
    implementation("io.github.microutils:kotlin-logging:${versions.klogging.get()}")
    // managed for jetty
    implementation("jakarta.servlet:jakarta.servlet-api:${versions.jakarta.get()}")
    implementation("org.flywaydb:flyway-core:${versions.flyway.get()}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${versions.kotlinversion.get()}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${versions.kotlinversion.get()}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${versions.kotlinx.get()}")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:${versions.springboot.get()}")
    implementation("org.springframework.boot:spring-boot-starter-jetty:${versions.springboot.get()}")
    implementation("org.springframework.boot:spring-boot-starter-quartz:${versions.springboot.get()}")
    implementation("org.springframework.boot:spring-boot-starter-validation:${versions.springboot.get()}")
    implementation("org.springframework.boot:spring-boot-starter-web:${versions.springboot.get()}") {
        exclude(module = "spring-boot-starter-tomcat")
    }
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.springdoc:springdoc-openapi-kotlin-tests:${versions.swagger.get()}")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${versions.swagger.get()}")
    testCompileOnly("org.junit.jupiter:junit-jupiter-api:${versions.junit.get()}")
    testCompileOnly("org.junit.jupiter:junit-jupiter-engine:${versions.junit.get()}")
    testImplementation("org.springframework.boot:spring-boot-starter-test:${versions.springboot.get()}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Wrapper> {
    gradleVersion = "7.6"
}

flyway {
    url = "jdbc:h2:file:./database/piradio"
    user = "pi"
    password = "pi"
    ignoreMigrationPatterns = listOf("repeatable:missing").toTypedArray()
}

sonarqube {
    properties {
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.organization", "strmark")
        property("sonar.projectKey", "strmark_kpiradio")
    }
}

dependencyCheck {
    analyzers.assemblyEnabled = false
}

apply(plugin = "org.owasp.dependencycheck")
