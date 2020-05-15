package com.arkivanov.mvikotlin.sample.todo.common.controller

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView

interface TodoListController {

    val input: (Input) -> Unit

    fun onViewCreated(
        todoListView: TodoListView,
        todoAddView: TodoAddView,
        viewLifecycle: Lifecycle,
        output: (Output) -> Unit
    )

    interface Dependencies {
        val storeFactory: StoreFactory
        val database: TodoDatabase
        val lifecycle: Lifecycle
    }

    sealed class Input {
        data class ItemChanged(val id: String, val data: TodoItem.Data) : Input()
        data class ItemDeleted(val id: String) : Input()
    }

    sealed class Output {
        object NoOp : Output()
        data class ItemSelected(val id: String) : Output()
    }
}
