package com.bancal.app.data.db

import androidx.room.TypeConverter
import com.bancal.app.domain.model.*

class Converters {
    @TypeConverter fun fromFamilia(value: FamiliaCultivo) = value.name
    @TypeConverter fun toFamilia(value: String) = FamiliaCultivo.valueOf(value)

    @TypeConverter fun fromNivelRiego(value: NivelRiego) = value.name
    @TypeConverter fun toNivelRiego(value: String) = NivelRiego.valueOf(value)

    @TypeConverter fun fromTipoSiembra(value: TipoSiembra) = value.name
    @TypeConverter fun toTipoSiembra(value: String) = TipoSiembra.valueOf(value)

    @TypeConverter fun fromEstado(value: EstadoPlantacion) = value.name
    @TypeConverter fun toEstado(value: String) = EstadoPlantacion.valueOf(value)

    @TypeConverter fun fromTipoTratamiento(value: TipoTratamiento) = value.name
    @TypeConverter fun toTipoTratamiento(value: String) = TipoTratamiento.valueOf(value)

    @TypeConverter fun fromTipoAsociacion(value: TipoAsociacion) = value.name
    @TypeConverter fun toTipoAsociacion(value: String) = TipoAsociacion.valueOf(value)

    @TypeConverter fun fromTipoAlerta(value: TipoAlerta) = value.name
    @TypeConverter fun toTipoAlerta(value: String) = TipoAlerta.valueOf(value)

    @TypeConverter fun fromCategoria(value: CategoriaBiointensiva) = value.name
    @TypeConverter fun toCategoria(value: String) = CategoriaBiointensiva.valueOf(value)

    @TypeConverter fun fromExigencia(value: ExigenciaNutricional) = value.name
    @TypeConverter fun toExigencia(value: String) = ExigenciaNutricional.valueOf(value)
}
