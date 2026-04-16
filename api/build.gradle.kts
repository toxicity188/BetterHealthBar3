plugins {
    alias(libs.plugins.conventions.paper)
    `maven-publish`
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }
}