import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kpiLibs.versions.let { versions ->
        id("org.springframework.boot") version versions.springboot.get()
        id("io.spring.dependency-management") version versions.dependencymanagement.get()
        id("org.flywaydb.flyway") version versions.flyway.get()
        id("com.github.ben-manes.versions") version versions.manes.get()
        id("org.sonarqube") version versions.sonarqube.get()
        id("org.owasp.dependencycheck") version versions.owasp.get()
        kotlin("jvm") version versions.kotlin.get()
        kotlin("plugin.spring") version versions.kotlin.get()
        kotlin("plugin.jpa") version versions.kotlin.get()
    }
}

allprojects {
    group = "nl.strmark"
    version = "0.0.1-SNAPSHOT"
    java.sourceCompatibility = JavaVersion.VERSION_17

    repositories {
        mavenCentral()
    }

    kpiLibs.versions.let { versions ->
        dependencies {
            implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${versions.jackson.get()}")
            implementation("com.h2database:h2:${versions.h2db.get()}")
            implementation("io.github.microutils:kotlin-logging:${versions.klogging.get()}")
            implementation("org.flywaydb:flyway-core:${versions.flyway.get()}")
            implementation("org.jetbrains.kotlin:kotlin-reflect:${versions.kotlinversion.get()}")
            implementation("org.jetbrains.kotlin:kotlin-stdlib:${versions.kotlinversion.get()}")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${versions.kotlinx.get()}")
            implementation("org.springframework.boot:spring-boot-starter-data-jpa:${versions.springboot.get()}")
            implementation("org.springframework.boot:spring-boot-starter-jetty:${versions.springboot.get()}")
            implementation("org.springframework.boot:spring-boot-starter-quartz:${versions.springboot.get()}")
            implementation("org.springframework.boot:spring-boot-starter-validation:${versions.springboot.get()}")
            implementation("org.springframework.boot:spring-boot-starter-web:${versions.springboot.get()}")
//  jetty not working fine with test and bootRun
            {
                exclude(module = "spring-boot-starter-tomcat")
            }
            implementation("org.yaml:snakeyaml:${versions.snakeyaml.get()}")
            developmentOnly("jakarta.servlet:jakarta.servlet-api:${versions.jakarta.get()}")
            developmentOnly("org.springframework.boot:spring-boot-devtools")
            implementation("org.springdoc:springdoc-openapi-kotlin-tests:${versions.swagger.get()}")
            implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${versions.swagger.get()}")
            testCompileOnly("org.junit.jupiter:junit-jupiter-api:${versions.junit.get()}")
            testCompileOnly("org.junit.jupiter:junit-jupiter-engine:${versions.junit.get()}")
            testImplementation("org.springframework.boot:spring-boot-starter-test:${versions.springboot.get()}")
        }
    }
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
    gradleVersion = "8.1.1"
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
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

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}
