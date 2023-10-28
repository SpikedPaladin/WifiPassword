package me.paladin.wifi

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Locator.initWith(this)
    }
}