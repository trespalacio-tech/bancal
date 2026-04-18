package com.bancal.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.bancal.app.ui.navigation.BancalNavHost
import com.bancal.app.ui.theme.BancalTheme
import com.bancal.app.worker.AlertaWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BancalTheme {
                BancalNavHost()
            }
        }

        lifecycleScope.launch(Dispatchers.Default) {
            scheduleAlertaWorker()
        }
    }

    private fun scheduleAlertaWorker() {
        val request = PeriodicWorkRequestBuilder<AlertaWorker>(
            12, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "alerta_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
