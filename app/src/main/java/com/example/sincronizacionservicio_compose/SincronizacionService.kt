package com.example.sincronizacionservicio_compose

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
class SincronizacionService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("SYNC", "1. Servicio iniciado")

        CoroutineScope(Dispatchers.IO).launch {
            delay(3000)
            val edificiosActualizados = (1..10).random()
            val resultado = Intent("com.example.sincronizacionservicio_compose.ACCION_SYNC_LISTA")
            resultado.setPackage(packageName)
            resultado.putExtra("edificios_actualizados", edificiosActualizados)
            sendBroadcast(resultado)

            Log.d("SYNC", "2. Aviso enviado: $edificiosActualizados edificios")
            stopSelf()
        }
        return START_NOT_STICKY
    }
}