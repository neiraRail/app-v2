package com.neirarail.sensor_3

import android.app.Application
import com.neirarail.sensor_3.di.AppModule
import com.neirarail.sensor_3.di.AppModuleImpl


class SensorApp: Application() {
    companion object {
        lateinit var appModule: AppModule
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(this)
    }
}