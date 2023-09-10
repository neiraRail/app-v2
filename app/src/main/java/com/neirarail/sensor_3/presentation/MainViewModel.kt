package com.neirarail.sensor_3.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neirarail.sensor_3.di.AppModule
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject


class MainViewModel(
    private val appModuleImpl: AppModule
) : ViewModel() {

    private val _sensorData = mutableStateListOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

    val sensorData: List<Float> = _sensorData
    var config: JSONObject? by mutableStateOf(null)

    init {
        appModuleImpl.accelerometer.startListening()
        appModuleImpl.gyroscope.startListening()
        appModuleImpl.magnetometer.startListening()
        appModuleImpl.accelerometer.setOnSensorValuesChangedListener { values ->
            _sensorData[0] = values[0]
            _sensorData[1] = values[1]
            _sensorData[2] = values[2]
        }
        appModuleImpl.gyroscope.setOnSensorValuesChangedListener { values ->
            _sensorData[3] = values[0]
            _sensorData[4] = values[1]
            _sensorData[5] = values[2]
        }
        appModuleImpl.magnetometer.setOnSensorValuesChangedListener { values ->
            _sensorData[6] = values[0]
            _sensorData[7] = values[1]
            _sensorData[8] = values[2]
        }

        viewModelScope.launch {
            config = appModuleImpl.configService.getConfiguration("{\"node\": 1, \"start\": 0}")
        }

        viewModelScope.launch {
            while (true) {
                delay(1000)
                println("Sensor data: $sensorData")
                val telemetry = """
                {
                    "time": 0,
                    "time_lap": 0,
                    "event": 1,
                    "node": ${config?.get("node")},
                    "acc_x": ${sensorData[0]},
                    "acc_y": ${sensorData[1]},
                    "acc_z": ${sensorData[2]},
                    "gyr_x": ${sensorData[3]},
                    "gyr_y": ${sensorData[4]},
                    "gyr_z": ${sensorData[5]},
                    "mx": ${sensorData[6]},
                    "my": ${sensorData[7]},
                    "mz": ${sensorData[8]},
                    "tp": 0
                }
                """.trimIndent()

                appModuleImpl.telemetryService.sendTelemetry(telemetry)
            }
        }
    }
}