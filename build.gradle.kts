plugins {
    kotlin("multiplatform") version "1.4.30"
    kotlin("plugin.serialization") version "1.4.30"
    `maven-publish`
}

group = "com.github.ageofwar"
version = "1.4"

repositories {
    mavenCentral()
    jcenter()
}

kotlin {
    jvm() {
        withJava()
        compilations {
            val main by getting
            tasks {
                val jvmJar by getting(org.gradle.jvm.tasks.Jar::class) {
                    archiveAppendix.set("")
                }
                val metadataJar by getting(org.gradle.jvm.tasks.Jar::class) {
                    archiveAppendix.set("")
                    archiveClassifier.set("metadata")
                }
                val jvmSourcesJar by getting(org.gradle.jvm.tasks.Jar::class) {
                    archiveAppendix.set("")
                    archiveClassifier.set("sources")
                }
                val fatJar by registering(org.gradle.jvm.tasks.Jar::class) {
                    archiveClassifier.set("fat-with-kotlin-stdlib")
                    from(main.output.classesDirs, main.compileDependencyFiles.map { if (it.isDirectory) it else zipTree(it) })
                    with(jvmJar as CopySpec)
                }
                val fatJarWithoutKotlinStdlib by registering(org.gradle.jvm.tasks.Jar::class) {
                    archiveClassifier.set("fat")
                    from(main.output.classesDirs, main.compileDependencyFiles.filter { !it.name.startsWith("kotlin-stdlib") }.map { if (it.isDirectory) it else zipTree(it) })
                    with(jvmJar as CopySpec)
                }
                jvmJar.dependsOn(fatJar, fatJarWithoutKotlinStdlib, jvmSourcesJar)
                artifacts {
                    add("archives", fatJarWithoutKotlinStdlib)
                    add("archives", jvmSourcesJar)
                    add("archives", metadataJar)
                }
            }
        }
    }
    //mingwX64()
    //linuxX64()

    sourceSets {
        val kotlinxSerializationVersion = "1.1.0"
        val ktorVersion = "1.5.1"

        all {
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
        }

        val commonMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
                api("io.ktor:ktor-client-core:$ktorVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        /*val mingwX64Main by getting {
            dependencies {
                api("io.ktor:ktor-client-curl:$ktorVersion")
            }
        }
        val linuxX64Main by getting {
            dependencies {
                api("io.ktor:ktor-client-curl:$ktorVersion")
            }
        }*/
        val jvmMain by getting {
            dependencies {
                api("io.ktor:ktor-client-apache:$ktorVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
    }
}
