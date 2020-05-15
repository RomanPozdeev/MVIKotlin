buildTargets = setOf(BuildTarget.Jvm, BuildTarget.Android)

setupMultiplatform()
setupPublication()

doIfBuildTargetAvailable<BuildTarget.Android> {
    android {
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mvikotlin"))
                implementation(project(":rx"))
                implementation(project(":rx-internal"))
                implementation(project(":utils-internal"))
            }
        }
        jvmCommonMain {
            dependencies {
                implementation(Deps.ReactiveX.RxJava2)
            }
        }
    }
}
