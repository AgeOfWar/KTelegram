plugins {
    kotlin("multiplatform") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    `maven-publish`
}

group = "com.github.ageofwar"
version = "1.7.7"

repositories {
    mavenCentral()
    jcenter()
}

kotlin {
    jvm {
        withJava()
        compilations {
            val main by getting
            tasks {
                val jvmJar by getting(org.gradle.jvm.tasks.Jar::class) {
                    archiveAppendix.set("")
                }
                val jvmSourcesJar by getting(org.gradle.jvm.tasks.Jar::class) {
                    archiveAppendix.set("")
                    archiveClassifier.set("sources")
                }
                val fatJar by registering(org.gradle.jvm.tasks.Jar::class) {
                    archiveClassifier.set("fat-with-kotlin-stdlib")
                    duplicatesStrategy = DuplicatesStrategy.INCLUDE
                    from(main.output.classesDirs, main.compileDependencyFiles.map { if (it.isDirectory) it else zipTree(it) })
                    with(jvmJar as CopySpec)
                }
                val fatJarWithoutKotlinStdlib by registering(org.gradle.jvm.tasks.Jar::class) {
                    archiveClassifier.set("fat")
                    duplicatesStrategy = DuplicatesStrategy.INCLUDE
                    from(main.output.classesDirs, main.compileDependencyFiles.filter { !it.name.startsWith("kotlin-stdlib") }.map { if (it.isDirectory) it else zipTree(it) })
                    with(jvmJar as CopySpec)
                }
                jvmJar.dependsOn(fatJar, fatJarWithoutKotlinStdlib, jvmSourcesJar)
                artifacts {
                    add("archives", fatJarWithoutKotlinStdlib)
                    add("archives", jvmSourcesJar)
                }
            }
        }
    }
    //mingwX64()
    //linuxX64()

    sourceSets {
        val kotlinxSerializationVersion = "1.4.0"
        val ktorVersion = "2.1.1"

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
