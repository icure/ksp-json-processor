import java.util.Properties

val localProps = Properties().apply {
    val propsFile = file("local.properties")
    if (propsFile.exists()) {
        propsFile.inputStream().use { load(it) }
    }
}

fun loadProp(key: String): String? {
    return System.getenv(key) ?: localProps.getProperty(key)
}

val githubUsername = loadProp("githubUsername")
val githubPassword = loadProp("githubPassword")

if (githubUsername != null) {
    this.extensions.extraProperties.set("githubUsername", githubUsername)
}
if (githubPassword != null) {
    this.extensions.extraProperties.set("githubPassword", githubPassword)
}

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/icure/charix")
            credentials {
                username = githubUsername
                password = githubPassword
            }
        }
    }
}

rootProject.name = "ksp-json-processor"
include(":library")
