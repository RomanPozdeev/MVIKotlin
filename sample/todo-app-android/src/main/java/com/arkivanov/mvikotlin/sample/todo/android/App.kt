package com.arkivanov.mvikotlin.sample.todo.android

import androidx.multidex.MultiDexApplication
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabaseImpl
import com.arkivanov.mvikotlin.timetravel.server.TimeTravelServer

class App : MultiDexApplication() {

    lateinit var database: TodoDatabase
        private set

    private val timeTravelServer = TimeTravelServer()

    override fun onCreate() {
        super.onCreate()

        database = TodoDatabaseImpl(this)
        timeTravelServer.start()
    }
}
