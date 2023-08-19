import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    with(kpiLibs.versions) {
        id("org.springframework.boot") version springboot.get()
        id("io.spring.dependency-management") version dependencymanagement.get()
        id("org.flywaydb.flyway") version flyway.get()
        id("com.github.ben-manes.versions") version manes.get()
        id("org.sonarqube") version sonarqube.get()
        id("org.owasp.dependencycheck") version owasp.get()
        kotlin("jvm") version kotlin.get()
        kotlin("plugin.spring") version kotlin.get()
        kotlin("plugin.jpa") version kotlin.get()
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
            ext["jakarta-servlet.version"] = jakarta.get()
            implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${jackson.get()}")
            implementation("com.h2database:h2:${h2db.get()}")
            implementation("io.github.microutils:kotlin-logging:${klogging.get()}")
            implementation("org.flywaydb:flyway-core:${flyway.get()}")
            implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlin.get()}")
            implementation("org.jetbrains.kotlin:kotlin-stdlib:${kotlin.get()}")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${kotlinx.get()}")
            implementation("org.springframework.boot:spring-boot-starter-data-jpa:${springboot.get()}")
            implementation("org.springframework.boot:spring-boot-starter-jetty:${springboot.get()}")
            implementation("org.springframework.boot:spring-boot-starter-quartz:${springboot.get()}")
            implementation("org.springframework.boot:spring-boot-starter-validation:${springboot.get()}")
            implementation("org.springframework.boot:spring-boot-starter-web:${springboot.get()}")
            implementation("org.springframework.boot:spring-boot-starter-jetty:${springboot.get()}")
            modules {
                module("org.springframework.boot:spring-boot-starter-tomcat") {
                    replacedBy("org.springframework.boot:spring-boot-starter-jetty", "Use Jetty instead of Tomcat")
                }
            }
            implementation("org.yaml:snakeyaml:${snakeyaml.get()}")
            developmentOnly("org.springframework.boot:spring-boot-devtools")
            implementation("org.springdoc:springdoc-openapi-kotlin-tests:${swagger.get()}")
            implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${swagger.get()}")
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
    gradleVersion = "8.3"
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
