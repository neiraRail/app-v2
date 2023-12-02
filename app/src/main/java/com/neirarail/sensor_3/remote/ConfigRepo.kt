package com.neirarail.sensor_3.remote

import org.json.JSONObject

interface ConfigRepo {
    suspend fun getConfiguration(json: String, serverUrl: String): JSONObject?
}