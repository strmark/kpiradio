rootProject.name = "Pi Radio"

enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    versionCatalogs {
        create("kpi") {
            from(files("libs.versions.toml"))
        }
    }
}