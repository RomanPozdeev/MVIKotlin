package com.arkivanov.mvikotlin.sample.todo.rxjava2.controller

import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.extensions.rxjava2.bind
import com.arkivanov.mvikotlin.extensions.rxjava2.events
import com.arkivanov.mvikotlin.extensions.rxjava2.labels
import com.arkivanov.mvikotlin.extensions.rxjava2.states
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoDetailsController
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoDetailsController.Dependencies
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoDetailsController.Output
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.detailsEventToIntent
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.detailsLabelToOutput
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.detailsStateToModel
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView
import com.arkivanov.mvikotlin.sample.todo.rxjava2.store.TodoDetailsStoreFactory

class TodoDetailsRxJava2Controller(dependencies: Dependencies) : TodoDetailsController {

    private val todoDetailsStore =
        TodoDetailsStoreFactory(
            storeFactory = dependencies.storeFactory,
            database = dependencies.database,
            itemId = dependencies.itemId
        ).create()

    init {
        dependencies.lifecycle.doOnDestroy(todoDetailsStore::dispose)
    }

    override fun onViewCreated(
        todoDetailsView: TodoDetailsView,
        viewLifecycle: Lifecycle,
        output: (Output) -> Unit
    ) {
        bind(viewLifecycle, BinderLifecycleMode.CREATE_DESTROY) {
            todoDetailsView.events.map(detailsEventToIntent) bindTo todoDetailsStore
        }

        bind(viewLifecycle, BinderLifecycleMode.START_STOP) {
            todoDetailsStore.states.map(detailsStateToModel) bindTo todoDetailsView
            todoDetailsStore.labels.map(detailsLabelToOutput) bindTo output
        }
    }
}

