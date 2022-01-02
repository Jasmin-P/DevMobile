package com.devmobilejasmin.todo.network

import android.app.Application

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Api.setUpContext(this)
    }
}