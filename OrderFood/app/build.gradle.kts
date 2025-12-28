plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "1.8.0"
}


android {
    namespace = "com.ccf.feige.orderfood"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ccf.feige.orderfood"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // 新增：统一 Kotlin 标准库依赖（版本与 Kotlin 插件一致）
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")

    // 补充：Glide 注解处理器（根据开发语言选择，二选一）
    // Java 项目使用
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    // 补充：AndroidX 核心扩展，简化文件、权限等操作
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-ktx:1.8.0")

    // 你的原有依赖（保留不变，移除冗余的 legacy-support-v4）
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation("androidx.annotation:annotation:1.6.0")
    implementation("com.alibaba.fastjson2:fastjson2:2.0.51")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // 测试依赖（保留不变）
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}