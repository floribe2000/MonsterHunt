import kr.entree.spigradle.kotlin.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("kr.entree.spigradle") version "2.4.2"
    kotlin("jvm") version "1.6.21"
    idea
}

repositories {
    spigotmc()
    sonatype()
    jitpack()
    mavenCentral()
}

dependencies {
    compileOnly(spigot("1.18.2-R0.1-SNAPSHOT"))
    compileOnly(vaultAll()) {
        isTransitive = false
    }
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")
    testImplementation(kotlin("test-junit5"))
    testImplementation(spigot("1.18.2-R0.1-SNAPSHOT"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.test {
    useJUnitPlatform()
}

spigot {
    name = "Monsterhunt"
    softDepends("Vault")
    version = "1.5.1"
    commands {
        create("hunt") {
            description = "Sign up for the hunt"
        }
        create("huntstatus") {
            description = "Check the hunt status"
        }
        create("huntscore") {
            description = "Check your current high score"
        }
        create("huntstart") {
            description = "Start hunt manually"
        }
        create("huntstop") {
            description = "Stop hunt manually"
        }
        create("huntzone") {
            description = "Select hunt zone"
        }
        create("hunttele") {
            description = "Teleport to the hunt zone"
        }
    }
    permissions {
        create("monsterhunt.*") {
            children = mapOf(
                "monsterhunt.admincmd.*" to true,
                "monsterhunt.usercmd.*" to true,
                "monsterhunt.rewardeverytime" to true,
                "monsterhunt.noteleportrestrictions" to true
            )
        }
        create("monsterhunt.admincmd.*") {
            children = mapOf(
                "monsterhunt.admincmd.huntstart" to true,
                "monsterhunt.admincmd.huntstop" to true,
                "monsterhunt.admincmd.huntzone" to true
            )
        }
        create("monsterhunt.usercmd.*") {
            children = mapOf(
                "monsterhunt.usercmd.hunt" to true,
                "monsterhunt.usercmd.huntscore" to true,
                "monsterhunt.usercmd.huntstatus" to true,
                "monsterhunt.usercmd.hunttele" to true
            )
        }
        create("monsterhunt.admincmd.huntstart") {
            defaults = "op"
        }
        create("monsterhunt.admincmd.huntstop") {
            defaults = "op"
        }
        create("monsterhunt.admincmd.huntzone") {
            defaults = "op"
        }
        create("monsterhunt.usercmd.hunt") {
            defaults = "true"
        }
        create("monsterhunt.usercmd.huntscore") {
            defaults = "true"
        }
        create("monsterhunt.usercmd.huntstatus") {
            defaults = "true"
        }
        create("monsterhunt.usercmd.hunttele") {
            defaults = "true"
        }
        create("monsterhunt.rewardeverytime") {
            defaults = "false"
        }
        create("monsterhunt.noteleportrestrictions") {
            defaults = "op"
        }
    }
}
