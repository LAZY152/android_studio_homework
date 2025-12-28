plugins {
    // 统一声明 Kotlin 插件版本（方便多模块统一管理）
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
}


tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}