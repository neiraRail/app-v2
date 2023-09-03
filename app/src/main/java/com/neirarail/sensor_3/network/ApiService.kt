package com.neirarail.sensor_3.network


import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.json.JSONObject

class ApiService(
    private val client: HttpClient
) {
    suspend fun postSensorData(sensorData: JSONObject ): Boolean {
        return try {
            client.post{
                url("http://129.151.100.69:8080/events/")
                body = sensorData
            }
        } catch (e: Exception) {
            println( "Error: ${e.message}")
            false
        }
    }
    suspend fun updateConfig(config: JsonObject): JsonObject {
        println("updating configuration ---------------------------------------------------------")
        val res = try {
            client.post<JsonObject> {
                url("http://129.151.100.69:8080/nodes/init")
                contentType(ContentType.Application.Json)
                body = config
            }
        } catch (e: Exception) {
            println( "Error: ${e.message}")
            JsonObject(mapOf("err" to JsonPrimitive("Ocurrió algun error")))
        }
        return res
    }

    companion object {
        fun create(): ApiService {
            return ApiService(
                client = HttpClient(Android) {

                    install(JsonFeature) {
                        serializer = KotlinxSerializer()
                    }
                }
            )
        }
    }
}