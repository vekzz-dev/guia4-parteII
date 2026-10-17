plugins {
    id("com.android.application")
}

android {
    namespace = "sv.edu.ufg.agenda"
    compileSdk = 34

    defaultConfig {
        applicationId = "sv.edu.ufg.agenda"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.code.gson:gson:2.11.0")

    // Pruebas locales sobre la capa de datos: no necesitan emulador.
    testImplementation("junit:junit:4.13.2")
}
