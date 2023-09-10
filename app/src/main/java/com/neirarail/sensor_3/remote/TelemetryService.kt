package com.neirarail.sensor_3.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class TelemetryService: TelemetryRepo {
    private val serverUrl = "http://129.151.100.69:8080"
    private val httpClient = OkHttpClient()

    override suspend fun sendTelemetry(telemetry: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = telemetry.toRequestBody(mediaType)


                val request = Request.Builder()
                    .url("$serverUrl/events")
                    .post(requestBody)
                    .build()

                val response: Response = httpClient.newCall(request).execute()
                println(response.body?.string())
                response.use {
                    val responseCode = it.code
                    println("Data sended properly")
                    responseCode == 200 // HTTP_OK
                }
            }
        } catch (e: Exception) {
            //e.printStackTrace()
            false
        }
    }
}