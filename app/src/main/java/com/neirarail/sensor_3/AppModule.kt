package com.neirarail.sensor_3

import android.content.Context
import com.neirarail.sensor_3.sensors.AccelerometerSensor
import com.neirarail.sensor_3.sensors.GyroscopeSensor
import com.neirarail.sensor_3.sensors.MagnetometerSensor


interface AppModule {
    val accelerometer: AccelerometerSensor
    val gyroscope: GyroscopeSensor
    val magnetometer: MagnetometerSensor
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
}