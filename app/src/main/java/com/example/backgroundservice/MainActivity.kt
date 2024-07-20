package com.example.backgroundservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.backgroundservice.service.BackgroundService


class MainActivity : AppCompatActivity() {

    //private lateinit var binding: ActivityMainBinding
    private lateinit var textView: TextView
    private lateinit var dataReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)
        val btnStart = this.findViewById(R.id.button) as AppCompatButton
        textView = findViewById(R.id.textView_respuesta)

        btnStart.setOnClickListener {
            Intent(this, BackgroundService::class.java).also {
                startService(it)
            }
        }


        // Inicializa el BroadcastReceiver
        dataReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.getStringExtra("data")?.let { data ->
                    textView.text = data
                }
            }
        }

        // Registra el BroadcastReceiver para recibir los datos del servicio
        registerReceiver(dataReceiver, IntentFilter("com.example.backgroundservice.DATA_BROADCAST"))
    }
    override fun onDestroy() {
        super.onDestroy()
        // Desregistra el BroadcastReceiver cuando la actividad se destruye
        unregisterReceiver(dataReceiver)
    }
}

