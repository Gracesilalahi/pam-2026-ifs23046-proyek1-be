import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val dotenv_version: String by project

plugins {
    kotlin("jvm") version "2.3.0"
    id("io.ktor.plugin") version "3.4.0"
    kotlin("plugin.serialization") version "2.3.0"
}

group = "org.delcom"
version = "0.0.1"

application {
    mainClass = "org.delcom.ApplicationKt"
}

// FORCE: Matikan sinkronisasi target JVM yang bikin bentrok
kotlin {
    jvmToolchain(21)
}

// FORCE: Matikan semua deteksi warning di tingkat kompilasi
tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        allWarningsAsErrors.set(false)
        // Flag tambahan untuk memaksa compiler mengabaikan deprecation
        freeCompilerArgs.add("-Xsuppress-version-warnings")
    }
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-config-yaml")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    // Database (PostgreSQL & H2)
    implementation("org.postgresql:postgresql:42.7.9")
    implementation("com.h2database:h2:2.3.232")

    // Exposed - SEMUA HARUS 0.61.0
    val exposed_version = "0.61.0"
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")

    // Koin & Extras
    implementation("io.insert-koin:koin-ktor:4.1.2-Beta1")
    implementation("io.insert-koin:koin-logger-slf4j:4.1.2-Beta1")
    implementation("io.ktor:ktor-server-host-common:3.4.0")
    implementation("io.ktor:ktor-server-status-pages:3.4.0")

    // Security
    implementation("io.github.cdimascio:dotenv-kotlin:${dotenv_version}")
    implementation("io.ktor:ktor-server-auth:${ktor_version}")
    implementation("io.ktor:ktor-server-auth-jwt:${ktor_version}")
    implementation("org.mindrot:jbcrypt:0.4")

    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}