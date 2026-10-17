import org.gradle.api.credentials.HttpHeaderCredentials
import org.gradle.authentication.http.HttpHeaderAuthentication

rootProject.name = "pixelboard-patches"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/MorpheApp/registry")

            // Gradle's Basic auth base64-encodes username:password. In Codex Cloud,
            // network secrets are placeholders and must survive unchanged into an
            // HTTPS request so the network proxy can substitute the real token.
            //
            // Keep explicit local gpr.key usable via GitHub's documented Basic
            // authentication. Otherwise use the GitHub token as a literal HTTP
            // Authorization header (Bearer TOKEN_PLACEHOLDER).
            val explicitKey = providers.gradleProperty("gpr.key").orNull
            if (!explicitKey.isNullOrBlank()) {
                credentials {
                    username = providers.gradleProperty("gpr.user").orNull
                        ?: System.getenv("GITHUB_ACTOR") ?: ""
                    password = explicitKey
                }
            } else {
                val token = System.getenv("GITHUB_TOKEN")
                if (!token.isNullOrBlank()) {
                    credentials(HttpHeaderCredentials::class) {
                        name = "Authorization"
                        value = "Bearer $token"
                    }
                    authentication {
                        create<HttpHeaderAuthentication>("header")
                    }
                } else {
                    // No credentials were provided; retain regular Gradle
                    // resolution errors rather than embedding any secrets.
                    credentials {
                        username = providers.gradleProperty("gpr.user").orNull
                            ?: System.getenv("GITHUB_ACTOR") ?: ""
                        password = ""
                    }
                }
            }
        }
        maven { url = uri("https://jitpack.io") }
    }
}

plugins {
    id("app.morphe.patches") version "1.3.3"
}
