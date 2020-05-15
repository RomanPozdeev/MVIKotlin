package com.arkivanov.mvikotlin.extensions.rxjava2

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * An abstract implementation of the [Bootstrapper] that provides interoperability with RxJava2.
 * Implements [Disposable] which disposes when the [Bootstrapper] is disposed.
 */
abstract class RxJava2Bootstrapper<Action : Any> : Bootstrapper<Action>, Disposable {

    private val disposables = CompositeDisposable()
    private val actionConsumer = lateinitAtomicReference<(Action) -> Unit>()

    final override fun init(actionConsumer: (Action) -> Unit) {
        this.actionConsumer.initialize(actionConsumer)
    }

    /**
     * Dispatches the `Action` to the [Store]
     *
     * @param action an `Action` to be dispatched
     */
    protected fun dispatch(action: Action) {
        actionConsumer.requireValue.invoke(action)
    }

    override fun dispose() {
        disposables.clear()
    }

    /*
     * DisposableScope delegate
     */

    override fun isDisposed(): Boolean {
        return disposables.isDisposed
    }

    fun <T> Observable<T>.subscribeScoped(
        onSubscribe: ((Disposable) -> Unit)?,
        onError: ((Throwable) -> Unit)?,
        onComplete: (() -> Unit)?,
        onNext: ((T) -> Unit)?
    ): Disposable = this.subscribe(onNext, onError, onComplete, onSubscribe).apply {
        disposables.add(this)
    }

    fun <T> Single<T>.subscribeScoped(
        onSubscribe: ((Disposable) -> Unit)?,
        onError: ((Throwable) -> Unit)?,
        onSuccess: ((T) -> Unit)?
    ): Disposable = this.doOnSubscribe(onSubscribe).subscribe(onSuccess, onError).apply {
        disposables.add(this)
    }

    fun <T> Maybe<T>.subscribeScoped(
        onSubscribe: ((Disposable) -> Unit)?,
        onError: ((Throwable) -> Unit)?,
        onComplete: (() -> Unit)?,
        onSuccess: ((T) -> Unit)?
    ): Disposable =
        this.doOnSubscribe(onSubscribe).subscribe(onSuccess, onError, onComplete).apply {
            disposables.add(this)
        }

    fun Completable.subscribeScoped(
        onSubscribe: ((Disposable) -> Unit)?,
        onError: ((Throwable) -> Unit)?,
        onComplete: (() -> Unit)?
    ): Disposable = this.doOnSubscribe(onSubscribe).subscribe(onComplete, onError).apply {
        disposables.add(this)
    }
}
