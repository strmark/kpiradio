rootProject.name = "PiRadio"

enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    versionCatalogs {
        create("kpi") {
            from(files("libs.versions.toml"))
        }
    }
}
