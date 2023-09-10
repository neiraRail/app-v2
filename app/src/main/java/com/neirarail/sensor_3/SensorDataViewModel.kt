//package com.neirarail.sensor_3
//
//import android.content.Context
//import android.hardware.Sensor
//import android.hardware.SensorEvent
//import android.hardware.SensorEventListener
//import android.hardware.SensorManager
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.core.content.ContextCompat.getSystemService
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.ViewModel
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody.Companion.toRequestBody
//import okhttp3.Response
//import org.json.JSONObject
//
//class SensorDataViewModel() : ViewModel(), SensorEventListener {
//    private val httpClient = OkHttpClient()
//    private val serverUrl = "http://129.151.100.69:8080"
//
//    private var sensorManager: SensorManager
//    private var accelerometer: Sensor? = null
//    private var gyroscope: Sensor? = null
//    private var magnetometer: Sensor? = null
//
//    var config: JSONObject? by mutableStateOf(null)
//    private var configFromServer: JSONObject? = null
//    var systemMessage: String by mutableStateOf("")
//
//    var configured: Boolean by mutableStateOf(false)
//    var sending: Boolean by mutableStateOf(false)
//    var listen: Boolean by mutableStateOf(false)
//    var updating: Boolean by mutableStateOf(false)
//
//    init {
//        sensorManager = .getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
//        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
//        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
//        accelerometer?.let {
//            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
//        }
//        gyroscope?.let {
//            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
//        }
//        magnetometer?.let {
//            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
//        }
//    }
//
//    suspend fun sendDataToServer(json: String): Boolean {
//        sending = true
//        return try {
//            withContext(Dispatchers.IO) {
//                val mediaType = "application/json; charset=utf-8".toMediaType()
//                val requestBody = json.toRequestBody(mediaType)
//
//
//                val request = Request.Builder()
//                    .url("$serverUrl/events")
//                    .post(requestBody)
//                    .build()
//
//                val response: Response = httpClient.newCall(request).execute()
//                println(response.body?.string())
//                response.use {
//                    val responseCode = it.code
//                    responseCode == 200 // HTTP_OK
//                }
//            }
//        } catch (e: Exception) {
//            //e.printStackTrace()
//            false
//        } finally {
//            sending = false
//        }
//    }
//
//    suspend fun fetchConfiguration(json: String) {
//        if (fetchConfigurationFromServer(json)) {
//            config = configFromServer
//            configured = true
//        } else {
//            systemMessage = "Hubo un error al obtener la configuración del servidor."
//        }
//    }
//
//
//    suspend fun fetchConfigurationFromServer(json: String): Boolean {
//        println("Fetching configuration...")
//        var success = false // Initialize success to false
//
//        withContext(Dispatchers.IO) {
//            val mediaType = "application/json; charset=utf-8".toMediaType()
//            val requestBody = json.toRequestBody(mediaType)
//
//            val request = Request.Builder()
//                .url("$serverUrl/nodes/init")
//                .header("Connection", "close")
//                .post(requestBody)
//                .build()
//
//            httpClient.newCall(request).execute().use { response ->
//                if (response.isSuccessful) {
//                    if (response.body != null) {
//                        println("Response successful and body is not null")
//                        val configString: String = response.body!!.string()
//                        configFromServer = JSONObject(configString)
//                        success = true // Set success to true
//                    } else {
//                        println("Response successful but body is null")
//                    }
//                } else {
//                    println("Response not successful")
//                }
//            }
//        }
//
//        return success // Return the success flag
//    }
//
//    override fun onSensorChanged(event: SensorEvent?) {
//        if (configured && config != null && listen) {
//            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
//                sensorData[0] = event.values[0]
//                sensorData[1] = event.values[1]
//                sensorData[2] = event.values[2]
//            }
//
//            if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
//                sensorData[3] = event.values[0]
//                sensorData[4] = event.values[1]
//                sensorData[5] = event.values[2]
//            }
//            if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
//                sensorData[6] = event.values[0]
//                sensorData[7] = event.values[1]
//                sensorData[8] = event.values[2]
//            }
//        }
//    }
//
//    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
//
//
//}
//
