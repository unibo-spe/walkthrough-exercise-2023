plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
    id("com.gradle.enterprise") version "3.15.1"
}

rootProject.name = "spe-greetings"

gradleEnterprise {
    buildScan {
        publishOnFailure()
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}
