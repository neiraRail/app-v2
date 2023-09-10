package com.neirarail.sensor_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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