package com.neirarail.sensor_3

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.neirarail.sensor_3.ui.theme.Sensor_3Theme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.Instant

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var magnetometer: Sensor? = null
    private lateinit var viewModel: SensorDataViewModel

    private val sensorData: MutableList<Float> = mutableListOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private val oldSensorData: MutableList<Float> =
        mutableListOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

    val senseG = 0
    val senseA = 0

    private var timeout_lec = 0L
    private var time_lap = 0L

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[SensorDataViewModel::class.java]

        //Configuration
        fetchConfig()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        magnetometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        GlobalScope.launch(Dispatchers.Main) {
            while (true) {
                delay(1)
                if (viewModel.configured && viewModel.config != null) {
                    time_lap = (System.currentTimeMillis() - timeout_lec)
                    if (time_lap >= (viewModel.config?.get("delay_sensor") as Int)) {
                        timeout_lec = System.currentTimeMillis()

                        if (sensorData[0] - oldSensorData[0] > senseA ||
                            sensorData[1] - oldSensorData[1] > senseA ||
                            sensorData[2] - oldSensorData[2] > senseA ||
                            sensorData[3] - oldSensorData[3] > senseG ||
                            sensorData[4] - oldSensorData[4] > senseG ||
                            sensorData[5] - oldSensorData[5] > senseG
                        ) {
                            oldSensorData[0] = sensorData[0]
                            oldSensorData[1] = sensorData[1]
                            oldSensorData[2] = sensorData[2]
                            oldSensorData[3] = sensorData[3]
                            oldSensorData[4] = sensorData[4]
                            oldSensorData[5] = sensorData[5]

                            // Create a JSON object with accelerometer data, node and event
                            val json = """
                            {
                                "time": $timeout_lec,
                                "time_lap": $time_lap,
                                "event": 1,
                                "node": ${viewModel.config!!["node"]},
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

                            //println(json)

                            // Use coroutines to send the data to the server
                            lifecycleScope.launch {
                                viewModel.sendDataToServer(json)
                            }
                        }
                        else{
                            delay((viewModel.config!!.get("delay_sensor") as Int).toLong())
                        }
                    }
                }
            }
        }

        setContent {
            Sensor_3Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!viewModel.configured) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Cargando...")
                        }
                    } else {
                        Column {
                            val sendColor = if (viewModel.sending) Color.Black else Color.LightGray
                            NodeInfo(viewModel.config as JSONObject)
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(color = Color.LightGray)
                                        .padding(6.dp)
                                ) {
                                    Text(text = "Nodo ${viewModel.config!!["node"]}")
                                    Spacer(modifier = Modifier.padding(16.dp))
                                    //show detail without the \" at the start and the end
                                    Text(
                                        text = "Background",
                                        fontSize = 20.sp
                                    )
                                }
                                Text(text = "Sending...", color = sendColor)
                                Text(text = "Sensor data: $sensorData")
                            }
                        }

                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
//        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    @Composable
    private fun NodeInfo(config: JSONObject) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.LightGray)
                    .padding(6.dp)
            ) {
                Text(text = "Nodo ${config["node"]}")
                Spacer(modifier = Modifier.padding(16.dp))
                //show detail without the \" at the start and the end
                Text(
                    text = config["detail"].toString(),
                    fontSize = 20.sp
                )
            }
            for (key in config.keys()) {
                Text(text = "$key: ${config[key]}")
            }
        }

    }


    override fun onSensorChanged(event: SensorEvent?) {
        if (viewModel.configured && viewModel.config != null) {
            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                sensorData[0] = event.values[0]
                sensorData[1] = event.values[1]
                sensorData[2] = event.values[2]
            }

            if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
                sensorData[3] = event.values[0]
                sensorData[4] = event.values[1]
                sensorData[5] = event.values[2]
            }
            if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
                sensorData[6] = event.values[0]
                sensorData[7] = event.values[1]
                sensorData[8] = event.values[2]
            }
        }
    }

    private fun fetchConfig() {
        lifecycleScope.launch {
            viewModel.fetchConfiguration("{\"node\": 1, \"start\": 0}")
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // Do nothing
    }
}