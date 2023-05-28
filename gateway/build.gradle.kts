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
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.1.0")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2022.0.3")
    }
}

kotlin {
    jvmToolchain(19)
}

tasks.withType<Test> {
    useJUnitPlatform()
}