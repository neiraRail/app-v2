package com.neirarail.sensor_3.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.Socket

class TelemetryService : TelemetryRepo {
    private val serverUrl = "http://200.13.4.208:8080"
    private val httpClient = OkHttpClient()
    private val udpSocket = DatagramSocket()
//    private val tcpSocket = Socket("200.13.4.208", 8079)

    private fun sendHttp(telemetry: String): Boolean {
        return try {
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = telemetry.toRequestBody(mediaType)


            val request = Request.Builder()
                .url("$serverUrl/lectura")
                .post(requestBody)
                .build()

            val response: Response = httpClient.newCall(request).execute()
            //println(response.body?.string())
            response.use {
                val responseCode = it.code
                //println("Data sended properly")
                responseCode == 200 // HTTP_OK
            }

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Uses udp socket to send telemetry
    //https://www.jesusninoc.com/07/04/crear-un-servidor-y-un-cliente-udp-en-kotlin/
    private fun sendUdp(telemetry: String): Boolean {
        return try {

            val buffer = telemetry.toByteArray()
            val direccion = InetAddress.getByName("200.13.4.208")

            val paquete = DatagramPacket(buffer, buffer.size, direccion, 8080)
            udpSocket.send(paquete)
            println("Mensaje enviado al servidor UDP")
            true

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Uses tcp socket to send telemetry
    //https://stackoverflow.com/questions/56535473/how-to-send-and-receive-strings-through-tcp-connection-using-kotlin
//    private suspend fun sendTcp(telemetry: String): Boolean {
//        return try {
//            withContext(Dispatchers.IO) {
//                tcpSocket.outputStream.write(telemetry.toByteArray())
//                true
//            }
//        }
//        catch (e: Exception){
//            false
//        }
//    }

    private fun sendMqtt(telemetry: String): Boolean {
        return true
    }


    override suspend fun sendTelemetry(telemetry: String, protocol: String): Boolean {
        when (protocol) {
            "http" -> {
                return sendHttp(telemetry)
            }

            "udp" -> {
                println("Sending telemetry using udp")
                return sendUdp(telemetry)
            }

            "tcp" -> {
                return true
//                return sendTcp(telemetry)
            }

            "mqtt" -> {
                return sendMqtt(telemetry)
            }

            else -> {
                return false
            }
        }
    }

}