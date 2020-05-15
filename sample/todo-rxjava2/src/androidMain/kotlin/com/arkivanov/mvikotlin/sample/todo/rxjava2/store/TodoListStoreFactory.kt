package com.arkivanov.mvikotlin.sample.todo.rxjava2.store

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.rxjava2.RxJava2Executor
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStore.State
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStoreAbstractFactory
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

internal class TodoListStoreFactory(
    storeFactory: StoreFactory,
    private val database: TodoDatabase
) : TodoListStoreAbstractFactory(
    storeFactory = storeFactory
) {

    override fun createExecutor(): Executor<Intent, Unit, State, Result, Nothing> = ExecutorImpl()

    private inner class ExecutorImpl : RxJava2Executor<Intent, Unit, State, Result, Nothing>() {
        override fun executeAction(action: Unit, getState: () -> State) {
            Single.fromCallable(database::getAll)
                .subscribeOn(Schedulers.io())
                .map(Result::Loaded)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeScoped(onSuccess = ::dispatch)
        }

        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.Delete -> delete(intent.id)
                is Intent.ToggleDone -> toggleDone(intent.id, getState)
                is Intent.HandleAdded -> dispatch(Result.Added(intent.item))
                is Intent.HandleTextChanged -> dispatch(Result.TextChanged(intent.id, intent.text))
                is Intent.HandleDeleted -> dispatch(Result.Deleted(intent.id))
                is Intent.HandleItemChanged -> dispatch(Result.Changed(intent.id, intent.data))
                Intent.NoOp -> {
                }
            }.let {}
        }

        private fun delete(id: String) {
            dispatch(Result.Deleted(id))

            Single.fromCallable { database.delete(id) }
                .subscribeOn(Schedulers.io())
                .subscribeScoped()
        }

        private fun toggleDone(id: String, state: () -> State) {
            dispatch(Result.DoneToggled(id))

            val item = state().items.find { it.id == id } ?: return

            Completable.fromAction {
                database.save(id, item.data)
            }
                .subscribeOn(Schedulers.io())
                .subscribeScoped()
        }
    }
}
