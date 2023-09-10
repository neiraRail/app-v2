package com.neirarail.sensor_3

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class MainViewModel(
    private val appModuleImpl: AppModule
): ViewModel(){

    var sensorData by mutableStateOf( 0f)

    init {
        appModuleImpl.accelerometer.startListening()
        appModuleImpl.gyroscope.startListening()
        appModuleImpl.magnetometer.startListening()
        appModuleImpl.accelerometer.setOnSensorValuesChangedListener {values ->
            sensorData = values[0]
        }
    }
}