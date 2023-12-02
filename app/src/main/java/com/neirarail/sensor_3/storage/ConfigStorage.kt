package com.neirarail.sensor_3.storage

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File

class ConfigStorage(
    private val context: Context,
    private val fileName: String
){
    suspend fun readConfigFromStorage(): JSONObject? {
        return withContext(Dispatchers.IO) {
            println("Reading configuration from storage...")
            val file = File(context.filesDir, fileName)
            try {
                if (!file.exists()) {
                    null
                } else {
                    val content: String = file.readText()
                    JSONObject(content)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun writeConfigToStorage(config: JSONObject) {
        withContext(Dispatchers.IO){
            println("Writing configuration to storage...")
            val file = File(context.filesDir, fileName)
            try {
                file.writeText(config.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}