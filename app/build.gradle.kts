plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.cashnova"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.cashnova"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

/*
 * Menyimpan file schema Room.
 * Folder app/schemas akan dibuat saat database berhasil dikompilasi.
 */
ksp {
    arg(
        "room.schemaLocation",
        "$projectDir/schemas"
    )
}

dependencies {
    implementation("androidx.core:core-ktx:1.18.0")
    implementation("androidx.activity:activity-compose:1.12.4")

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2026.02.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Lifecycle dan ViewModel
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.9.8")

    // Room Database
    val roomVersion = "2.8.4"

    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Unit test
    testImplementation("junit:junit:4.13.2")

    // Android test
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2026.02.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Compose preview dan debugging
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}