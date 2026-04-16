plugins {
    id("conventions-standard")
}

dependencies {
    implementation("io.papermc.paper:paper-api:${property("minecraft_version")}.build.+")
    api(libs.bundles.library.download)
}