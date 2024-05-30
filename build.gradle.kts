plugins {
    kotlin("jvm") version "1.9.23"
    `java-library`
    `maven-publish`
}

var sGroupId = "ru.oklookat"
val sArtifactId = "unidecode4k"
val sVersion = "1.0.0"

group = sGroupId
version = sVersion

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}

java {
    withSourcesJar()
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        )
    }
}

configure<PublishingExtension> {
    publications.create<MavenPublication>(sArtifactId) {
        groupId = sGroupId
        artifactId = sArtifactId
        version = sVersion
        pom {
            packaging = "jar"
            name = sArtifactId
        }
        artifact("${layout.buildDirectory}/libs/$artifactId-$version.jar") { classifier = "jar" }
        artifact("${layout.buildDirectory}/libs/$artifactId-$version-sources.jar") { classifier = "sources" }
    }
}