import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project

const val JAVA_VERSION = 25

val Project.libs
    get() = rootProject.extensions.getByName("libs") as LibrariesForLibs