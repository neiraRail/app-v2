package com.neirarail.sensor_3.di

import android.content.Context
import com.neirarail.sensor_3.remote.ConfigService
import com.neirarail.sensor_3.remote.TelemetryService
import com.neirarail.sensor_3.sensors.AccelerometerSensor
import com.neirarail.sensor_3.sensors.GyroscopeSensor
import com.neirarail.sensor_3.sensors.MagnetometerSensor


interface AppModule {
    val accelerometer: AccelerometerSensor
    val gyroscope: GyroscopeSensor
    val magnetometer: MagnetometerSensor
    val configService: ConfigService
    val telemetryService: TelemetryService
}

class AppModuleImpl(
    private val appContext: Context
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
}