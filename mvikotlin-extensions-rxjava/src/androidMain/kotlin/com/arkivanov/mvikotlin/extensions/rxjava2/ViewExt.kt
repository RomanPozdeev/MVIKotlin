package com.arkivanov.mvikotlin.extensions.rxjava2

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.view.ViewEvents
import io.reactivex.Observable

/**
 * Returns a [Observable] that emits `View Events`
 * Emissions are performed on the main thread.
 */
@MainThread
val <Event : Any> ViewEvents<Event>.events: Observable<Event>
    get() = toObservable(ViewEvents<Event>::events)
