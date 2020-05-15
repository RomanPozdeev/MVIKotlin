package com.arkivanov.mvikotlin.sample.todo.rxjava2.store

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.rxjava2.RxJava2Executor
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.*
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStoreAbstractFactory
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

internal class TodoAddStoreFactory(
    storeFactory: StoreFactory,
    private val database: TodoDatabase
) : TodoAddStoreAbstractFactory(
    storeFactory = storeFactory
) {

    override fun createExecutor(): Executor<Intent, Nothing, State, Result, Label> = ExecutorImpl()

    private inner class ExecutorImpl : RxJava2Executor<Intent, Nothing, State, Result, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.HandleTextChanged -> dispatch(Result.TextChanged(intent.text))
                is Intent.Add -> addItem(getState())
            }.let {}
        }

        private fun addItem(state: State) {
            val text = state.text.takeUnless(String::isBlank) ?: return

            dispatch(Result.TextChanged(""))

            Single.fromCallable {
                database.create(TodoItem.Data(text = text))
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Label::Added)
                .subscribeScoped(onSuccess = ::publish)
        }
    }
}
