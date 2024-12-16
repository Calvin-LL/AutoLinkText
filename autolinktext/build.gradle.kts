import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.maven.publish)
}

group = "sh.calvin.autolinktext"
version = "2.0.0"

kotlin {
    androidTarget {
        publishLibraryVariants("release", "debug")
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }

    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    js {
        browser()
        binaries.executable()
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "autolinktext"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.material3)
            implementation(libs.kermit)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.libphonenumber)
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.robolectric)
            }
        }

        jvmMain.dependencies {
            implementation(libs.libphonenumber)
        }
    }
}

android {
    namespace = project.group.toString()
    compileSdk = 35
    defaultConfig {
        minSdk = 21
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

mavenPublishing {
    pom {
        name = "AutoLinkText"
        description = "A library for Compose to automatically linkify text"
        url = "https://github.com/Calvin-LL/AutoLinkText"
        inceptionYear = "2024"

        licenses {
            license {
                name = "The Apache Software License, Version 2.0"
                url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        scm {
            connection = "scm:git:git://github.com/Calvin-LL/AutoLinkText.git"
            developerConnection = "scm:git:ssh://github.com/Calvin-LL/AutoLinkText.git"
            url = "https://github.com/Calvin-LL/AutoLinkText"
        }
        developers {
            developer {
                name = "Calvin Liang"
                email = "me@calvin.sh"
                url = "https://calvin.sh"
            }
        }
    }
}
