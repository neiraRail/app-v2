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

    private val _sensorData = mutableStateListOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    val sensorData: List<Float> = _sensorData

    init {
        appModuleImpl.accelerometer.startListening()
        appModuleImpl.gyroscope.startListening()
        appModuleImpl.magnetometer.startListening()
        appModuleImpl.accelerometer.setOnSensorValuesChangedListener {values ->
            _sensorData[0] = values[0]
            _sensorData[1] = values[1]
            _sensorData[2] = values[2]
        }
        appModuleImpl.gyroscope.setOnSensorValuesChangedListener {values ->
            _sensorData[3] = values[0]
            _sensorData[4] = values[1]
            _sensorData[5] = values[2]
        }
        appModuleImpl.magnetometer.setOnSensorValuesChangedListener {values ->
            _sensorData[6] = values[0]
            _sensorData[7] = values[1]
            _sensorData[8] = values[2]
        }
    }
}