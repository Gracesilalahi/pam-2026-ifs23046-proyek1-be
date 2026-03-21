import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val dotenv_version: String = "6.5.1" // Kita kunci di versi yang sudah pasti ada

plugins {
    kotlin("jvm") version "2.3.0" // Mengikuti log laptopmu
    id("io.ktor.plugin") version "3.4.0" // Mengikuti log laptopmu
    kotlin("plugin.serialization") version "2.3.0"
}

group = "org.delcom"
version = "0.0.1"

application {
    mainClass.set("org.delcom.ApplicationKt")
}

repositories {
    mavenCentral() // WAJIB: Biar Gradle tahu cari library di mana
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        allWarningsAsErrors.set(false)
        freeCompilerArgs.add("-Xsuppress-version-warnings")
    }
}

dependencies {
    // Ktor Server
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-config-yaml")

    // Serialization (Penerjemah JSON)
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")

    // Database
    val exposed_version = "0.61.0"
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
    implementation("org.postgresql:postgresql:42.7.9")
    implementation("com.h2database:h2:2.3.232")

    // Koin
    implementation("io.insert-koin:koin-ktor:4.1.2-Beta1")
    implementation("io.insert-koin:koin-logger-slf4j:4.1.2-Beta1")

    // Plugins & Security
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-auth-jwt")
    implementation("io.github.cdimascio:dotenv-kotlin:$dotenv_version")
    implementation("org.mindrot:jbcrypt:0.4")

    // Testing
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}