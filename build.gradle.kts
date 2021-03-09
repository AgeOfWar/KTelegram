plugins {
    kotlin("multiplatform") version "1.4.30"
    kotlin("plugin.serialization") version "1.4.30"
    maven
}

group = "com.github.ageofwar"
version = "0.2"

repositories {
    mavenCentral()
    jcenter()
}

kotlin {
    jvm() {
        withJava()
        compilations {
            val main by getting
            val jvmJar by tasks.getting(org.gradle.jvm.tasks.Jar::class) {
                archiveAppendix.set("")
            }
            tasks {
                register<org.gradle.jvm.tasks.Jar>("buildFatJar") {
                    archiveBaseName.set("${project.name}-fat-with-kotlin-stdlib")
                    from(main.output.classesDirs, main.compileDependencyFiles.map { if (it.isDirectory) it else zipTree(it) })
                    with(jvmJar as CopySpec)
                }
                register<org.gradle.jvm.tasks.Jar>("buildFatJarWithoutKotlinStdlib") {
                    archiveBaseName.set("${project.name}-fat")
                    from(main.output.classesDirs, main.compileDependencyFiles.filter { !it.name.startsWith("kotlin-stdlib") }.map { if (it.isDirectory) it else zipTree(it) })
                    with(jvmJar as CopySpec)
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
                implementation("io.ktor:ktor-client-core:$ktorVersion")
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
                implementation("io.ktor:ktor-client-curl:$ktorVersion")
            }
        }
        val linuxX64Main by getting {
            dependencies {
                implementation("io.ktor:ktor-client-curl:$ktorVersion")
            }
        }*/
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-apache:$ktorVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
    }
}
