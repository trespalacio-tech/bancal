package com.bancal.app.ui.screens.calendario

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bancal.app.data.db.AppDatabase
import com.bancal.app.data.db.entity.CultivoEntity
import com.bancal.app.data.repository.BancalRepository
import com.bancal.app.domain.logic.CalendarioEngine
import com.bancal.app.domain.model.TipoSiembra
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class CalendarioViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = BancalRepository(
        db.cultivoDao(), db.plantacionDao(), db.tratamientoDao(),
        db.asociacionDao(), db.alertaDao(), db.diarioDao(), db.bancalDao()
    )

    private val _mesSeleccionado = MutableStateFlow(LocalDate.now().monthValue)
    val mesSeleccionado: StateFlow<Int> = _mesSeleccionado

    val cultivosPorTipo: StateFlow<Map<TipoSiembra, List<CultivoEntity>>> =
        combine(repository.getCultivos(), _mesSeleccionado) { cultivos, mes ->
            CalendarioEngine.cultivosParaMes(cultivos, mes)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun selectMes(mes: Int) {
        _mesSeleccionado.value = mes
    }
}
