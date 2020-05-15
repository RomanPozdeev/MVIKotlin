package com.arkivanov.mvikotlin.sample.todo.common.internal.mapper

import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoDetailsController.Output
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore.*
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView.Event
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView.Model

val detailsStateToModel: State.() -> Model =
    {
        Model(
            text = data?.text ?: "",
            isDone = data?.isDone ?: false
        )
    }

val detailsEventToIntent: Event.() -> Intent =
    {
        when (this) {
            is Event.TextChanged -> Intent.HandleTextChanged(text = text)
            is Event.DoneClicked -> Intent.ToggleDone
            is Event.DeleteClicked -> Intent.Delete
        }
    }

val detailsLabelToOutput: Label.() -> Output =
    {
        when (this) {
            is Label.Changed -> Output.ItemChanged(id = id, data = data)
            is Label.Deleted -> Output.ItemDeleted(id = id)
        }
    }
