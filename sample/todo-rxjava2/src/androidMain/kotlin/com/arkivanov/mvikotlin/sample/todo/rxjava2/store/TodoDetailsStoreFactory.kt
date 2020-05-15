package com.arkivanov.mvikotlin.sample.todo.rxjava2.store

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.rxjava2.RxJava2Executor
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore.*
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStoreAbstractFactory
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

internal class TodoDetailsStoreFactory(
    storeFactory: StoreFactory,
    private val database: TodoDatabase,
    private val itemId: String
) : TodoDetailsStoreAbstractFactory(
    storeFactory = storeFactory
) {

    override fun createExecutor(): Executor<Intent, Unit, State, Result, Label> = ExecutorImpl()

    private inner class ExecutorImpl : RxJava2Executor<Intent, Unit, State, Result, Label>() {
        override fun executeAction(action: Unit, getState: () -> State) {
            Single.fromCallable {
                database.get(itemId)
            }
                .subscribeOn(Schedulers.io())
                .map { it.data.let(Result::Loaded) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeScoped(onSuccess = ::dispatch)
        }

        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.HandleTextChanged -> handleTextChanged(intent.text, getState)
                is Intent.ToggleDone -> toggleDone(getState)
                is Intent.Delete -> delete()
            }.let {}
        }

        private fun handleTextChanged(text: String, state: () -> State) {
            dispatch(Result.TextChanged(text))
            save(state())
        }

        private fun toggleDone(state: () -> State) {
            dispatch(Result.DoneToggled)
            save(state())
        }

        private fun save(state: State) {
            val data = state.data ?: return
            publish(Label.Changed(itemId, data))

            Completable.fromAction {
                database.save(itemId, data)
            }
                .subscribeOn(Schedulers.io())
                .subscribeScoped()
        }

        private fun delete() {

            publish(Label.Deleted(itemId))

            Completable.fromAction {
                database.delete(itemId)
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeScoped {
                    dispatch(Result.Finished)
                }
        }
    }
}
