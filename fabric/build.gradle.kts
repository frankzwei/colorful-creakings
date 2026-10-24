import me.ultrusmods.colorfulcreakings.gradle.Properties
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.jvm.tasks.Jar

plugins {
    id("conventions.loader")
    id("net.fabricmc.fabric-loom")
    id("me.modmuss50.mod-publish-plugin")
}
base.archivesName = base.archivesName.get() + "-fabric"

repositories {
    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/")
    }
}
dependencies {
    minecraft(libs.minecraft);

    implementation(libs.fabric.loader)
    implementation(libs.fabric.api)

    runtimeOnly(libs.modmenu)
}

loom {
    val aw = file("src/main/resources/${Properties.MOD_ID}.accesswidener");
    if (aw.exists())
        accessWidenerPath.set(aw)
    mods {
        register(Properties.MOD_ID) {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["test"])
        }
    }
    runs {
        named("client") {
            client()
            configName = "Fabric Client"
            setSource(sourceSets["test"])
            ideConfigGenerated(true)
//            vmArgs("-Dmixin.debug.verbose=true", "-Dmixin.debug.export=true")
        }
        named("server") {
            server()
            configName = "Fabric Server"
            setSource(sourceSets["test"])
            ideConfigGenerated(true)
//            vmArgs("-Dmixin.debug.verbose=true", "-Dmixin.debug.export=true")
        }
        register("datagen") {
            client()
            configName = "Fabric Datagen"
            setSource(sourceSets["test"])
            ideConfigGenerated(true)
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${file("../common/src/generated/resources")}")
            vmArg("-Dfabric-api.datagen.modid=${Properties.MOD_ID}")
            runDir("build/datagen")
        }
    }
}

tasks {
    named<ProcessResources>("processResources").configure {
        exclude("${Properties.MOD_ID}.cfg")
    }
}


publishMods {
    file.set(tasks.named<Jar>("jar").get().archiveFile)
    modLoaders.add("fabric")
    changelog = rootProject.file("CHANGELOG.md").readText()
    displayName = "Colorful Creakings Fabric ${Properties.MOD}+${libs.minecraft.get().version}"
    version = "${Properties.MOD}+${libs.minecraft.get().version}-fabric"
    type = STABLE

    curseforge {
        projectId = Properties.CURSEFORGE_PROJECT_ID
        accessToken = providers.gradleProperty("CF_API_KEY")

        minecraftVersions.addAll(Properties.SUPPORTED_VERSIONS.asIterable())
        javaVersions.add(JavaVersion.VERSION_25)

        clientRequired = true
        serverRequired = true
    }

    modrinth {
        projectId = Properties.MODRINTH_PROJECT_ID
        accessToken = providers.gradleProperty("MODRINTH_TOKEN")

        minecraftVersions.addAll(Properties.SUPPORTED_VERSIONS.asIterable())
    }

    github {
        accessToken = providers.gradleProperty("GH_TOKEN")
        parent(project(":").tasks.named("publishGithub"))
    }
}