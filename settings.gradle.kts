rootProject.name = "PiRadio"

dependencyResolutionManagement {
    versionCatalogs {
        create("kpiLibs") {
            from(files("libs.versions.toml"))
        }
    }
}
