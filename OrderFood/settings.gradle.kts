pluginManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }

        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // 保留你原有的阿里云/Google/MavenCentral 仓库
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        google()
        mavenCentral()
        // 关键：添加 JitPack 仓库（必须放在所有仓库后，或与其他仓库同级）
        maven { url = uri("https://jitpack.io") }
    }
}

// 声明你的项目模块（确保包含 OrderFood 模块）
rootProject.name = "big_homework"
include(":app") // 对应你的 OrderFood 模块（与文件夹名称一致）
 