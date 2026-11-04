package com.example.sincronizacionservicio_compose

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.sincronizacionservicio_compose.ui.theme.SincronizacionServicio_ComposeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SincronizacionServicio_ComposeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PantallaSincronizacion()
                }
            }
        }
    }
}

@Composable
fun PantallaSincronizacion() {
    val context = LocalContext.current
    var resultado by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                Log.d("SYNC", "3. Aviso recibido en la pantalla")
                val cantidad = intent?.getIntExtra("edificios_actualizados", 0) ?: 0
                resultado = "Sincronización completa: $cantidad edificios actualizados"
            }
        }
        val filtro = IntentFilter("com.example.sincronizacionservicio_compose.ACCION_SYNC_LISTA")
        ContextCompat.registerReceiver(
            context,
            receiver,
            filtro,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            resultado = null
            context.startService(Intent(context, SincronizacionService::class.java))
        }) {
            Text("Sincronizar")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (resultado == null) {
            CircularProgressIndicator()
        } else {
            Text(resultado ?: "")
        }
    }
}