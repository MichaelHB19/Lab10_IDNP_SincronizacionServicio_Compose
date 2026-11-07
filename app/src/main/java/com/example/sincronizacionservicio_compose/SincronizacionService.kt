package com.example.sincronizacionservicio_compose

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class SincronizacionService : Service() {

    private var syncJob: Job? = null
    companion object {
        const val ACCION_SYNC_LISTA = "com.example.sincronizacionservicio_compose.ACCION_SYNC_LISTA"
        const val ACCION_SYNC_ERROR = "com.example.sincronizacionservicio_compose.ACCION_SYNC_ERROR"
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("SYNC", "1. Servicio iniciado")

        syncJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                delay(3000)
                //Simulación de fallo con 40% de probabilidad
                val hayFallo = Random.nextFloat() < 0.40f
                if (hayFallo) {
                    val intentError = Intent(ACCION_SYNC_ERROR).apply {
                        setPackage(packageName)
                        putExtra("mensaje_error", "Error 503: Falla de conexión con el servidor remoto.")
                    }
                    sendBroadcast(intentError)
                    Log.d("SYNC", "2. Aviso de error enviado por broadcast")
                } else {
                    val edificiosActualizados = (1..10).random()
                    val intentExito = Intent(ACCION_SYNC_LISTA).apply {
                        setPackage(packageName)
                        putExtra("edificios_actualizados", edificiosActualizados)
                    }
                    sendBroadcast(intentExito)
                    Log.d("SYNC", "2. Aviso de éxito enviado: $edificiosActualizados edificios")
                }
            } catch (e: Exception) {
                Log.d("SYNC", "Tarea interrumpida: ${e.message}")
            } finally {
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        syncJob?.cancel()
        Log.d("SYNC", "Servicio destruido")
    }
}