import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.JavaVersion.VERSION_17
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

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
    java.sourceCompatibility = VERSION_17

    repositories {
        mavenCentral()
    }

    with(kpiLibs.versions) {
        dependencies {
            implementation("com.h2database:h2:${h2db.get()}")
            implementation("io.github.oshai:kotlin-logging-jvm:${klogging.get()}")
            implementation("org.flywaydb:flyway-core:${flyway.get()}")
            implementation("org.flywaydb:flyway-community-db-support:${flywaydb.get()}")
            implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlin.get()}")
            implementation("org.jetbrains.kotlin:kotlin-stdlib:${kotlin.get()}")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${kotlinx.get()}")
            implementation("org.springframework.boot:spring-boot-starter-actuator:${springboot.get()}")
            implementation("org.springframework.boot:spring-boot-starter-data-jpa:${springboot.get()}")
            implementation("org.springframework.boot:spring-boot-starter-validation:${springboot.get()}")
            implementation("org.springframework.boot:spring-boot-starter-webmvc:${springboot.get()}")
            implementation("org.springframework.boot:spring-boot-starter-jetty:${springboot.get()}")
            implementation("org.jobrunr:jobrunr-spring-boot-4-starter:${jobrunr.get()}")
            implementation("org.jobrunr:jobrunr-kotlin-2.1-support:${jobrunr.get()}")
            implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${swagger.get()}")
            implementation("org.yaml:snakeyaml:${snakeyaml.get()}")
            implementation("tools.jackson.module:jackson-module-kotlin")

            developmentOnly("org.springframework.boot:spring-boot-devtools")
            testImplementation("org.springframework.boot:spring-boot-starter-test:${springboot.get()}")
            testImplementation("org.springframework.boot:spring-boot-starter-flyway-test:${springboot.get()}")
            testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test:${springboot.get()}")
            testImplementation("org.springframework.boot:spring-boot-starter-validation-test:${springboot.get()}")
            testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test:${springboot.get()}")
            testRuntimeOnly("org.junit.platform:junit-platform-launcher")
        }
    }
}

tasks.withType<KotlinJvmCompile> {
    compilerOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xannotation-default-target=param-property")
        jvmTarget = JVM_17
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Wrapper> {
    gradleVersion = "9.3.1"
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}

dependencyCheck {
    analyzers.assemblyEnabled = false
    analyzers.retirejs.enabled = false
    nvd.apiKey = System.getenv("NVDAPIKEY")
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

