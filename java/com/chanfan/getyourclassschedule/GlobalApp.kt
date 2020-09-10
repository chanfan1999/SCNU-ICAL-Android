package com.chanfan.getyourclassschedule

import android.app.Application
import android.content.Context

class GlobalApp : Application() {
    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}