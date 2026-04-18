package com.bancal.app.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bancal.app.data.db.AppDatabase
import com.bancal.app.data.repository.BancalRepository
import com.bancal.app.domain.logic.AlertaEngine
import kotlinx.coroutines.flow.first

class AlertaWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "bancal_alertas"
        const val CHANNEL_NAME = "Alertas del Bancal"
        const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result {
        return try {
            val db = AppDatabase.getInstance(applicationContext)
            val repository = BancalRepository(
                db.cultivoDao(), db.plantacionDao(), db.tratamientoDao(),
                db.asociacionDao(), db.alertaDao(), db.diarioDao(), db.bancalDao()
            )
            val alertaEngine = AlertaEngine(repository)

            // Alertas para todos los bancales
            val plantaciones = repository.getTodasPlantacionesActivas().first()
            val cultivos = repository.getCultivos().first().associateBy { it.id }

            val nuevasAlertas = alertaEngine.generarAlertas(plantaciones, cultivos)

            for (alerta in nuevasAlertas) {
                repository.insertAlerta(alerta)
            }

            if (nuevasAlertas.isNotEmpty()) {
                sendNotification(nuevasAlertas.size)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("AlertaWorker", "Error generando alertas", e)
            Result.retry()
        }
    }

    private fun sendNotification(count: Int) {
        createNotificationChannel()

        if (ActivityCompat.checkSelfPermission(
                applicationContext, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Bancal")
            .setContentText(
                if (count == 1) "Tienes 1 nueva alerta en tu bancal"
                else "Tienes $count nuevas alertas en tu bancal"
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext)
            .notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Alertas de cosecha, trasplante, heladas y tratamientos"
        }
        val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}
