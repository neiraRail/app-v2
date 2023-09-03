package com.neirarail.sensor_3

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.post
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import org.json.JSONObject

fun main() {
    val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    runBlocking {
        val requestJson = JSONObject(mapOf("node" to 0))

        val responseJson = client.post<JsonObject>("http://129.151.100.69:8080/init") {
            body = requestJson
        }

        println("Response JSON: $responseJson")
    }

    client.close()
}