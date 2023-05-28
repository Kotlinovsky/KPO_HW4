plugins {
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.21"
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "io.kotlinovsky.restaurant"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":framework_auth"))
    implementation(project(":framework_core"))

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    runtimeOnly("com.h2database:h2")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
}

kotlin {
    jvmToolchain(19)
}

tasks.withType<Test> {
    useJUnitPlatform()
}