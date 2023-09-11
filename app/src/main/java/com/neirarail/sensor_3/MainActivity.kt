package com.neirarail.sensor_3

import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neirarail.sensor_3.presentation.MainViewModel
import com.neirarail.sensor_3.presentation.viewModelFactory

import com.neirarail.sensor_3.ui.theme.Sensor_3Theme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = viewModel<MainViewModel>(
                factory = viewModelFactory {
                    MainViewModel(
                        SensorApp.appModule
                    )
                }
            )

            Sensor_3Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (viewModel.config != null) {
                            Column(
                            ) {
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
                                        text = viewModel.config!!["detail"].toString(),
                                        fontSize = 20.sp
                                    )
                                }
                                for (key in viewModel.config!!.keys()) {
                                    Text(text = "$key: ${viewModel.config!![key]}")
                                }
                            }


                            Spacer(modifier = Modifier.padding(16.dp))
                            Column() {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(color = Color.LightGray)
                                        .padding(6.dp)
                                ) {
                                    Text(text = "Nodo ${viewModel.config?.get("node") ?: "null"}")
                                    Spacer(modifier = Modifier.padding(16.dp))
                                    //show detail without the \" at the start and the end
                                    Text(
                                        text = "Background",
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.padding(16.dp))
                                    Button(onClick = { viewModel.toggleActive() }) {
                                        if (viewModel.config!!["active"] == 1) {
                                            Text(text = "Stop")
                                        } else {
                                            Text(text = "Start")
                                        }
                                    }
                                }

                                Text(text = "Sensor data:")
                                for (i in 0..8) {
                                    Text(text = viewModel.sensorData[i].toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}