plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.compose)
    alias(libs.plugins.mavenPublish)
}

group = "sh.calvin.autolinktext"
version = "1.1.1"

kotlin {
    androidTarget {
        publishLibraryVariants("release", "debug")
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    jvm()

    wasmJs {
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
            implementation(compose.foundation)
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
    compileSdk = 34
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
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
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
