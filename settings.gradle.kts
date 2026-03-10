import java.util.Properties

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
fun getGithubCredentials(): Pair<String, String> {
    fun getRootDir(p: Project): String =
        if (p.parent == null) p.projectDir.absolutePath
        else getRootDir(p.parent!!)

    val propsFileName = "${layout.rootDirectory}/local.properties"
    val propsFile = file(propsFileName)
    val localProps = Properties().apply {
        if (propsFile.exists()) {
            propsFile.inputStream().use { load(it) }
        }
    }
    fun loadProp(key: String): String? {
        return localProps.getProperty(key) ?: System.getenv("ORG_GRADLE_PROJECT_$key")
    }

    val githubUser = loadProp("githubUsername") ?: throw IllegalStateException("Cannot load github username")
    val githubPass = loadProp("githubPassword") ?: throw IllegalStateException("Cannot load github password")

    return githubUser to githubPass
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/icure/charix")
            credentials {
                val (githubUsername, githubPassword) = getGithubCredentials()
                username = githubUsername
                password = githubPassword
            }
        }
    }
}

rootProject.name = "ksp-json-processor"
include(":library")
