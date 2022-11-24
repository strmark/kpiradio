import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.7.21"
    `version-catalog`
    id("org.springframework.boot") version "2.7.6"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.flywaydb.flyway") version "9.8.3"
    id("com.github.ben-manes.versions") version "0.44.0"
    id("org.sonarqube") version "3.5.0.2730"
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
}

dependencies {
    val versions = kpiLibs.versions
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:${versions.springboot.get()}")
    implementation("org.springframework.boot:spring-boot-starter-jetty:${versions.springboot.get()}")
    implementation("org.springframework.boot:spring-boot-starter-quartz:${versions.springboot.get()}")
    implementation("org.springframework.boot:spring-boot-starter-web:${versions.springboot.get()}") {
        exclude(module = "spring-boot-starter-tomcat")
    }
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${versions.kotlinversion.get()}")
    implementation("org.springdoc:springdoc-openapi-kotlin:${versions.swagger.get()}")
    implementation("org.springdoc:springdoc-openapi-ui:${versions.swagger.get()}")
    implementation("io.github.microutils:kotlin-logging:${versions.klogging.get()}")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("com.fasterxml.jackson:jackson-bom:${versions.jackson.get()}")
    implementation("com.h2database:h2:${versions.h2db.get()}")
    implementation("org.flywaydb:flyway-core:${versions.flyway.get()}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${versions.kotlinx.get()}")
    implementation("com.h2database:h2:${versions.h2db.get()}")
    testImplementation("org.springframework.boot:spring-boot-starter-test:${versions.springboot.get()}") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testCompileOnly("org.junit.jupiter:junit-jupiter-api:${versions.junit.get()}")
    testCompileOnly("org.junit.jupiter:junit-jupiter-engine:${versions.junit.get()}")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
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
    gradleVersion = "7.5.1"
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
