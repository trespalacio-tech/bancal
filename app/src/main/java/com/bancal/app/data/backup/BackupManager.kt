package com.bancal.app.data.backup

import android.content.Context
import android.net.Uri
import com.bancal.app.data.db.AppDatabase
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BackupManager(private val context: Context) {

    private val dbName = "bancal_database"

    /**
     * Nombre sugerido para el archivo de copia de seguridad.
     */
    fun suggestedFileName(): String {
        val ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"))
        return "bancal_backup_$ts.db"
    }

    /**
     * Exporta la base de datos actual al URI elegido por el usuario (vía SAF).
     * Cierra la BD antes de copiar para garantizar consistencia.
     */
    fun exportar(destUri: Uri): Result<Unit> {
        return try {
            // Checkpoint WAL para que todo esté en el archivo principal
            val db = AppDatabase.getInstance(context)
            db.openHelper.writableDatabase.execSQL("PRAGMA wal_checkpoint(TRUNCATE)")

            val dbFile = context.getDatabasePath(dbName)
            if (!dbFile.exists()) {
                return Result.failure(IOException("No se encontro la base de datos"))
            }

            context.contentResolver.openOutputStream(destUri)?.use { output ->
                dbFile.inputStream().use { input ->
                    input.copyTo(output)
                }
            } ?: return Result.failure(IOException("No se pudo abrir el destino"))

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Importa una copia de seguridad desde el URI elegido por el usuario.
     * Cierra la BD actual, sobreescribe el archivo y fuerza la reapertura.
     */
    fun importar(sourceUri: Uri): Result<Unit> {
        return try {
            val dbFile = context.getDatabasePath(dbName)
            val walFile = context.getDatabasePath("$dbName-wal")
            val shmFile = context.getDatabasePath("$dbName-shm")

            // Cerrar la base de datos actual
            AppDatabase.closeInstance()

            // Copiar el archivo de backup sobre la BD
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                dbFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return Result.failure(IOException("No se pudo abrir el archivo de copia"))

            // Eliminar archivos WAL/SHM para evitar inconsistencias
            walFile.delete()
            shmFile.delete()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
