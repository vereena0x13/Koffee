plugins {
    kotlin("jvm") version "2.0.20"
    `maven-publish`
}

group = "codes.som"
version = "8.0.2"

repositories {
    mavenCentral()
}

dependencies {
    arrayOf("asm", "asm-tree", "asm-commons").forEach {
        implementation("org.ow2.asm:$it:9.7.1")
    }

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

kotlin {
    explicitApi()
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    repositories {
        maven("$buildDir/repo")
    }

    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
        }
    }
}
