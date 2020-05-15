package com.arkivanov.mvikotlin.sample.todo.rxjava2.controller

import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.extensions.rxjava2.bind
import com.arkivanov.mvikotlin.extensions.rxjava2.events
import com.arkivanov.mvikotlin.extensions.rxjava2.labels
import com.arkivanov.mvikotlin.extensions.rxjava2.states
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController.*
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.*
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView
import com.arkivanov.mvikotlin.sample.todo.rxjava2.store.TodoAddStoreFactory
import com.arkivanov.mvikotlin.sample.todo.rxjava2.store.TodoListStoreFactory
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class TodoListRxJava2Controller(dependencies: Dependencies) : TodoListController {

    private val todoListStore =
        TodoListStoreFactory(
            storeFactory = dependencies.storeFactory,
            database = dependencies.database
        ).create()

    private val todoAddStore =
        TodoAddStoreFactory(
            storeFactory = dependencies.storeFactory,
            database = dependencies.database
        ).create()

    private val inputRelay: Subject<Input> = PublishSubject.create()
    override val input: (Input) -> Unit = inputRelay::onNext

    init {
        bind(dependencies.lifecycle, BinderLifecycleMode.CREATE_DESTROY) {
            inputRelay.map(inputToListIntent) bindTo todoListStore
            todoAddStore.labels.map(addLabelToListIntent) bindTo todoListStore
        }

        dependencies.lifecycle.doOnDestroy {
            todoListStore.dispose()
            todoAddStore.dispose()
        }
    }

    override fun onViewCreated(
        todoListView: TodoListView,
        todoAddView: TodoAddView,
        viewLifecycle: Lifecycle,
        output: (Output) -> Unit
    ) {
        bind(viewLifecycle, BinderLifecycleMode.CREATE_DESTROY) {
            todoListView.events.map(listEventToListIntent) bindTo todoListStore
            todoAddView.events.map(addEventToAddIntent) bindTo todoAddStore
        }

        bind(viewLifecycle, BinderLifecycleMode.START_STOP) {
            todoListStore.states.map(listStateToListModel) bindTo todoListView
            todoAddStore.states.map(addStateToAddModel) bindTo todoAddView
            todoListView.events.map(listEventToOutput) bindTo output
        }
    }
}
