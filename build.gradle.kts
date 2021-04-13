import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `version-catalog`
    id("org.springframework.boot") version "2.4.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.flywaydb.flyway") version "7.7.3"
    id("com.github.ben-manes.versions") version "0.38.0"
    kotlin("jvm") version "1.4.32"
    kotlin("plugin.spring") version "1.4.32"
    kotlin("plugin.jpa") version "1.4.32"
    kotlin("plugin.serialization") version "1.4.32"
}

group = "nl.strmark"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_15

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jetty")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(module = "spring-boot-starter-tomcat")
    }
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.springfox:springfox-swagger-ui:${kpi.versions.swagger.get()}")
    implementation("io.springfox:springfox-boot-starter:${kpi.versions.swagger.get()}")
    implementation("io.github.microutils:kotlin-logging:${kpi.versions.klogging.get()}")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("com.h2database:h2:${kpi.versions.h2db.get()}")
    implementation("org.flywaydb:flyway-core:${kpi.versions.flyway.get()}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${kpi.versions.kotlinx.get()}")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:${kpi.versions.junit.get()}")
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
    url = "jdbc:h2:file:~/db/piradio"
    user = "pi"
    password = "pi"
}
