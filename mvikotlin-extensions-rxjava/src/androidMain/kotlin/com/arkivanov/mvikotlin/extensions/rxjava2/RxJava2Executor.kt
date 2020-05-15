package com.arkivanov.mvikotlin.extensions.rxjava2

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * An abstract implementation of the [Executor] that provides interoperability with RxJava2.
 * Implements [DisposableScope] which disposes when the [Executor] is disposed.
 */
open class RxJava2Executor<in Intent : Any, in Action : Any, in State : Any, Result : Any, Label : Any> :
    Executor<Intent, Action, State, Result, Label>,
    Disposable {

    private val disposables = CompositeDisposable()
    private val callbacks = lateinitAtomicReference<Executor.Callbacks<State, Result, Label>>()
    private val getState: () -> State = { callbacks.requireValue.state }

    final override fun init(callbacks: Executor.Callbacks<State, Result, Label>) {
        this.callbacks.initialize(callbacks)
    }

    final override fun handleIntent(intent: Intent) {
        executeIntent(intent, getState)
    }

    /**
     * The companion of the [Executor.handleIntent] method
     *
     * @param intent an `Intent` received by the [Store]
     * @param getState a `State` supplier that returns the *current* `State` of the [Store]
     */
    @MainThread
    protected open fun executeIntent(intent: Intent, getState: () -> State) {
    }

    final override fun handleAction(action: Action) {
        executeAction(action, getState)
    }

    /**
     * The companion of the [Executor.handleAction] method
     *
     * @param action an `Action` produced by the [Bootstrapper]
     * @param getState a `State` supplier that returns the *current* `State` of the [Store]
     */
    @MainThread
    protected open fun executeAction(action: Action, getState: () -> State) {
    }

    override fun isDisposed(): Boolean {
        return disposables.isDisposed
    }

    override fun dispose() {
        disposables.clear()
    }

    /**
     * Dispatches the provided `Result` to the [Reducer].
     * The updated `State` will be available immediately after this method returns.
     *
     * @param result a `Result` to be dispatched to the `Reducer`
     */
    @MainThread
    protected fun dispatch(result: Result) {
        callbacks.requireValue.onResult(result)
    }

    /**
     * Sends the provided `Label` to the [Store] for publication
     *
     * @param label a `Label` to be published
     */
    @MainThread
    protected fun publish(label: Label) {
        callbacks.requireValue.onLabel(label)
    }

    /*
    * DisposableScope delegate
    */
    fun <T> Observable<T>.subscribeScoped(
        onSubscribe: ((Disposable) -> Unit)?,
        onError: ((Throwable) -> Unit)?,
        onComplete: (() -> Unit)?,
        onNext: ((T) -> Unit)?
    ): Disposable = this.subscribe(onNext, onError, onComplete, onSubscribe).apply {
        disposables.add(this)
    }

    fun <T> Single<T>.subscribeScoped(
        onSubscribe: ((Disposable) -> Unit)? = {},
        onError: ((Throwable) -> Unit)? = {},
        onSuccess: ((T) -> Unit)? = {}
    ): Disposable = this.also {
        it.doOnSubscribe(onSubscribe)
    }.subscribe(onSuccess, onError).apply {
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
        onSubscribe: ((Disposable) -> Unit)? = {},
        onError: ((Throwable) -> Unit)? = {},
        onComplete: (() -> Unit)? = {}
    ): Disposable = this.doOnSubscribe(onSubscribe).subscribe(onComplete, onError).apply {
        disposables.add(this)
    }
}
