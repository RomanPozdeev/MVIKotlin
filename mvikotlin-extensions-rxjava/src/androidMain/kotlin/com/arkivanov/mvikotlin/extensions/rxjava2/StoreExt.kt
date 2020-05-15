package com.arkivanov.mvikotlin.extensions.rxjava2

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.store.Store
import io.reactivex.Observable

/**
 * Returns an [Observable] that emits [Store] `States`.
 * The first emission with the current `State` will be performed synchronously on subscription.
 * Emissions are performed on the main thread.
 */
@MainThread
val <State : Any> Store<*, State, *>.states: Observable<State>
    get() = toObservable(Store<*, State, *>::states)

/**
 * Returns an [Observable] that emits [Store] `Labels`.
 * Emissions are performed on the main thread.
 */
@MainThread
val <Label : Any> Store<*, *, Label>.labels: Observable<Label>
    get() = toObservable(Store<*, *, Label>::labels)
