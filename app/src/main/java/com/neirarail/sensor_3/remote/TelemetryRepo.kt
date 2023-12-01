package com.neirarail.sensor_3.remote

import org.json.JSONObject

interface TelemetryRepo {
    suspend fun sendTelemetry(telemetry: String, protocol: String): Boolean
}