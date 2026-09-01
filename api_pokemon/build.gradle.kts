plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.devtools.ksp")
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    implementation(libs.kotlinx.serialization.core)
    api(libs.okhttp)
    api(libs.retrofit)

    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.dagger)
    ksp(libs.dagger.processor)

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

    implementation(project(":core"))
    implementation(project(":api_pokemon:dao"))
}
