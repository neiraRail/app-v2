//package com.neirarail.sensor_3
//
//import android.content.Context
//import android.hardware.Sensor
//import android.hardware.SensorEvent
//import android.hardware.SensorEventListener
//import android.hardware.SensorManager
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.lifecycleScope
//import com.neirarail.sensor_3.ui.theme.Sensor_3Theme
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.DelicateCoroutinesApi
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import org.json.JSONObject
//
//@AndroidEntryPoint
//class MainActivity : ComponentActivity() {
//
//    private lateinit var viewModel: SensorDataViewModel
//
//    private val sensorData: MutableList<Float> = mutableListOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
//    private val oldSensorData: MutableList<Float> =
//        mutableListOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
//
//    val senseG = 0
//    val senseA = 0
//
//    private var timeout_lec = 0L
//    private var time_lap = 0L
//    private var timeleft: Int by mutableStateOf(0)
//
//    private val jobs = mutableListOf<Job>()
//
//
//    @OptIn(DelicateCoroutinesApi::class)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        viewModel = ViewModelProvider(this)[SensorDataViewModel::class.java]
//
//        //Configuration
//        fetchConfig()
//
//
//
//        GlobalScope.launch(Dispatchers.Main) {
//            while (true) {
//                delay(2)
//                if (viewModel.configured && viewModel.config != null && viewModel.listen) {
//
//                    time_lap = (System.currentTimeMillis() - timeout_lec)
//
//                    if (time_lap >= (viewModel.config?.get("delay_sensor") as Int)) {
//                        timeout_lec = System.currentTimeMillis()
//
//                        if (sensorData[0] - oldSensorData[0] > senseA ||
//                            sensorData[1] - oldSensorData[1] > senseA ||
//                            sensorData[2] - oldSensorData[2] > senseA ||
//                            sensorData[3] - oldSensorData[3] > senseG ||
//                            sensorData[4] - oldSensorData[4] > senseG ||
//                            sensorData[5] - oldSensorData[5] > senseG
//                        ) {
//                            oldSensorData[0] = sensorData[0]
//                            oldSensorData[1] = sensorData[1]
//                            oldSensorData[2] = sensorData[2]
//                            oldSensorData[3] = sensorData[3]
//                            oldSensorData[4] = sensorData[4]
//                            oldSensorData[5] = sensorData[5]
//
//                            // Create a JSON object with accelerometer data, node and event
//                            val json = """
//                            {
//                                "time": $timeout_lec,
//                                "time_lap": $time_lap,
//                                "event": 1,
//                                "node": ${viewModel.config!!["node"]},
//                                "acc_x": ${sensorData[0]},
//                                "acc_y": ${sensorData[1]},
//                                "acc_z": ${sensorData[2]},
//                                "gyr_x": ${sensorData[3]},
//                                "gyr_y": ${sensorData[4]},
//                                "gyr_z": ${sensorData[5]},
//                                "mx": ${sensorData[6]},
//                                "my": ${sensorData[7]},
//                                "mz": ${sensorData[8]},
//                                "tp": 0
//                            }
//                        """.trimIndent()
//
//                            println(json)
//
//                            // Use coroutines to send the data to the server
//                            lifecycleScope.launch {
//                                viewModel.sendDataToServer(json)
//                            }
//                        } else {
//                            delay((viewModel.config!!.get("delay_sensor") as Int).toLong())
//                        }
//                    }
//                }
//            }
//        }
//        val conf = GlobalScope.launch(Dispatchers.Main) {
//            val countRestartFrom = System.currentTimeMillis()
//            var time_update = 0L
//            while (true) {
//                delay(10)
//                if (viewModel.configured && viewModel.config != null) {
//                    // fetch configuration every viewModel.config()["time_reset"] minutes
//                    if ((System.currentTimeMillis() - time_update) >
//                        (viewModel.config!!["delay_update"] as Int) * 60000
//                    ) {
//                        viewModel.updating = true
//                        time_update = System.currentTimeMillis()
//                        fetchConfig()
//                        viewModel.updating = false
//                    } else {
//                        timeleft = ((viewModel.config!!["delay_update"] as Int).times(60000)
//                            .minus(System.currentTimeMillis().minus(time_update))).div(1000).toInt()
//                        delay(1000)
//                    }
//                    if (((viewModel.config!!["time_reset"] as Int) > 0) && (System.currentTimeMillis() - countRestartFrom > (viewModel.config!!["time_reset"] as Int) * 3600000)) { // reinicia cada time_reset horas
//                        restartActivity()
//                    }
//                }
//
//            }
//        }
//        jobs.add(conf)
//
//        setContent {
//            Sensor_3Theme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    if (viewModel.config == null) {
//                        Column(
//                            modifier = Modifier.fillMaxSize(),
//                            verticalArrangement = Arrangement.Center,
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            Text(text = "Cargando configuración...")
//                        }
//                    } else {
//                        if (!viewModel.configured) {
//                            Column(modifier = Modifier.padding(16.dp)) {
//                                for (key in viewModel.config!!.keys()) {
//                                    Text(text = "$key: ${viewModel.config!![key]}")
//                                }
//                            }
//                        } else {
//                            Column {
//                                NodeInfo(viewModel.config as JSONObject)
//
//                                val sendColor =
//                                    if (viewModel.sending) Color.Black else Color.LightGray
//
//                                val updateColor =
//                                    if (viewModel.updating) Color.Black else Color.LightGray
//
//                                Column(modifier = Modifier.padding(16.dp)) {
//                                    Row(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .background(color = Color.LightGray)
//                                            .padding(6.dp)
//                                    ) {
//                                        Text(text = "Nodo ${viewModel.config!!["node"]}")
//                                        Spacer(modifier = Modifier.padding(16.dp))
//                                        //show detail without the \" at the start and the end
//                                        Text(
//                                            text = "Background",
//                                            fontSize = 20.sp
//                                        )
//                                        Spacer(modifier = Modifier.padding(16.dp))
//                                        Button(onClick = { viewModel.listen = !viewModel.listen }) {
//                                            if (viewModel.listen) {
//                                                Text(text = "Stop")
//                                            } else {
//                                                Text(text = "Start")
//                                            }
//                                        }
//                                    }
//                                    Text(text = "Transmitiendo...", color = sendColor)
//
//                                    Text(
//                                        text = "Configurando en $timeleft segundos",
//                                        color = updateColor
//                                    )
//                                    Text(text = "Sensor data:")
//                                    for (value in sensorData) {
//                                        Text(text = "$value")
//                                    }
//                                }
//                            }
//                        }
//
//                    }
//
//                    Text(text = viewModel.systemMessage)
//                }
//            }
//        }
//    }
//
//    private fun restartActivity() {
//        for (job in jobs) {
//            job.cancel()
//        }
//        this.recreate()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        sensorManager.unregisterListener(this)
//    }
//
//    override fun onResume() {
//        super.onResume()
////        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
//    }
//
//    @Composable
//    private fun NodeInfo(config: JSONObject) {
//        Column(
//            modifier = Modifier
//                .padding(16.dp)
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(color = Color.LightGray)
//                    .padding(6.dp)
//            ) {
//                Text(text = "Nodo ${config["node"]}")
//                Spacer(modifier = Modifier.padding(16.dp))
//                //show detail without the \" at the start and the end
//                Text(
//                    text = config["detail"].toString(),
//                    fontSize = 20.sp
//                )
//            }
//            for (key in config.keys()) {
//                Text(text = "$key: ${config[key]}")
//            }
//        }
//
//    }
//
//    private fun fetchConfig() {
//        lifecycleScope.launch {
//            try {
//                viewModel.fetchConfiguration("{\"node\": 1, \"start\": 0}")
//            } catch (e: Exception) {
//                e.printStackTrace()
//                delay(3000)
//                restartActivity()
//            }
//        }
//    }
//
//    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
//        // Do nothing
//    }
//}