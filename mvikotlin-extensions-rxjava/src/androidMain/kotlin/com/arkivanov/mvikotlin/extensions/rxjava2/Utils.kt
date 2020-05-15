package com.arkivanov.mvikotlin.extensions.rxjava2

import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import com.arkivanov.mvikotlin.rx.observer
import io.reactivex.Observable
import java.util.concurrent.atomic.AtomicReference

fun <T : Any> lateinitAtomicReference(): AtomicReference<T?> = AtomicReference(null)

fun <T : Any> AtomicReference<T?>.initialize(value: T) {
    check(this.get() == null) { "AtomicReference is already initialized: $this" }

    this.set(value)
}

val <T : Any> AtomicReference<T?>.requireValue: T
    get() = requireNotNull(this.get()) { "Value was not initialized" }

internal inline fun <T, R> T.toObservable(
    crossinline subscribe: T.(Observer<R>) -> Disposable
): Observable<R> =
    Observable.create { emitter ->
        val disposable = subscribe(
            observer(
                onComplete = emitter::onComplete,
                onNext = emitter::onNext
            )
        )
        emitter.setDisposable(disposable.toReaktiveDisposable())
    }
