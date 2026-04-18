package com.bancal.app.ui.screens.alertas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bancal.app.data.db.AppDatabase
import com.bancal.app.data.db.entity.AlertaEntity
import com.bancal.app.data.repository.BancalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlertasViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = BancalRepository(
        db.cultivoDao(), db.plantacionDao(), db.tratamientoDao(),
        db.asociacionDao(), db.alertaDao(), db.diarioDao(), db.bancalDao()
    )

    val alertas: StateFlow<List<AlertaEntity>> = repository.getAllAlertas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun resolverAlerta(id: Long) {
        viewModelScope.launch {
            repository.resolverAlerta(id)
        }
    }

    fun limpiarResueltas() {
        viewModelScope.launch {
            repository.limpiarAlertasResueltas()
        }
    }
}
