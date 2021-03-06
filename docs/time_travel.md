[Overview](index.md) | [Store](store.md) | [View](view.md) | [Binding and Lifecycle](binding_and_lifecycle.md) | [Logging](logging.md) | Time travel

## Time travel

Time travel is a power debugging tool. Basically it allows you to record all events and states from all active `Stores`. When events are recorded you can explore them, replay and debug. The core functionality is multiplatform and is available for all supported targets.

If you want time travel you have to use the [TimeTravelStoreFactory](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin-timetravel/src/commonMain/kotlin/com/arkivanov/mvikotlin/timetravel/store/TimeTravelStoreFactory.kt).

### Using TimeTravelStoreFactory

The `TimeTravelStoreFactory` is used to create implementations of `Store` that are able to record and replay events. 

Suppose we have the following `Store` factory:

```kotlin
internal class CalculatorStoreFactory(private val storeFactory: StoreFactory) {

    fun create(): CalculatorStore =
        object : CalculatorStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "CounterStore",
            // ...
        ) {
        }

    // ...
}
```

It accepts a `StoreFactory` and uses it to create an implementation of the `CalculatorStore`. You can now pass any `StoreFactory` here. So if you want to add time travel just pass an instance of the `TimeTravelStoreFactory`:

```kotlin
val storeFactory = TimeTravelStoreFactory(DefaultStoreFactory)

CalculatorStoreFactory(storeFactory).create()
```

You can also combine it with the `LoggingStoreFactory` in the following way:

```kotlin
val storeFactory = LoggingStoreFactory(TimeTravelStoreFactory(DefaultStoreFactory))

CalculatorStoreFactory(storeFactory).create()
```

Normally you should define a global `StoreFactory` somewhere in the main app and pass it down to all the dependencies.

### Time travel UI

You can integrate a special UI into your app which provides controls for time travel and displays a list of recorded events. It is also possible to explore and debug any recorded event. Debugging means you can put a breakpoint in your code and fire a previously recorded event. You can do usual debugging when the breakpoint is triggered.

For Android you can use the [TimeTravelView](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin-timetravel/src/androidMain/kotlin/com/arkivanov/mvikotlin/timetravel/widget/TimeTravelView.kt).
For iOS you can copy-paste [TimeTravelViewController](https://github.com/arkivanov/MVIKotlin/blob/master/sample/todo-app-ios/todo-app-ios/TimeTravelViewController.swift) from the sample app.

Check out the following videos demonstrating time travel UI: 
- [Debugging Android application with MVIKotlin](https://youtu.be/_bbxR503-u0)
- [Debugging iOS application with MVIKotlin](https://youtu.be/MJCYQzeL-w8)

Alternatively you can create your own time travel UI. Please refer to [TimeTravelController](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin-timetravel/src/commonMain/kotlin/com/arkivanov/mvikotlin/timetravel/controller/TimeTravelController.kt) for more information.

### Time travel plugin for IntelliJ IDEA and Android Studio

There is a more convenient tool for Android - Time travel plugin. This can be used directly from IDE so there is no need to integrate any additional UI.

Check out the [video](https://youtu.be/Tr2ayOcVU34) demonstrating the time travel plugin.

#### How to install

You can find the plugin in the IntelliJ IDEA [Marketplace](https://plugins.jetbrains.com/plugin/14241-mvikotlin-time-travel). It can be installed directly from IntelliJ IDEA or Android Studio. Please navigate to Settings -> Plugins -> Marketplace and type "MVIKotlin" in the search field.

Please refer to the [samples](https://github.com/arkivanov/MVIKotlin/tree/master/sample) for more examples.

[Overview](index.md) | [Store](store.md) | [View](view.md) | [Binding and Lifecycle](binding_and_lifecycle.md) | [Logging](logging.md) | Time travel
