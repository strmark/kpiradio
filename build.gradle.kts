import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlin_version = "1.5.10"
    `version-catalog`
    id("org.springframework.boot") version "2.5.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.flywaydb.flyway") version "7.9.1"
    id("com.github.ben-manes.versions") version "0.38.0"
    kotlin("jvm") version kotlin_version
    kotlin("plugin.spring") version kotlin_version
    kotlin("plugin.jpa") version kotlin_version
    kotlin("plugin.serialization") version kotlin_version
}

group = "nl.strmark"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_15

repositories {
    mavenCentral()
}

dependencies {
    val versions = kpi.versions
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jetty")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(module = "spring-boot-starter-tomcat")
    }
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${versions.jackson.get()}")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.springfox:springfox-swagger-ui:${versions.swagger.get()}")
    implementation("io.springfox:springfox-boot-starter:${versions.swagger.get()}")
    implementation("io.github.microutils:kotlin-logging:${versions.klogging.get()}")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("com.h2database:h2:${versions.h2db.get()}")
    implementation("org.flywaydb:flyway-core:${versions.flyway.get()}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${versions.kotlinx.get()}")
    implementation("uk.co.caprica:vlcj:${versions.vlcj.get()}")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:${versions.junit.get()}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "15"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

flyway {
    url = "jdbc:h2:file:./database/piradio"
    user = "pi"
    password = "pi"
}
