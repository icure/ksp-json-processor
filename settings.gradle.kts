import java.util.Properties

val localPropertiesFile = file("local.properties")
val properties = Properties()

if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { properties.load(it) }
}

// Now you can read the properties
val githubUsername: String = properties.getProperty("githubUsername") ?: throw IllegalArgumentException("githubUsername is not set")
val githubPassword: String = properties.getProperty("githubPassword") ?: throw IllegalArgumentException("githubPassword is not set")

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
