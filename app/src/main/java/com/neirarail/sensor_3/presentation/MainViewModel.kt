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
import org.json.JSONObject


class MainViewModel(
    private val appModuleImpl: AppModule
) : ViewModel() {

    fun toggleActive() {
        if (config != null) {
            val newConfig = JSONObject(config.toString())
            if (config!!["active"] == 1) {
                newConfig.put("active", 0)
            } else {
                newConfig.put("active", 1)
            }
            config = newConfig
        }
    }

    private val _sensorData = mutableStateListOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private val _oldSensorData = mutableListOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

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

        /**
         * Send sensor data to server every second
         */
        viewModelScope.launch {
            var lectureTime = System.currentTimeMillis()
            while (true) {
                if (config == null) {
                    delay(1000)
                    continue
                }
                if (config!!["active"] != 1) {
                    delay(1000)
                    continue
                }

                val timePassed = System.currentTimeMillis() - lectureTime
                if (timePassed < config!!["delay_sensor"] as Int) {
                    delay(((config!!["delay_sensor"] as Int).div(2)).toLong())
                    continue
                }
                val senseA = 0
                val senseG = 0
                if (sensorData[0] - _oldSensorData[0] < senseA &&
                    sensorData[1] - _oldSensorData[1] < senseA &&
                    sensorData[2] - _oldSensorData[2] < senseA &&
                    sensorData[3] - _oldSensorData[3] < senseG &&
                    sensorData[4] - _oldSensorData[4] < senseG &&
                    sensorData[5] - _oldSensorData[5] < senseG
                ) {
                    continue
                }

                _oldSensorData[0] = sensorData[0]
                _oldSensorData[1] = sensorData[1]
                _oldSensorData[2] = sensorData[2]
                _oldSensorData[3] = sensorData[3]
                _oldSensorData[4] = sensorData[4]
                _oldSensorData[5] = sensorData[5]

                lectureTime = System.currentTimeMillis()

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

        /**
         * Configure the node
         */
        viewModelScope.launch {
            val countRestartFrom = System.currentTimeMillis()
            var timeUpdate = 0L
            while (true) {
                delay(10)
                if (config == null) {
                    continue
                }

                if (((config!!["time_reset"] as Int) > 0) &&
                    (System.currentTimeMillis() - countRestartFrom > (config!!["time_reset"] as Int) * 3600000)
                ) {
                    //restartActivity()
                    println("Restart activity")
                }

                if ((System.currentTimeMillis() - timeUpdate) <= (config!!["delay_update"] as Int) * 60000) {
//                        timeLeft = ((viewModel.config!!["delay_update"] as Int).times(60000)
//                            .minus(System.currentTimeMillis().minus(timeUpdate))).div(1000).toInt()
                    delay(1000)
                    continue
                }

                //viewModel.updating = true
                timeUpdate = System.currentTimeMillis()
                val newConfig: JSONObject? = try {
                    appModuleImpl.configService.getConfiguration(
                        """
                            {
                                "node": ${config!!["node"]},
                                "start": ${config!!["start"]}
                            }
                            """.trimIndent()
                    )
                } catch (e: Exception) {
                    println("Error fetching configuration: $e")
                    null
                }
                if (newConfig != null) {
                    config = newConfig
                }
                //viewModel.updating = false
            }
        }
    }
}