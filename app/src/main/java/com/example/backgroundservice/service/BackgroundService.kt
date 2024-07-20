package com.example.backgroundservice.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.widget.Toast
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import android.os.Process
import android.util.Log

class BackgroundService: Service() {
    private var serviceLooper: Looper? = null
    private var serviceHandler: HandleService? = null

       override fun onCreate() {
        Log.i("Información","Creando servicio")
        super.onCreate()
        HandlerThread("BackgroundService",Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()
            serviceLooper = looper
            serviceHandler = HandleService(looper)
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int):
            Int {
        Log.i("Información", "Servicio iniciado")
        Toast.makeText(this, "Iniciando descarga", Toast.LENGTH_LONG).show()
        serviceHandler?.obtainMessage()?.also { message: Message ->
            message.arg1 = startId
            serviceHandler?.sendMessage(message)
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Log.i("Información","Servicio done")
        super.onDestroy()
        Toast.makeText(this, "Deteniendo servicio", Toast.LENGTH_LONG).show()
    }

    private fun dummyHttpRequest(): String? {
        val url = URL("https://randomuser.me/api/")
        return (url.openConnection() as? HttpURLConnection)?.run {
            requestMethod = "GET"
            inputStream.bufferedReader().readText()
        }
    }

    private inner class HandleService(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            try {
                thread {
                    println("dummy request: ${dummyHttpRequest()}")
                    val response = dummyHttpRequest()
                    val intent = Intent("com.example.backgroundservice.DATA_BROADCAST").apply {
                        putExtra("data", response)
                    }
                    sendBroadcast(intent)
                   // stopSelf(msg.arg1)
                }
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
            stopSelf(msg.arg1)// llama a stopSelf(msg.arg1) para detener el servicio
        }
    }


}