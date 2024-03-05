plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.mavenPublish)
}

group = "sh.calvin.autolinktext"
version = "1.0.0"

android {
    namespace = project.group.toString()
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.ui)
    implementation(libs.libphonenumber)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
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
