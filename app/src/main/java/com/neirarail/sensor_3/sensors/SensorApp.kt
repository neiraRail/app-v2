package com.neirarail.sensor_3.sensors

import android.app.Application
import com.neirarail.sensor_3.AppModule
import com.neirarail.sensor_3.AppModuleImpl


class SensorApp: Application() {
    companion object {
        lateinit var appModule: AppModule
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(this)
    }
}