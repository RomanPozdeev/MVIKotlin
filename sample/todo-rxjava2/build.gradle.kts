setupMultiplatform()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mvikotlin"))
                api(project(":sample:todo-common"))
                implementation(project(":sample:todo-common-internal"))
            }
        }
        jvmCommonMain {
            dependencies {
                implementation(project(":mvikotlin-extensions-rxjava"))
                implementation(Deps.ReactiveX.RxJava2)
                implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
            }
        }
    }
}
