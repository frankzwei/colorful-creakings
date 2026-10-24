import me.ultrusmods.colorfulcreakings.gradle.Properties
import me.modmuss50.mpp.PublishModTask

plugins {
    id("conventions.common")
    alias(libs.plugins.neoforge.moddev)
}

sourceSets {
    create("generated") {
        resources {
            srcDir("src/generated/resources")
        }
    }
}
neoForge {
    setNeoFormVersion(Properties.NEOFORM_VERSION)
    addModdingDependenciesTo(sourceSets["test"])

    val at = file("src/main/resources/${Properties.MOD_ID}.cfg")
    if (at.exists())
        setAccessTransformers(at)
    validateAccessTransformers = true
}

dependencies {
    compileOnly(libs.mixin.extras)
    annotationProcessor(libs.mixin.extras)
    compileOnly(libs.fabric.mixin)
}

configurations {
    register("commonJava") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    register("commonResources") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    register("commonTestResources") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
}

artifacts {
    add("commonJava", sourceSets["main"].java.sourceDirectories.singleFile)
    add("commonResources", sourceSets["main"].resources.sourceDirectories.singleFile)
    add("commonResources", sourceSets["generated"].resources.sourceDirectories.singleFile)
    add("commonTestResources", sourceSets["test"].resources.sourceDirectories.singleFile)
}