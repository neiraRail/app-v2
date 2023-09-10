package com.neirarail.sensor_3

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject

class SensorDataViewModel : ViewModel() {
    private val httpClient = OkHttpClient()
    private val serverUrl = "http://129.151.100.69:8080"

    var config: JSONObject? by mutableStateOf(null)
    private var configFromServer: JSONObject? = null
    var systemMessage: String by mutableStateOf("")

    var configured: Boolean by mutableStateOf(false)
    var sending: Boolean by mutableStateOf(false)
    var listen: Boolean by mutableStateOf(false)
    var updating: Boolean by mutableStateOf(false)

    suspend fun sendDataToServer(json: String): Boolean {
        sending = true
        return try {
            withContext(Dispatchers.IO) {
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = json.toRequestBody(mediaType)


                val request = Request.Builder()
                    .url("$serverUrl/events")
                    .post(requestBody)
                    .build()

                val response: Response = httpClient.newCall(request).execute()
                println(response.body?.string())
                response.use {
                    val responseCode = it.code
                    responseCode == 200 // HTTP_OK
                }
            }
        } catch (e: Exception) {
            //e.printStackTrace()
            false
        } finally {
            sending = false
        }
    }

    suspend fun fetchConfiguration(json: String) {
        if (fetchConfigurationFromServer(json)) {
            config = configFromServer
            configured = true
        } else {
            systemMessage = "Hubo un error al obtener la configuración del servidor."
        }
    }


    suspend fun fetchConfigurationFromServer(json: String): Boolean {
        println("Fetching configuration...")
        var success = false // Initialize success to false

        withContext(Dispatchers.IO) {
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toRequestBody(mediaType)

            val request = Request.Builder()
                .url("$serverUrl/nodes/init")
                .header("Connection", "close")
                .post(requestBody)
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    if (response.body != null) {
                        println("Response successful and body is not null")
                        val configString: String = response.body!!.string()
                        configFromServer = JSONObject(configString)
                        success = true // Set success to true
                    } else {
                        println("Response successful but body is null")
                    }
                } else {
                    println("Response not successful")
                }
            }
        }

        return success // Return the success flag
    }

}

