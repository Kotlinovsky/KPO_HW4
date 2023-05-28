plugins {
    kotlin("jvm") version "1.8.0"
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "io.kotlinovsky.restaurant"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":framework_core"))

    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

kotlin {
    jvmToolchain(19)
}

tasks.withType<Test> {
    useJUnitPlatform()
}