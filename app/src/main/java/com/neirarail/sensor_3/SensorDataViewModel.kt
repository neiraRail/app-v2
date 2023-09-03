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
    var configured: Boolean by mutableStateOf(false)
    var config: JSONObject? by mutableStateOf(null)
    private val httpClient = OkHttpClient()
    var sending: Boolean by mutableStateOf(false)

    suspend fun sendDataToServer(json: String): Boolean {
        sending = true
        return try {
            withContext(Dispatchers.IO) {
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = json.toRequestBody(mediaType)
                val serverUrl = "http://129.151.100.69:8080/events"

                val request = Request.Builder()
                    .url(serverUrl)
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
        try {
            withContext(Dispatchers.IO) {
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = json.toRequestBody(mediaType)
                val serverUrl = "http://129.151.100.69:8080/nodes/init"

                val request = Request.Builder()
                    .url(serverUrl)
                    .post(requestBody)
                    .build()

                val response: Response = httpClient.newCall(request).execute()
                response.use {
                    if (response.isSuccessful) {
                        if(response.body != null){
                            config = JSONObject(response.body!!.string())
                            configured = true
                        } else {
                            config = null
                        }
                    } else {
                        config = null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            config = null
        }
    }
}

