rootProject.name = "PiRadio"

dependencyResolutionManagement {
    versionCatalogs {
        create("kpiLibs") {
            from(files("libs.versions.toml"))
        }
    }
}

pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        gradlePluginPortal()
    }
}
