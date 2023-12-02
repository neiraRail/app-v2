package com.neirarail.sensor_3.di

import android.content.Context
import com.neirarail.sensor_3.remote.ConfigRepo
import com.neirarail.sensor_3.remote.ConfigService
import com.neirarail.sensor_3.remote.TelemetryRepo
import com.neirarail.sensor_3.remote.TelemetryService
import com.neirarail.sensor_3.sensors.AccelerometerSensor
import com.neirarail.sensor_3.sensors.GyroscopeSensor
import com.neirarail.sensor_3.sensors.MagnetometerSensor
import com.neirarail.sensor_3.sensors.MeasurableSensor
import com.neirarail.sensor_3.storage.ConfigStorage


interface AppModule {
    val accelerometer: MeasurableSensor
    val gyroscope: MeasurableSensor
    val magnetometer: MeasurableSensor
    val configService: ConfigRepo
    val telemetryService: TelemetryRepo
    val configStorage: ConfigStorage
}

class AppModuleImpl(
    val appContext: Context
): AppModule {
    override val accelerometer: AccelerometerSensor by lazy {
        AccelerometerSensor(appContext)
    }

    override val gyroscope: GyroscopeSensor by lazy {
        GyroscopeSensor(appContext)
    }

    override val magnetometer: MagnetometerSensor by lazy {
        MagnetometerSensor(appContext)
    }

    override val configService: ConfigService by lazy {
        ConfigService()
    }

    override val telemetryService: TelemetryService by lazy {
        TelemetryService()
    }

    override val configStorage: ConfigStorage by lazy {
        ConfigStorage(appContext, "config.json")
    }
}