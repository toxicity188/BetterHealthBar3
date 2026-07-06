plugins {
    id("conventions-standard")
}

dependencies {
    implementation("io.papermc.paper:paper-api:26.1.2.build.+")
    api(libs.bundles.library.download)
    compileOnly(libs.adventure.platform.bukkit) {
        exclude(group = "net.kyori")
    }
}