import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlin_version = "1.5.31"
    `version-catalog`
    id("org.springframework.boot") version "2.5.6"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.flywaydb.flyway") version "8.0.4"
    id("com.github.ben-manes.versions") version "0.39.0"
    id("org.sonarqube") version "3.3"
    kotlin("jvm") version kotlin_version
    kotlin("plugin.spring") version kotlin_version
    kotlin("plugin.jpa") version kotlin_version
    kotlin("plugin.serialization") version kotlin_version
}

group = "nl.strmark"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    val versions = kpi.versions
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:${versions.springboot.get()}")
    implementation("org.springframework.boot:spring-boot-starter-jetty:${versions.springboot.get()}")
    implementation("org.springframework.boot:spring-boot-starter-quartz:${versions.springboot.get()}")
    implementation("org.springframework.boot:spring-boot-starter-web:${versions.springboot.get()}") {
        exclude(module = "spring-boot-starter-tomcat")
    }
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${versions.jackson.get()}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${versions.kotlinversion.get()}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${versions.kotlinversion.get()}")
    implementation("org.springdoc:springdoc-openapi-data-rest:${versions.swagger.get()}")
    implementation("org.springdoc:springdoc-openapi-ui:${versions.swagger.get()}")
    implementation("io.github.microutils:kotlin-logging:${versions.klogging.get()}")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("com.h2database:h2:${versions.h2db.get()}")
    implementation("org.flywaydb:flyway-core:${versions.flyway.get()}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${versions.kotlinx.get()}")
    implementation("uk.co.caprica:vlcj:${versions.vlcj.get()}")
    runtimeOnly("com.h2database:h2:${versions.h2db.get()}")
    testImplementation("org.springframework.boot:spring-boot-starter-test:${versions.springboot.get()}"){
        exclude(group= "org.junit.vintage", module= "junit-vintage-engine")
    }
    testCompileOnly("org.junit.jupiter:junit-jupiter-engine:${versions.junit.get()}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "16"
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

sonarqube {
    properties {
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.organization", "strmark")
        property("sonar.projectKey", "strmark_kpiradio")
    }
}
