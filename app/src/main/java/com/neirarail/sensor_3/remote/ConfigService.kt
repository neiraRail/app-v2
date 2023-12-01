package com.neirarail.sensor_3.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class ConfigService : ConfigRepo {

    private val httpClient = OkHttpClient()
    private val serverUrl = "http://200.13.4.208:8080"

    override suspend fun getConfiguration(json: String): JSONObject? {
        println("Fetching configuration...")

        val config: JSONObject? = withContext(Dispatchers.IO) {
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
                        JSONObject(configString)
                    } else {
                        println("Response successful but body is null")
                        null
                    }
                } else {
                    println("Response not successful")
                    null
                }
            }
        }
        return config
    }
}