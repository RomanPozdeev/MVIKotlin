package com.arkivanov.mvikotlin.sample.todo.common.internal.store.list

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStore.State

interface TodoListStore : Store<Intent, State, Nothing> {

    sealed class Intent : JvmSerializable {
        object NoOp : Intent()
        data class Delete(val id: String) : Intent()
        data class ToggleDone(val id: String) : Intent()
        data class HandleAdded(val item: TodoItem) : Intent()
        data class HandleTextChanged(val id: String, val text: String) : Intent()
        data class HandleDeleted(val id: String) : Intent()
        data class HandleItemChanged(val id: String, val data: TodoItem.Data) : Intent()
    }

    data class State(
        val items: List<TodoItem> = emptyList(),
        val selectedItemId: String? = null
    ) : JvmSerializable
}
