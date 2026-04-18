package com.bancal.app.ui.screens.diario

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bancal.app.data.db.AppDatabase
import com.bancal.app.data.db.entity.DiarioEntity
import com.bancal.app.data.repository.BancalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class DiarioStats(
    val entradas: Int = 0,
    val diasHelada: Int = 0,
    val totalCosechaKg: Float? = null,
    val totalLluviaMm: Float? = null
)

class DiarioViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = BancalRepository(
        db.cultivoDao(), db.plantacionDao(), db.tratamientoDao(),
        db.asociacionDao(), db.alertaDao(), db.diarioDao(), db.bancalDao()
    )

    private val zone = ZoneId.of("Europe/Madrid")

    val entradas: StateFlow<List<DiarioEntity>> = repository.getDiarioEntradas(50)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stats: StateFlow<DiarioStats> = kotlinx.coroutines.flow.combine(
        repository.countEntradasDiario(),
        repository.countDiasHelada(),
        repository.totalCosechaKg(),
        repository.totalLluviaMm()
    ) { entradas, heladas, cosecha, lluvia ->
        DiarioStats(entradas, heladas, cosecha, lluvia)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DiarioStats())

    // --- Formulario ---
    private val _showForm = MutableStateFlow(false)
    val showForm: StateFlow<Boolean> = _showForm

    private val _editando = MutableStateFlow<DiarioEntity?>(null)
    val editando: StateFlow<DiarioEntity?> = _editando

    fun mostrarFormularioHoy() {
        viewModelScope.launch {
            val hoy = LocalDate.now().atStartOfDay(zone).toInstant().toEpochMilli()
            val existente = repository.getDiarioByFecha(hoy)
            val entry = existente ?: DiarioEntity(fecha = hoy)
            _editando.value = entry
            _fotoPath.value = entry.fotoPath
            _showForm.value = true
        }
    }

    fun editarEntrada(entrada: DiarioEntity) {
        _editando.value = entrada
        _fotoPath.value = entrada.fotoPath
        _showForm.value = true
    }

    fun cerrarFormulario() {
        _showForm.value = false
        _editando.value = null
        _fotoPath.value = null
    }

    // --- Foto ---
    private val _fotoPath = MutableStateFlow<String?>(null)
    val fotoPath: StateFlow<String?> = _fotoPath

    private var _pendingPhotoUri: Uri? = null

    /**
     * Crea un archivo temporal para la cámara y devuelve su content URI.
     */
    fun crearUriParaFoto(): Uri {
        val app = getApplication<Application>()
        val dir = File(app.filesDir, "fotos_diario").apply { mkdirs() }
        val ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        val file = File(dir, "diario_$ts.jpg")
        _pendingPhotoUri = FileProvider.getUriForFile(app, "${app.packageName}.fileprovider", file)
        _fotoPath.value = file.absolutePath
        return _pendingPhotoUri!!
    }

    fun onFotoCapturada(success: Boolean) {
        if (!success) {
            // Borrar archivo vacío si la captura falló
            _fotoPath.value?.let { File(it).delete() }
            _fotoPath.value = _editando.value?.fotoPath
        }
    }

    fun onFotoSeleccionada(uri: Uri?) {
        if (uri == null) return
        val app = getApplication<Application>()
        val dir = File(app.filesDir, "fotos_diario").apply { mkdirs() }
        val ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        val destFile = File(dir, "diario_$ts.jpg")
        app.contentResolver.openInputStream(uri)?.use { input ->
            destFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        _fotoPath.value = destFile.absolutePath
    }

    fun eliminarFoto() {
        _fotoPath.value?.let { File(it).delete() }
        _fotoPath.value = null
    }

    fun guardar(
        tempMin: Int?,
        tempMax: Int?,
        lluviaMm: Float?,
        helada: Boolean,
        tareas: String,
        observaciones: String,
        cosechaKg: Float?,
        cosechaNotas: String
    ) {
        val base = _editando.value ?: return
        // Si se eliminó la foto anterior, borrar el archivo
        val fotoAnterior = base.fotoPath
        val fotoNueva = _fotoPath.value
        if (fotoAnterior != null && fotoAnterior != fotoNueva) {
            File(fotoAnterior).delete()
        }
        val entrada = base.copy(
            tempMin = tempMin,
            tempMax = tempMax,
            lluviaMm = lluviaMm,
            helada = helada,
            tareas = tareas,
            observaciones = observaciones,
            cosechaKg = cosechaKg,
            cosechaNotas = cosechaNotas,
            fotoPath = fotoNueva
        )
        viewModelScope.launch {
            if (entrada.id == 0L) {
                repository.insertDiario(entrada)
            } else {
                repository.updateDiario(entrada)
            }
            _showForm.value = false
            _editando.value = null
            _fotoPath.value = null
        }
    }

    fun eliminar(entrada: DiarioEntity) {
        viewModelScope.launch {
            entrada.fotoPath?.let { File(it).delete() }
            repository.deleteDiario(entrada)
        }
    }
}
