import kr.entree.spigradle.kotlin.*

plugins {
    java
    id("kr.entree.spigradle") version "2.4.2"
}

repositories {
    papermc {
        content {
            includeGroup("com.destroystokyo.paper")
        }
    }
//    spigotmc()
//    sonatype()
    jitpack()
    mavenCentral()
}

dependencies {
//    compileOnly(spigot("1.18.2-R0.1-SNAPSHOT"))
    compileOnly(paper("1.18.2-R0.1-SNAPSHOT"))
    compileOnly(vaultAll()) {
        isTransitive = false
    }
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
