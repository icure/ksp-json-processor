import java.util.Properties

gradle.beforeProject {
    val propsFile = file("local.properties")
    val localProps = Properties().apply {
        if (propsFile.exists()) {
            propsFile.inputStream().use { load(it) }
        }
    }

    fun loadProp(key: String): String? {
        return System.getenv(key) ?: localProps.getProperty(key)
    }

    val githubUser = loadProp("GITHUB_USERNAME")
    val githubPass = loadProp("GITHUB_PASSWORD")

    if (githubUser != null) {
        this.extensions.extraProperties.set("githubUsername", githubUser)
    }
    if (githubPass != null) {
        this.extensions.extraProperties.set("githubPassword", githubPass)
    }
}

val githubUsername by extra("githubUsername")
val githubPassword by extra("githubPassword")

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
