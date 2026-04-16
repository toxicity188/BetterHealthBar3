plugins {
    id("conventions-standard")
}

val shade = configurations.getByName("shade")

dependencies {
    shade(project(":api")) { isTransitive = false }
}