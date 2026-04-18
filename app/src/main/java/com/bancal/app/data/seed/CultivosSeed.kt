package com.bancal.app.data.seed

import com.bancal.app.data.db.entity.AsociacionEntity
import com.bancal.app.data.db.entity.CultivoEntity
import com.bancal.app.domain.model.CategoriaBiointensiva.*
import com.bancal.app.domain.model.FamiliaCultivo.*
import com.bancal.app.domain.model.NivelRiego.*
import com.bancal.app.domain.model.ExigenciaNutricional.*
import com.bancal.app.domain.model.TipoAsociacion.*

/**
 * Base de datos de cultivos adaptados al clima de Burgos (zona 8b).
 * Datos de marcos de plantación y tiempos basados en:
 * "Introducción a la huerta regenerativa ecológica" - Agricultura Proactiva (sección 6.1)
 *
 * Clima continental: heladas tardías hasta mediados de abril, primeras heladas en octubre.
 * Meses de siembra ajustados a estas condiciones.
 *
 * Bitfield de meses: bit 0 = enero, bit 1 = febrero, ..., bit 11 = diciembre
 */
object CultivosSeed {

    private fun meses(vararg m: Int): Int = m.fold(0) { acc, mes -> acc or (1 shl (mes - 1)) }

    val cultivos = listOf(

        // =====================================================================
        // SOLANÁCEAS
        // =====================================================================
        CultivoEntity(
            id = 1, nombre = "Tomate", familia = SOLANACEAS, icono = "\uD83C\uDF45",
            marcoCm = 50, diasGerminacion = 8, diasCosecha = 98, // 14 semanas
            temperaturaMinima = 10, temperaturaOptima = 24,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(2, 3),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 0.5f, riego = ALTO, categoria = VEGETAL,
            lineasPorBancal = 1, semanasCosechando = 10, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Tomate tradicional. Necesita tutor. Despuntar a partir del 4º racimo."
        ),
        CultivoEntity(
            id = 41, nombre = "Tomate cherry", familia = SOLANACEAS, icono = "\uD83C\uDF45",
            marcoCm = 75, diasGerminacion = 8, diasCosecha = 84, // 12 semanas
            temperaturaMinima = 10, temperaturaOptima = 24,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(2, 3),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 0.5f, riego = ALTO, categoria = VEGETAL,
            lineasPorBancal = 1, semanasCosechando = 10, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Muy productivo. Necesita tutor alto o jaula."
        ),
        CultivoEntity(
            id = 42, nombre = "Tomate F1", familia = SOLANACEAS, icono = "\uD83C\uDF45",
            marcoCm = 50, diasGerminacion = 8, diasCosecha = 98, // 14 semanas
            temperaturaMinima = 10, temperaturaOptima = 24,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(2, 3),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 0.5f, riego = ALTO, categoria = VEGETAL,
            lineasPorBancal = 1, semanasCosechando = 10, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Híbrido. Mayor producción pero no guarda semillas."
        ),
        CultivoEntity(
            id = 43, nombre = "Tomate determinado", familia = SOLANACEAS, icono = "\uD83C\uDF45",
            marcoCm = 50, diasGerminacion = 8, diasCosecha = 84, // 12 semanas
            temperaturaMinima = 10, temperaturaOptima = 24,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(2, 3),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 0.5f, riego = ALTO, categoria = VEGETAL,
            lineasPorBancal = 1, semanasCosechando = 8, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "No necesita despunte. Cosecha concentrada."
        ),
        CultivoEntity(
            id = 2, nombre = "Pimiento", familia = SOLANACEAS, icono = "\uD83C\uDF36\uFE0F",
            marcoCm = 40, diasGerminacion = 12, diasCosecha = 91, // 13 semanas
            temperaturaMinima = 12, temperaturaOptima = 25,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(2, 3),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 0.5f, riego = ALTO, categoria = VEGETAL,
            lineasPorBancal = 2, semanasCosechando = 12, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Proteger del viento. Sensible a heladas."
        ),
        CultivoEntity(
            id = 44, nombre = "Pimiento gordo", familia = SOLANACEAS, icono = "\uD83C\uDF36\uFE0F",
            marcoCm = 40, diasGerminacion = 12, diasCosecha = 105, // 15 semanas
            temperaturaMinima = 12, temperaturaOptima = 25,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(2, 3),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 0.5f, riego = ALTO, categoria = VEGETAL,
            lineasPorBancal = 2, semanasCosechando = 8, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Tipo lamuyo o california. Necesita calor prolongado."
        ),
        CultivoEntity(
            id = 45, nombre = "Pimiento pequeño", familia = SOLANACEAS, icono = "\uD83C\uDF36\uFE0F",
            marcoCm = 40, diasGerminacion = 12, diasCosecha = 70, // 10 semanas
            temperaturaMinima = 12, temperaturaOptima = 25,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(2, 3),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 0.5f, riego = ALTO, categoria = VEGETAL,
            lineasPorBancal = 2, semanasCosechando = 12, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Tipo padrón o piquillo. Muy productivo."
        ),
        CultivoEntity(
            id = 46, nombre = "Pimiento picante", familia = SOLANACEAS, icono = "\uD83C\uDF36\uFE0F",
            marcoCm = 40, diasGerminacion = 14, diasCosecha = 70, // 10 semanas
            temperaturaMinima = 14, temperaturaOptima = 26,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(2, 3),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 0.5f, riego = MEDIO, categoria = VEGETAL,
            lineasPorBancal = 2, semanasCosechando = 8, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Guindilla, cayena, jalapeño. Se puede secar."
        ),
        CultivoEntity(
            id = 3, nombre = "Berenjena", familia = SOLANACEAS, icono = "\uD83C\uDF46",
            marcoCm = 50, diasGerminacion = 14, diasCosecha = 70, // 10 semanas
            temperaturaMinima = 14, temperaturaOptima = 26,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(2, 3),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 0.5f, riego = ALTO, categoria = VEGETAL,
            lineasPorBancal = 2, semanasCosechando = 12, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Muy sensible al frío. En Burgos solo a pleno sol."
        ),
        CultivoEntity(
            id = 4, nombre = "Patata", familia = SOLANACEAS, icono = "\uD83E\uDD54",
            marcoCm = 40, diasGerminacion = 15, diasCosecha = 91, // 13 semanas
            temperaturaMinima = 5, temperaturaOptima = 18,
            mesesSiembraDirecta = meses(3, 4),
            mesesSemillero = 0,
            mesesTrasplante = 0,
            profundidadSiembraCm = 10f, riego = MEDIO, categoria = CALORICO,
            lineasPorBancal = 1, semanasCosechando = 1, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = true, admitePlantel = false,
            notas = "Aporcar cuando la planta tenga 20cm. No asociar con tomate."
        ),
        CultivoEntity(
            id = 47, nombre = "Boniato", familia = SOLANACEAS, icono = "\uD83C\uDF60",
            marcoCm = 30, diasGerminacion = 14, diasCosecha = 112, // 16 semanas
            temperaturaMinima = 14, temperaturaOptima = 26,
            mesesSiembraDirecta = meses(5),
            mesesSemillero = 0,
            mesesTrasplante = 0,
            profundidadSiembraCm = 8f, riego = MEDIO, categoria = CALORICO,
            lineasPorBancal = 2, semanasCosechando = 1, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = true, admitePlantel = false,
            notas = "Necesita mucho calor. Plantar esquejes. Cosechar antes de heladas."
        ),

        // =====================================================================
        // CUCURBITÁCEAS
        // =====================================================================
        CultivoEntity(
            id = 5, nombre = "Calabacín", familia = CUCURBITACEAS, icono = "\uD83E\uDD52",
            marcoCm = 80, diasGerminacion = 7, diasCosecha = 63, // 9 semanas
            temperaturaMinima = 10, temperaturaOptima = 25,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(4),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 2f, riego = ALTO, categoria = VEGETAL,
            lineasPorBancal = 1, semanasCosechando = 10, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Muy productivo. Cosechar jóvenes para más producción."
        ),
        CultivoEntity(
            id = 6, nombre = "Calabaza", familia = CUCURBITACEAS, icono = "\uD83C\uDF83",
            marcoCm = 100, diasGerminacion = 8, diasCosecha = 84, // 12 semanas
            temperaturaMinima = 10, temperaturaOptima = 25,
            mesesSiembraDirecta = meses(5),
            mesesSemillero = meses(4),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 3f, riego = MEDIO, categoria = CALORICO,
            lineasPorBancal = 1, semanasCosechando = 8, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = true, admitePlantel = true,
            notas = "Calabaza pequeña tipo butternut. Puede trepar con tutor."
        ),
        CultivoEntity(
            id = 48, nombre = "Calabaza grande", familia = CUCURBITACEAS, icono = "\uD83C\uDF83",
            marcoCm = 100, diasGerminacion = 8, diasCosecha = 112, // 16 semanas
            temperaturaMinima = 10, temperaturaOptima = 25,
            mesesSiembraDirecta = meses(5),
            mesesSemillero = meses(4),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 3f, riego = MEDIO, categoria = CALORICO,
            lineasPorBancal = 1, semanasCosechando = 8, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = true, admitePlantel = true,
            notas = "Ocupa mucho espacio. Cosechar antes de heladas."
        ),
        CultivoEntity(
            id = 7, nombre = "Pepino", familia = CUCURBITACEAS, icono = "\uD83E\uDD52",
            marcoCm = 40, diasGerminacion = 6, diasCosecha = 42, // 6 semanas
            temperaturaMinima = 12, temperaturaOptima = 25,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(4),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 2f, riego = ALTO, categoria = VEGETAL,
            lineasPorBancal = 2, semanasCosechando = 4, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Necesita tutor o enrejado. Cosechar frecuentemente."
        ),
        CultivoEntity(
            id = 8, nombre = "Melón", familia = CUCURBITACEAS, icono = "\uD83C\uDF48",
            marcoCm = 80, diasGerminacion = 8, diasCosecha = 84, // 12 semanas
            temperaturaMinima = 14, temperaturaOptima = 28,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 2f, riego = MEDIO, categoria = VEGETAL,
            lineasPorBancal = 1, semanasCosechando = 4, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Necesita mucho calor. Acolchar en Burgos."
        ),
        CultivoEntity(
            id = 9, nombre = "Sandía", familia = CUCURBITACEAS, icono = "\uD83C\uDF49",
            marcoCm = 80, diasGerminacion = 8, diasCosecha = 84, // 12 semanas
            temperaturaMinima = 14, temperaturaOptima = 28,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 2f, riego = MEDIO, categoria = VEGETAL,
            lineasPorBancal = 1, semanasCosechando = 8, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Necesita mucho calor. Difícil en Burgos sin protección."
        ),

        // =====================================================================
        // LEGUMINOSAS
        // =====================================================================
        CultivoEntity(
            id = 10, nombre = "Judía baja", familia = LEGUMINOSAS, icono = "\uD83E\uDED8",
            marcoCm = 20, diasGerminacion = 8, diasCosecha = 56, // 8 semanas
            temperaturaMinima = 10, temperaturaOptima = 22,
            mesesSiembraDirecta = meses(5, 6, 7),
            mesesSemillero = meses(4, 5),
            mesesTrasplante = meses(5, 6, 7),
            profundidadSiembraCm = 3f, riego = MEDIO, categoria = CALORICO, intervaloSucesionDias = 21,
            lineasPorBancal = 3, semanasCosechando = 5, exigencia = NADA_EXIGENTE,
            admiteSiembraDirecta = true, admitePlantel = true,
            notas = "Judía de mata baja. Fija nitrógeno. Siembra escalonada cada 3 semanas."
        ),
        CultivoEntity(
            id = 49, nombre = "Judía de enrame", familia = LEGUMINOSAS, icono = "\uD83E\uDED8",
            marcoCm = 10, diasGerminacion = 8, diasCosecha = 63, // 9 semanas
            temperaturaMinima = 10, temperaturaOptima = 22,
            mesesSiembraDirecta = meses(5, 6, 7),
            mesesSemillero = meses(4, 5),
            mesesTrasplante = meses(5, 6, 7),
            profundidadSiembraCm = 3f, riego = MEDIO, categoria = CALORICO,
            lineasPorBancal = 2, semanasCosechando = 6, exigencia = NADA_EXIGENTE,
            admiteSiembraDirecta = true, admitePlantel = true,
            notas = "Necesita tutor alto. Fija nitrógeno. Muy productiva."
        ),
        CultivoEntity(
            id = 11, nombre = "Guisante", familia = LEGUMINOSAS, icono = "\uD83E\uDED1",
            marcoCm = 10, diasGerminacion = 10, diasCosecha = 84, // 12 semanas
            temperaturaMinima = 2, temperaturaOptima = 16,
            mesesSiembraDirecta = meses(2, 3, 10, 11),
            mesesSemillero = meses(1, 2, 9, 10),
            mesesTrasplante = meses(2, 3, 10, 11),
            profundidadSiembraCm = 3f, riego = BAJO, categoria = CALORICO, intervaloSucesionDias = 21,
            lineasPorBancal = 2, semanasCosechando = 2, exigencia = NADA_EXIGENTE,
            admiteSiembraDirecta = true, admitePlantel = true,
            notas = "Tolera heladas suaves. Siembra otoñal posible en Burgos con protección."
        ),
        CultivoEntity(
            id = 12, nombre = "Haba", familia = LEGUMINOSAS, icono = "\uD83E\uDED8",
            marcoCm = 15, diasGerminacion = 10, diasCosecha = 84, // 12 semanas
            temperaturaMinima = 0, temperaturaOptima = 15,
            mesesSiembraDirecta = meses(2, 3, 10, 11),
            mesesSemillero = meses(1, 2, 9, 10),
            mesesTrasplante = meses(2, 3, 10, 11),
            profundidadSiembraCm = 5f, riego = BAJO, categoria = CALORICO,
            lineasPorBancal = 3, semanasCosechando = 2, exigencia = NADA_EXIGENTE,
            admiteSiembraDirecta = true, admitePlantel = true,
            notas = "Muy resistente al frío. Despuntar al cuajar para evitar pulgón."
        ),

        // =====================================================================
        // CRUCÍFERAS
        // =====================================================================
        CultivoEntity(
            id = 13, nombre = "Col", familia = CRUCIFERAS, icono = "\uD83E\uDD66",
            marcoCm = 40, diasGerminacion = 7, diasCosecha = 70, // 10 semanas
            temperaturaMinima = 0, temperaturaOptima = 17,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4, 7, 8),
            mesesTrasplante = meses(4, 5, 8, 9),
            profundidadSiembraCm = 1f, riego = MEDIO, categoria = VEGETAL,
            lineasPorBancal = 2, semanasCosechando = 6, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Col puntuda/pico. Resistente al frío. Vigilar oruga de la col."
        ),
        CultivoEntity(
            id = 16, nombre = "Repollo", familia = CRUCIFERAS, icono = "\uD83E\uDD66",
            marcoCm = 40, diasGerminacion = 7, diasCosecha = 63, // 9 semanas
            temperaturaMinima = -2, temperaturaOptima = 16,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4, 7, 8),
            mesesTrasplante = meses(4, 5, 8, 9),
            profundidadSiembraCm = 1f, riego = MEDIO, categoria = VEGETAL,
            lineasPorBancal = 2, semanasCosechando = 6, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Col lisa. Muy resistente al frío burgalés."
        ),
        CultivoEntity(
            id = 50, nombre = "Col de Bruselas", familia = CRUCIFERAS, icono = "\uD83E\uDD66",
            marcoCm = 40, diasGerminacion = 7, diasCosecha = 112, // 16 semanas
            temperaturaMinima = -5, temperaturaOptima = 16,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 1f, riego = MEDIO, categoria = VEGETAL,
            lineasPorBancal = 2, semanasCosechando = 12, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Ciclo largo. Mejora con las heladas. Ideal para Burgos."
        ),
        CultivoEntity(
            id = 51, nombre = "Col rizada", familia = CRUCIFERAS, icono = "\uD83E\uDD6C",
            marcoCm = 40, diasGerminacion = 7, diasCosecha = 84, // 12 semanas
            temperaturaMinima = -5, temperaturaOptima = 16,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4, 7, 8),
            mesesTrasplante = meses(4, 5, 8, 9),
            profundidadSiembraCm = 1f, riego = MEDIO, categoria = VEGETAL,
            lineasPorBancal = 2, semanasCosechando = 8, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Col rizada tipo berza. Muy resistente al frío."
        ),
        CultivoEntity(
            id = 14, nombre = "Brócoli", familia = CRUCIFERAS, icono = "\uD83E\uDD66",
            marcoCm = 40, diasGerminacion = 7, diasCosecha = 70, // 10 semanas
            temperaturaMinima = 0, temperaturaOptima = 17,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4, 7, 8),
            mesesTrasplante = meses(4, 5, 8, 9),
            profundidadSiembraCm = 1f, riego = MEDIO, categoria = VEGETAL,
            lineasPorBancal = 2, semanasCosechando = 3, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Cortar la pella central para que salgan brotes laterales."
        ),
        CultivoEntity(
            id = 15, nombre = "Coliflor", familia = CRUCIFERAS, icono = "\uD83E\uDD66",
            marcoCm = 40, diasGerminacion = 7, diasCosecha = 84, // 12 semanas
            temperaturaMinima = 0, temperaturaOptima = 17,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4, 7),
            mesesTrasplante = meses(4, 5, 8, 9),
            profundidadSiembraCm = 1f, riego = MEDIO, categoria = VEGETAL,
            lineasPorBancal = 2, semanasCosechando = 3, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Coliflor temprana. Tapar la pella con las hojas para blanquear."
        ),
        CultivoEntity(
            id = 52, nombre = "Coliflor tardía", familia = CRUCIFERAS, icono = "\uD83E\uDD66",
            marcoCm = 40, diasGerminacion = 7, diasCosecha = 133, // 19 semanas
            temperaturaMinima = -2, temperaturaOptima = 16,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(5, 6),
            mesesTrasplante = meses(7, 8),
            profundidadSiembraCm = 1f, riego = MEDIO, categoria = VEGETAL,
            lineasPorBancal = 2, semanasCosechando = 4, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Ciclo largo para cosecha de invierno."
        ),
        CultivoEntity(
            id = 53, nombre = "Romanesco", familia = CRUCIFERAS, icono = "\uD83E\uDD66",
            marcoCm = 40, diasGerminacion = 7, diasCosecha = 77, // 11 semanas
            temperaturaMinima = 0, temperaturaOptima = 17,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4, 7),
            mesesTrasplante = meses(4, 5, 8, 9),
            profundidadSiembraCm = 1f, riego = MEDIO, categoria = VEGETAL,
            lineasPorBancal = 2, semanasCosechando = 3, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Similar a coliflor pero más decorativo. Sabor más suave."
        ),
        CultivoEntity(
            id = 17, nombre = "Kale", familia = CRUCIFERAS, icono = "\uD83E\uDD66",
            marcoCm = 40, diasGerminacion = 7, diasCosecha = 63, // 9 semanas
            temperaturaMinima = -5, temperaturaOptima = 16,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4, 7, 8),
            mesesTrasplante = meses(4, 5, 8, 9),
            profundidadSiembraCm = 1f, riego = MEDIO, categoria = VEGETAL,
            lineasPorBancal = 2, semanasCosechando = 16, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Mejora el sabor con las heladas. Ideal para Burgos."
        ),
        CultivoEntity(
            id = 54, nombre = "Colinabo", familia = CRUCIFERAS, icono = "\uD83E\uDD6C",
            marcoCm = 40, diasGerminacion = 7, diasCosecha = 70, // 10 semanas
            temperaturaMinima = 0, temperaturaOptima = 17,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4, 7, 8),
            mesesTrasplante = meses(4, 5, 8, 9),
            profundidadSiembraCm = 1f, riego = MEDIO, categoria = CALORICO,
            lineasPorBancal = 3, semanasCosechando = 2, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Raíz engrosada tipo nabo. Se comen raíz y hojas."
        ),
        CultivoEntity(
            id = 55, nombre = "Colirábano", familia = CRUCIFERAS, icono = "\uD83E\uDD6C",
            marcoCm = 40, diasGerminacion = 5, diasCosecha = 70, // 10 semanas
            temperaturaMinima = 0, temperaturaOptima = 17,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4, 7, 8),
            mesesTrasplante = meses(4, 5, 8, 9),
            profundidadSiembraCm = 1f, riego = MEDIO, categoria = VEGETAL,
            lineasPorBancal = 3, semanasCosechando = 1, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Tallo engrosado. Cosechar cuando tenga 5-8cm de diámetro."
        ),
        CultivoEntity(
            id = 18, nombre = "Rabanito", familia = CRUCIFERAS, icono = "\uD83E\uDD52",
            marcoCm = 10, diasGerminacion = 4, diasCosecha = 21, // 3 semanas
            temperaturaMinima = 2, temperaturaOptima = 16,
            mesesSiembraDirecta = meses(3, 4, 5, 6, 7, 8, 9),
            mesesSemillero = 0,
            mesesTrasplante = 0,
            profundidadSiembraCm = 1f, riego = MEDIO, intervaloSucesionDias = 10,
            lineasPorBancal = 5, semanasCosechando = 4, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = true, admitePlantel = false,
            notas = "Cultivo rápido, ideal para intercalar. Siembra escalonada cada 10 días."
        ),
        CultivoEntity(
            id = 56, nombre = "Rábano de invierno", familia = CRUCIFERAS, icono = "\uD83E\uDD52",
            marcoCm = 15, diasGerminacion = 5, diasCosecha = 35, // 5 semanas
            temperaturaMinima = 0, temperaturaOptima = 15,
            mesesSiembraDirecta = meses(7, 8, 9),
            mesesSemillero = meses(7, 8),
            mesesTrasplante = meses(8, 9),
            profundidadSiembraCm = 1.5f, riego = MEDIO,
            lineasPorBancal = 4, semanasCosechando = 8, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = true, admitePlantel = true,
            notas = "Daikon y similares. Más grande que el rabanito. Se conserva bien."
        ),
        CultivoEntity(
            id = 19, nombre = "Nabo", familia = CRUCIFERAS, icono = "\uD83E\uDD52",
            marcoCm = 30, diasGerminacion = 5, diasCosecha = 42, // 6 semanas
            temperaturaMinima = 0, temperaturaOptima = 15,
            mesesSiembraDirecta = meses(3, 4, 8, 9),
            mesesSemillero = 0,
            mesesTrasplante = 0,
            profundidadSiembraCm = 1f, riego = MEDIO, categoria = CALORICO, intervaloSucesionDias = 21,
            lineasPorBancal = 3, semanasCosechando = 12, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = true, admitePlantel = false,
            notas = "Nabiza/Grelos. Se comen hojas y raíz. Resistente al frío."
        ),
        CultivoEntity(
            id = 57, nombre = "Rúcula", familia = CRUCIFERAS, icono = "\uD83E\uDD6C",
            marcoCm = 10, diasGerminacion = 5, diasCosecha = 35, // 5 semanas
            temperaturaMinima = 2, temperaturaOptima = 17,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4, 8, 9),
            mesesTrasplante = meses(4, 5, 9, 10),
            profundidadSiembraCm = 0.5f, riego = MEDIO, intervaloSucesionDias = 14,
            lineasPorBancal = 5, semanasCosechando = 8, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Sabor picante. Espiga rápido con calor. Mejor en otoño y primavera."
        ),
        CultivoEntity(
            id = 58, nombre = "Mizuna", familia = CRUCIFERAS, icono = "\uD83E\uDD6C",
            marcoCm = 30, diasGerminacion = 5, diasCosecha = 35, // 5 semanas
            temperaturaMinima = 0, temperaturaOptima = 17,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4, 8, 9),
            mesesTrasplante = meses(4, 5, 9, 10),
            profundidadSiembraCm = 0.5f, riego = MEDIO,
            lineasPorBancal = 5, semanasCosechando = 8, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Hoja asiática suave. Tolera frío. Buena para ensaladas."
        ),
        CultivoEntity(
            id = 59, nombre = "Mostaza", familia = CRUCIFERAS, icono = "\uD83C\uDF3F",
            marcoCm = 45, diasGerminacion = 5, diasCosecha = 35, // 5 semanas
            temperaturaMinima = 2, temperaturaOptima = 18,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4, 8, 9),
            mesesTrasplante = meses(4, 5, 9, 10),
            profundidadSiembraCm = 0.5f, riego = MEDIO,
            lineasPorBancal = 5, semanasCosechando = 8, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Hojas picantes para ensalada. Crece rápido."
        ),
        CultivoEntity(
            id = 60, nombre = "Pak choi", familia = CRUCIFERAS, icono = "\uD83E\uDD6C",
            marcoCm = 30, diasGerminacion = 5, diasCosecha = 35, // 5 semanas
            temperaturaMinima = 2, temperaturaOptima = 18,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4, 8, 9),
            mesesTrasplante = meses(4, 5, 9, 10),
            profundidadSiembraCm = 0.5f, riego = MEDIO,
            lineasPorBancal = 3, semanasCosechando = 4, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Col china de tallo blanco. Rápida y versátil."
        ),
        CultivoEntity(
            id = 61, nombre = "Tatsoi", familia = CRUCIFERAS, icono = "\uD83E\uDD6C",
            marcoCm = 30, diasGerminacion = 5, diasCosecha = 42, // 6 semanas
            temperaturaMinima = -5, temperaturaOptima = 17,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4, 8, 9),
            mesesTrasplante = meses(4, 5, 9, 10),
            profundidadSiembraCm = 0.5f, riego = MEDIO,
            lineasPorBancal = 5, semanasCosechando = 4, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Roseta compacta. Muy resistente al frío. Ideal para Burgos."
        ),
        CultivoEntity(
            id = 62, nombre = "Tokyo bekana", familia = CRUCIFERAS, icono = "\uD83E\uDD6C",
            marcoCm = 30, diasGerminacion = 5, diasCosecha = 42, // 6 semanas
            temperaturaMinima = 0, temperaturaOptima = 17,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4, 8, 9),
            mesesTrasplante = meses(4, 5, 9, 10),
            profundidadSiembraCm = 0.5f, riego = MEDIO,
            lineasPorBancal = 3, semanasCosechando = 4, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Col china de hoja ligera. Textura crujiente."
        ),
        CultivoEntity(
            id = 63, nombre = "Wasabino", familia = CRUCIFERAS, icono = "\uD83E\uDD6C",
            marcoCm = 30, diasGerminacion = 5, diasCosecha = 42, // 6 semanas
            temperaturaMinima = 0, temperaturaOptima = 17,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4, 8, 9),
            mesesTrasplante = meses(4, 5, 9, 10),
            profundidadSiembraCm = 0.5f, riego = MEDIO,
            lineasPorBancal = 5, semanasCosechando = 4, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Hoja de mostaza japonesa. Sabor picante tipo wasabi."
        ),
        CultivoEntity(
            id = 64, nombre = "Pet sai", familia = CRUCIFERAS, icono = "\uD83E\uDD6C",
            marcoCm = 30, diasGerminacion = 5, diasCosecha = 35, // 5 semanas
            temperaturaMinima = 2, temperaturaOptima = 18,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(7, 8),
            mesesTrasplante = meses(8, 9),
            profundidadSiembraCm = 0.5f, riego = MEDIO,
            lineasPorBancal = 3, semanasCosechando = 4, exigencia = MUY_EXIGENTE,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Col china alargada. Mejor en otoño para evitar espigado."
        ),

        // =====================================================================
        // LILIÁCEAS
        // =====================================================================
        CultivoEntity(
            id = 20, nombre = "Cebolla", familia = LILIÁCEAS, icono = "\uD83E\uDDC5",
            marcoCm = 25, diasGerminacion = 12, diasCosecha = 112, // 16 semanas
            temperaturaMinima = 0, temperaturaOptima = 18,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(1, 2),
            mesesTrasplante = meses(3, 4),
            profundidadSiembraCm = 1f, riego = BAJO, categoria = CALORICO,
            lineasPorBancal = 4, semanasCosechando = 1,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Cebolla para guardar. Dejar de regar cuando dobla el cuello."
        ),
        CultivoEntity(
            id = 65, nombre = "Cebolla fresca", familia = LILIÁCEAS, icono = "\uD83E\uDDC5",
            marcoCm = 25, diasGerminacion = 12, diasCosecha = 70, // 10 semanas
            temperaturaMinima = 0, temperaturaOptima = 18,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(1, 2),
            mesesTrasplante = meses(3, 4),
            profundidadSiembraCm = 1f, riego = BAJO, categoria = VEGETAL,
            lineasPorBancal = 5, semanasCosechando = 8,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Cebolla tierna para consumo fresco. No se conserva."
        ),
        CultivoEntity(
            id = 66, nombre = "Cebolla japonesa", familia = LILIÁCEAS, icono = "\uD83E\uDDC5",
            marcoCm = 25, diasGerminacion = 12, diasCosecha = 56, // 8 semanas
            temperaturaMinima = -5, temperaturaOptima = 15,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(7, 8),
            mesesTrasplante = meses(9, 10),
            profundidadSiembraCm = 1f, riego = BAJO, categoria = VEGETAL,
            lineasPorBancal = 5, semanasCosechando = 8,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Se planta en verano para cosecha de primavera. Muy resistente."
        ),
        CultivoEntity(
            id = 67, nombre = "Chalota", familia = LILIÁCEAS, icono = "\uD83E\uDDC5",
            marcoCm = 25, diasGerminacion = 12, diasCosecha = 98, // 14 semanas
            temperaturaMinima = -2, temperaturaOptima = 17,
            mesesSiembraDirecta = meses(10, 11),
            mesesSemillero = 0,
            mesesTrasplante = 0,
            profundidadSiembraCm = 2f, riego = BAJO, categoria = CALORICO,
            lineasPorBancal = 5, semanasCosechando = 1,
            admiteSiembraDirecta = true, admitePlantel = false,
            notas = "Plantar bulbillos en otoño. Sabor más suave que la cebolla."
        ),
        CultivoEntity(
            id = 21, nombre = "Ajo", familia = LILIÁCEAS, icono = "\uD83E\uDDC4",
            marcoCm = 15, diasGerminacion = 15, diasCosecha = 210, // 30 semanas
            temperaturaMinima = -5, temperaturaOptima = 14,
            mesesSiembraDirecta = meses(10, 11, 12),
            mesesSemillero = 0,
            mesesTrasplante = 0,
            profundidadSiembraCm = 3f, riego = BAJO, categoria = CALORICO,
            lineasPorBancal = 4, semanasCosechando = 1,
            admiteSiembraDirecta = true, admitePlantel = false,
            notas = "Plantar en otoño. No regar en exceso. Cosecha en junio-julio."
        ),
        CultivoEntity(
            id = 68, nombre = "Ajo tierno", familia = LILIÁCEAS, icono = "\uD83E\uDDC4",
            marcoCm = 15, diasGerminacion = 12, diasCosecha = 56, // 8 semanas
            temperaturaMinima = -5, temperaturaOptima = 14,
            mesesSiembraDirecta = meses(10, 11, 12),
            mesesSemillero = 0,
            mesesTrasplante = 0,
            profundidadSiembraCm = 3f, riego = BAJO, categoria = VEGETAL,
            lineasPorBancal = 5, semanasCosechando = 8,
            admiteSiembraDirecta = true, admitePlantel = false,
            notas = "Se cosecha joven antes de que forme cabeza. Siembra densa."
        ),
        CultivoEntity(
            id = 22, nombre = "Puerro", familia = LILIÁCEAS, icono = "\uD83E\uDDC5",
            marcoCm = 15, diasGerminacion = 14, diasCosecha = 70, // 10 semanas
            temperaturaMinima = -3, temperaturaOptima = 16,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(2, 3),
            mesesTrasplante = meses(5, 6, 7),
            profundidadSiembraCm = 1f, riego = MEDIO, categoria = CALORICO,
            lineasPorBancal = 2, semanasCosechando = 12,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Aporcar para blanquear. Muy resistente al frío."
        ),
        CultivoEntity(
            id = 69, nombre = "Cebollino", familia = LILIÁCEAS, icono = "\uD83C\uDF3F",
            marcoCm = 40, diasGerminacion = 14, diasCosecha = 56, // 8 semanas
            temperaturaMinima = -5, temperaturaOptima = 18,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(2, 3),
            mesesTrasplante = meses(4, 5),
            profundidadSiembraCm = 0.5f, riego = MEDIO, categoria = VEGETAL,
            lineasPorBancal = 3, semanasCosechando = 30,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Perenne. Cortar hojas regularmente. Flores comestibles."
        ),
        CultivoEntity(
            id = 70, nombre = "Espárrago", familia = LILIÁCEAS, icono = "\uD83C\uDF3F",
            marcoCm = 40, diasGerminacion = 21, diasCosecha = 364, // 52 semanas (1er año sin cosecha)
            temperaturaMinima = -10, temperaturaOptima = 20,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(2, 3),
            mesesTrasplante = meses(3, 4),
            profundidadSiembraCm = 2f, riego = MEDIO, categoria = VEGETAL,
            lineasPorBancal = 1, semanasCosechando = 8,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Perenne (15-20 años). No cosechar los 2 primeros años."
        ),

        // =====================================================================
        // COMPUESTAS
        // =====================================================================
        CultivoEntity(
            id = 23, nombre = "Lechuga", familia = COMPUESTAS, icono = "\uD83E\uDD6C",
            marcoCm = 30, diasGerminacion = 7, diasCosecha = 42, // 6 semanas
            temperaturaMinima = 2, temperaturaOptima = 17,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(2, 3, 4, 7, 8),
            mesesTrasplante = meses(3, 4, 5, 8, 9, 10),
            profundidadSiembraCm = 0.5f, riego = ALTO, intervaloSucesionDias = 14,
            lineasPorBancal = 3, semanasCosechando = 4,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Siembra escalonada cada 2 semanas. Espiga con calor."
        ),
        CultivoEntity(
            id = 71, nombre = "Lechuga cogollo", familia = COMPUESTAS, icono = "\uD83E\uDD6C",
            marcoCm = 30, diasGerminacion = 7, diasCosecha = 35, // 5 semanas
            temperaturaMinima = 2, temperaturaOptima = 17,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(2, 3, 4, 7, 8),
            mesesTrasplante = meses(3, 4, 5, 8, 9, 10),
            profundidadSiembraCm = 0.5f, riego = ALTO, intervaloSucesionDias = 14,
            lineasPorBancal = 4, semanasCosechando = 3,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Tipo cogollos de Tudela. Compacta y crujiente."
        ),
        CultivoEntity(
            id = 72, nombre = "Lechuga mix", familia = COMPUESTAS, icono = "\uD83E\uDD6C",
            marcoCm = 20, diasGerminacion = 7, diasCosecha = 42, // 6 semanas
            temperaturaMinima = 2, temperaturaOptima = 17,
            mesesSiembraDirecta = meses(3, 4, 5, 8, 9),
            mesesSemillero = 0,
            mesesTrasplante = 0,
            profundidadSiembraCm = 0.5f, riego = ALTO, intervaloSucesionDias = 14,
            lineasPorBancal = 5, semanasCosechando = 8,
            admiteSiembraDirecta = true, admitePlantel = false,
            notas = "Mezcla de hojas baby. Cortar y vuelve a brotar."
        ),
        CultivoEntity(
            id = 73, nombre = "Escarola", familia = COMPUESTAS, icono = "\uD83E\uDD6C",
            marcoCm = 30, diasGerminacion = 7, diasCosecha = 70, // 10 semanas
            temperaturaMinima = 0, temperaturaOptima = 17,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(6, 7),
            mesesTrasplante = meses(7, 8, 9),
            profundidadSiembraCm = 0.5f, riego = MEDIO,
            lineasPorBancal = 3, semanasCosechando = 6,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Atar para blanquear el corazón. Cultivo de otoño."
        ),
        CultivoEntity(
            id = 74, nombre = "Canónigo", familia = COMPUESTAS, icono = "\uD83E\uDD6C",
            marcoCm = 15, diasGerminacion = 10, diasCosecha = 42, // 6 semanas
            temperaturaMinima = -5, temperaturaOptima = 15,
            mesesSiembraDirecta = meses(8, 9, 10),
            mesesSemillero = meses(8, 9),
            mesesTrasplante = meses(9, 10),
            profundidadSiembraCm = 0.5f, riego = MEDIO,
            lineasPorBancal = 8, semanasCosechando = 8,
            admiteSiembraDirecta = true, admitePlantel = true,
            notas = "Hierba de canónigos. Ideal para invierno en Burgos. Muy resistente."
        ),
        CultivoEntity(
            id = 24, nombre = "Girasol", familia = COMPUESTAS, icono = "\uD83C\uDF3B",
            marcoCm = 50, diasGerminacion = 8, diasCosecha = 90,
            temperaturaMinima = 5, temperaturaOptima = 22,
            mesesSiembraDirecta = meses(4, 5),
            mesesSemillero = 0,
            mesesTrasplante = 0,
            profundidadSiembraCm = 3f, riego = BAJO, categoria = CARBONO,
            lineasPorBancal = 1, semanasCosechando = 4,
            admiteSiembraDirecta = true, admitePlantel = false,
            notas = "Atrae polinizadores. Buena pantalla cortavientos."
        ),
        CultivoEntity(
            id = 25, nombre = "Caléndula", familia = COMPUESTAS, icono = "\uD83C\uDF3C",
            marcoCm = 25, diasGerminacion = 8, diasCosecha = 60,
            temperaturaMinima = 0, temperaturaOptima = 18,
            mesesSiembraDirecta = meses(3, 4, 5, 9),
            mesesSemillero = meses(2, 3),
            mesesTrasplante = meses(4, 5),
            profundidadSiembraCm = 1f, riego = BAJO,
            lineasPorBancal = 3, semanasCosechando = 12,
            admiteSiembraDirecta = true, admitePlantel = true,
            notas = "Repele nematodos. Flores medicinales comestibles."
        ),
        CultivoEntity(
            id = 26, nombre = "Tagete", familia = COMPUESTAS, icono = "\uD83C\uDF3C",
            marcoCm = 20, diasGerminacion = 7, diasCosecha = 50,
            temperaturaMinima = 5, temperaturaOptima = 22,
            mesesSiembraDirecta = meses(5),
            mesesSemillero = meses(3, 4),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 0.5f, riego = BAJO,
            lineasPorBancal = 3, semanasCosechando = 12,
            admiteSiembraDirecta = true, admitePlantel = true,
            notas = "Excelente repelente de nematodos y pulgones. Imprescindible."
        ),

        // =====================================================================
        // QUENOPODIÁCEAS
        // =====================================================================
        CultivoEntity(
            id = 27, nombre = "Espinaca", familia = QUENOPODIÁCEAS, icono = "\uD83E\uDD6C",
            marcoCm = 30, diasGerminacion = 8, diasCosecha = 42, // 6 semanas
            temperaturaMinima = -2, temperaturaOptima = 15,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(2, 3, 8, 9),
            mesesTrasplante = meses(3, 4, 9, 10),
            profundidadSiembraCm = 2f, riego = MEDIO, intervaloSucesionDias = 21,
            lineasPorBancal = 4, semanasCosechando = 8,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Espiga con calor. Mejor en primavera temprana y otoño en Burgos."
        ),
        CultivoEntity(
            id = 75, nombre = "Espinaca baby", familia = QUENOPODIÁCEAS, icono = "\uD83E\uDD6C",
            marcoCm = 30, diasGerminacion = 8, diasCosecha = 42, // 6 semanas
            temperaturaMinima = -2, temperaturaOptima = 15,
            mesesSiembraDirecta = meses(3, 4, 9, 10),
            mesesSemillero = meses(2, 3, 8, 9),
            mesesTrasplante = meses(3, 4, 9, 10),
            profundidadSiembraCm = 2f, riego = MEDIO, intervaloSucesionDias = 14,
            lineasPorBancal = 4, semanasCosechando = 8,
            admiteSiembraDirecta = true, admitePlantel = true,
            notas = "Cosecha de hoja baby. Cortar y vuelve a brotar."
        ),
        CultivoEntity(
            id = 28, nombre = "Acelga", familia = QUENOPODIÁCEAS, icono = "\uD83E\uDD6C",
            marcoCm = 30, diasGerminacion = 10, diasCosecha = 56, // 8 semanas
            temperaturaMinima = -2, temperaturaOptima = 18,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 2f, riego = MEDIO, intervaloSucesionDias = 30,
            lineasPorBancal = 3, semanasCosechando = 12,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Cosechar hojas exteriores. Resiste bien el frío burgalés."
        ),
        CultivoEntity(
            id = 76, nombre = "Acelga baby", familia = QUENOPODIÁCEAS, icono = "\uD83E\uDD6C",
            marcoCm = 15, diasGerminacion = 8, diasCosecha = 35, // 5 semanas
            temperaturaMinima = -2, temperaturaOptima = 18,
            mesesSiembraDirecta = meses(4, 5, 8, 9),
            mesesSemillero = meses(3, 4, 8),
            mesesTrasplante = meses(4, 5, 9),
            profundidadSiembraCm = 2f, riego = MEDIO, intervaloSucesionDias = 21,
            lineasPorBancal = 5, semanasCosechando = 8,
            admiteSiembraDirecta = true, admitePlantel = true,
            notas = "Cosecha de hoja baby. Siembra más densa que acelga normal."
        ),
        CultivoEntity(
            id = 29, nombre = "Remolacha", familia = QUENOPODIÁCEAS, icono = "\uD83E\uDED1",
            marcoCm = 15, diasGerminacion = 10, diasCosecha = 35, // 5 semanas
            temperaturaMinima = 2, temperaturaOptima = 18,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 2f, riego = MEDIO, categoria = CALORICO, intervaloSucesionDias = 21,
            lineasPorBancal = 4, semanasCosechando = 4,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Remolacha de manojo. Cada semilla da varias plantas, aclarar."
        ),
        CultivoEntity(
            id = 77, nombre = "Remolacha granel", familia = QUENOPODIÁCEAS, icono = "\uD83E\uDED1",
            marcoCm = 15, diasGerminacion = 10, diasCosecha = 63, // 9 semanas
            temperaturaMinima = 2, temperaturaOptima = 18,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 2f, riego = MEDIO, categoria = CALORICO,
            lineasPorBancal = 3, semanasCosechando = 8,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Remolacha grande para guardar. Se conserva bien en bodega."
        ),

        // =====================================================================
        // UMBELÍFERAS
        // =====================================================================
        CultivoEntity(
            id = 30, nombre = "Zanahoria", familia = UMBELÍFERAS, icono = "\uD83E\uDD55",
            marcoCm = 15, diasGerminacion = 14, diasCosecha = 77, // 11 semanas
            temperaturaMinima = 2, temperaturaOptima = 17,
            mesesSiembraDirecta = meses(3, 4, 5, 6, 7),
            mesesSemillero = 0,
            mesesTrasplante = 0,
            profundidadSiembraCm = 1f, riego = MEDIO, categoria = CALORICO, intervaloSucesionDias = 21,
            lineasPorBancal = 4, semanasCosechando = 4,
            admiteSiembraDirecta = true, admitePlantel = false,
            notas = "Zanahoria de manojo. No trasplantar. Asociar con cebolla."
        ),
        CultivoEntity(
            id = 78, nombre = "Zanahoria granel", familia = UMBELÍFERAS, icono = "\uD83E\uDD55",
            marcoCm = 15, diasGerminacion = 14, diasCosecha = 112, // 16 semanas
            temperaturaMinima = 2, temperaturaOptima = 17,
            mesesSiembraDirecta = meses(3, 4, 5),
            mesesSemillero = 0,
            mesesTrasplante = 0,
            profundidadSiembraCm = 1f, riego = MEDIO, categoria = CALORICO,
            lineasPorBancal = 3, semanasCosechando = 1,
            admiteSiembraDirecta = true, admitePlantel = false,
            notas = "Zanahoria grande para guardar. Se conserva bien."
        ),
        CultivoEntity(
            id = 31, nombre = "Apio", familia = UMBELÍFERAS, icono = "\uD83E\uDD6C",
            marcoCm = 40, diasGerminacion = 18, diasCosecha = 84, // 12 semanas
            temperaturaMinima = 5, temperaturaOptima = 18,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(2, 3),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 0.3f, riego = ALTO,
            lineasPorBancal = 3, semanasCosechando = 12,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Germinación lenta. Necesita mucha agua."
        ),
        CultivoEntity(
            id = 79, nombre = "Apionabo", familia = UMBELÍFERAS, icono = "\uD83E\uDD6C",
            marcoCm = 40, diasGerminacion = 18, diasCosecha = 70, // 10 semanas
            temperaturaMinima = 5, temperaturaOptima = 18,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(2, 3),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 0.3f, riego = ALTO, categoria = CALORICO,
            lineasPorBancal = 4, semanasCosechando = 4,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Raíz de apio. Germinación lenta. Buen sabor asado."
        ),
        CultivoEntity(
            id = 32, nombre = "Perejil", familia = UMBELÍFERAS, icono = "\uD83C\uDF3F",
            marcoCm = 40, diasGerminacion = 20, diasCosecha = 56, // 8 semanas
            temperaturaMinima = 2, temperaturaOptima = 18,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(2, 3),
            mesesTrasplante = meses(4, 5),
            profundidadSiembraCm = 0.5f, riego = MEDIO,
            lineasPorBancal = 3, semanasCosechando = 20,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Germinación muy lenta. Remojar semillas 24h antes."
        ),
        CultivoEntity(
            id = 33, nombre = "Cilantro", familia = UMBELÍFERAS, icono = "\uD83C\uDF3F",
            marcoCm = 40, diasGerminacion = 14, diasCosecha = 56, // 8 semanas
            temperaturaMinima = 5, temperaturaOptima = 18,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(2, 3, 4, 8),
            mesesTrasplante = meses(3, 4, 5, 9),
            profundidadSiembraCm = 1f, riego = MEDIO, intervaloSucesionDias = 14,
            lineasPorBancal = 3, semanasCosechando = 20,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Espiga rápido con calor. Siembra escalonada cada 2 semanas."
        ),
        CultivoEntity(
            id = 80, nombre = "Hinojo", familia = UMBELÍFERAS, icono = "\uD83C\uDF3F",
            marcoCm = 40, diasGerminacion = 12, diasCosecha = 70, // 10 semanas
            temperaturaMinima = 5, temperaturaOptima = 18,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4, 7),
            mesesTrasplante = meses(5, 6, 8, 9),
            profundidadSiembraCm = 0.5f, riego = MEDIO,
            lineasPorBancal = 3, semanasCosechando = 3,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Bulbo de hinojo (Florence). Aporcar para blanquear."
        ),
        CultivoEntity(
            id = 81, nombre = "Eneldo", familia = UMBELÍFERAS, icono = "\uD83C\uDF3F",
            marcoCm = 30, diasGerminacion = 10, diasCosecha = 56, // 8 semanas
            temperaturaMinima = 5, temperaturaOptima = 18,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 0.5f, riego = MEDIO,
            lineasPorBancal = 4, semanasCosechando = 3,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Aromática anual. Atrae fauna auxiliar. No asociar con hinojo."
        ),

        // =====================================================================
        // GRAMÍNEAS
        // =====================================================================
        CultivoEntity(
            id = 34, nombre = "Maíz", familia = GRAMÍNEAS, icono = "\uD83C\uDF3D",
            marcoCm = 30, diasGerminacion = 7, diasCosecha = 84, // 12 semanas
            temperaturaMinima = 10, temperaturaOptima = 25,
            mesesSiembraDirecta = meses(5, 6),
            mesesSemillero = 0,
            mesesTrasplante = 0,
            profundidadSiembraCm = 4f, riego = MEDIO, categoria = CARBONO,
            lineasPorBancal = 2, semanasCosechando = 4,
            admiteSiembraDirecta = true, admitePlantel = false,
            notas = "Sembrar en bloque para buena polinización. Asociar con judías."
        ),

        // =====================================================================
        // LABIADAS (aromáticas)
        // =====================================================================
        CultivoEntity(
            id = 35, nombre = "Albahaca", familia = LABIADAS, icono = "\uD83C\uDF3F",
            marcoCm = 40, diasGerminacion = 10, diasCosecha = 42, // 6 semanas
            temperaturaMinima = 10, temperaturaOptima = 25,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 0.3f, riego = MEDIO,
            lineasPorBancal = 4, semanasCosechando = 8,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Compañera del tomate. Muy sensible al frío."
        ),
        CultivoEntity(
            id = 36, nombre = "Romero", familia = LABIADAS, icono = "\uD83C\uDF3F",
            marcoCm = 60, diasGerminacion = 21, diasCosecha = 180,
            temperaturaMinima = -5, temperaturaOptima = 20,
            mesesSiembraDirecta = meses(4, 5),
            mesesSemillero = meses(3, 4),
            mesesTrasplante = meses(4, 5, 9, 10),
            profundidadSiembraCm = 0.5f, riego = BAJO,
            lineasPorBancal = 1, semanasCosechando = 30,
            admiteSiembraDirecta = true, admitePlantel = true,
            notas = "Perenne. Mejor por esqueje. Atrae polinizadores."
        ),
        CultivoEntity(
            id = 37, nombre = "Tomillo", familia = LABIADAS, icono = "\uD83C\uDF3F",
            marcoCm = 40, diasGerminacion = 18, diasCosecha = 112, // 16 semanas
            temperaturaMinima = -8, temperaturaOptima = 20,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4),
            mesesTrasplante = meses(4, 5, 9, 10),
            profundidadSiembraCm = 0.3f, riego = BAJO,
            lineasPorBancal = 3, semanasCosechando = 30,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Perenne. Muy resistente. Repele plagas. Mejor por esqueje."
        ),
        CultivoEntity(
            id = 38, nombre = "Salvia", familia = LABIADAS, icono = "\uD83C\uDF3F",
            marcoCm = 35, diasGerminacion = 14, diasCosecha = 150,
            temperaturaMinima = -5, temperaturaOptima = 20,
            mesesSiembraDirecta = meses(4, 5),
            mesesSemillero = meses(3, 4),
            mesesTrasplante = meses(4, 5, 9, 10),
            profundidadSiembraCm = 0.5f, riego = BAJO,
            lineasPorBancal = 2, semanasCosechando = 20,
            admiteSiembraDirecta = true, admitePlantel = true,
            notas = "Perenne. Repele la mosca de la zanahoria."
        ),
        CultivoEntity(
            id = 82, nombre = "Orégano", familia = LABIADAS, icono = "\uD83C\uDF3F",
            marcoCm = 40, diasGerminacion = 14, diasCosecha = 112, // 16 semanas
            temperaturaMinima = -8, temperaturaOptima = 20,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(3, 4),
            mesesTrasplante = meses(5, 6),
            profundidadSiembraCm = 0.3f, riego = BAJO,
            lineasPorBancal = 3, semanasCosechando = 30,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Perenne. Secar al aire para conservar. Atrae polinizadores."
        ),

        // =====================================================================
        // BORAGINÁCEAS
        // =====================================================================
        CultivoEntity(
            id = 39, nombre = "Borraja", familia = BORAGINÁCEAS, icono = "\uD83C\uDF3C",
            marcoCm = 25, diasGerminacion = 8, diasCosecha = 42, // 6 semanas
            temperaturaMinima = 0, temperaturaOptima = 17,
            mesesSiembraDirecta = meses(3, 4, 5, 9),
            mesesSemillero = meses(2, 3, 4),
            mesesTrasplante = meses(4, 5, 6),
            profundidadSiembraCm = 1f, riego = MEDIO,
            lineasPorBancal = 2, semanasCosechando = 8,
            admiteSiembraDirecta = true, admitePlantel = true,
            notas = "Atrae polinizadores. Compañera del tomate y la fresa."
        ),

        // =====================================================================
        // ROSÁCEAS
        // =====================================================================
        CultivoEntity(
            id = 40, nombre = "Fresa", familia = ROSÁCEAS, icono = "\uD83C\uDF53",
            marcoCm = 25, diasGerminacion = 21, diasCosecha = 90,
            temperaturaMinima = -5, temperaturaOptima = 18,
            mesesSiembraDirecta = 0,
            mesesSemillero = 0,
            mesesTrasplante = meses(3, 4, 9, 10),
            profundidadSiembraCm = 0f, riego = MEDIO,
            lineasPorBancal = 3, semanasCosechando = 12,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Plantar por estolones. Acolchar con paja. Asociar con ajo."
        ),

        // =====================================================================
        // POLIGONÁCEAS (en QUENOPODIÁCEAS por agrupación)
        // =====================================================================
        CultivoEntity(
            id = 83, nombre = "Ruibarbo", familia = QUENOPODIÁCEAS, icono = "\uD83C\uDF3F",
            marcoCm = 50, diasGerminacion = 14, diasCosecha = 91, // 13 semanas
            temperaturaMinima = -10, temperaturaOptima = 17,
            mesesSiembraDirecta = 0,
            mesesSemillero = meses(2, 3),
            mesesTrasplante = meses(4, 5),
            profundidadSiembraCm = 1f, riego = MEDIO,
            lineasPorBancal = 1, semanasCosechando = 8,
            admiteSiembraDirecta = false, admitePlantel = true,
            notas = "Perenne. Solo comer tallos, hojas tóxicas. No cosechar el 1er año."
        )
    )

    // === ASOCIACIONES DE CULTIVOS ===
    // Datos basados en agricultura biointensiva y sinérgica
    val asociaciones = listOf(
        // Tomate (1) asociaciones — aplica también a variantes (41-43)
        AsociacionEntity(1, 35, BENEFICIOSA, "La albahaca repele el pulgón y la mosca blanca del tomate"),
        AsociacionEntity(1, 25, BENEFICIOSA, "La caléndula repele nematodos alrededor del tomate"),
        AsociacionEntity(1, 30, BENEFICIOSA, "La zanahoria airea el suelo para las raíces del tomate"),
        AsociacionEntity(1, 32, BENEFICIOSA, "El perejil repele insectos dañinos del tomate"),
        AsociacionEntity(1, 39, BENEFICIOSA, "La borraja atrae polinizadores y repele gusano del tomate"),
        AsociacionEntity(1, 13, PERJUDICIAL, "Compiten por nutrientes y la col atrae plagas al tomate"),
        AsociacionEntity(1, 4, PERJUDICIAL, "Misma familia, comparten enfermedades (mildiu, tizón)"),
        AsociacionEntity(1, 7, PERJUDICIAL, "El pepino y el tomate compiten y comparten enfermedades"),

        // Zanahoria (30) + Aliáceas
        AsociacionEntity(30, 20, BENEFICIOSA, "Se repelen mutuamente la mosca de la zanahoria y la de la cebolla"),
        AsociacionEntity(30, 22, BENEFICIOSA, "El puerro repele la mosca de la zanahoria"),
        AsociacionEntity(30, 21, BENEFICIOSA, "El ajo repele la mosca de la zanahoria"),
        AsociacionEntity(30, 23, BENEFICIOSA, "La lechuga aprovecha la sombra parcial de la zanahoria", intercalable = true),

        // Maíz (34) + Judía (10) + Calabaza (6) — Las tres hermanas
        AsociacionEntity(34, 10, BENEFICIOSA, "La judía fija nitrógeno y trepa por el maíz (Tres Hermanas)", intercalable = true),
        AsociacionEntity(34, 6, BENEFICIOSA, "La calabaza cubre el suelo y retiene humedad (Tres Hermanas)"),
        AsociacionEntity(10, 6, BENEFICIOSA, "La calabaza protege el suelo, la judía aporta nitrógeno"),

        // Lechuga (23) asociaciones
        AsociacionEntity(23, 18, BENEFICIOSA, "El rabanito marca la línea y se cosecha antes", intercalable = true),
        AsociacionEntity(23, 40, BENEFICIOSA, "La fresa y la lechuga se complementan en espacio"),

        // Leguminosas mejoran el suelo para muchos cultivos
        AsociacionEntity(12, 4, BENEFICIOSA, "Las habas fijan nitrógeno que aprovecha la patata"),
        AsociacionEntity(11, 30, BENEFICIOSA, "El guisante fija nitrógeno para la zanahoria"),

        // Aromáticas protectoras
        AsociacionEntity(36, 13, BENEFICIOSA, "El romero repele la mariposa de la col"),
        AsociacionEntity(37, 13, BENEFICIOSA, "El tomillo repele la oruga de la col"),
        AsociacionEntity(38, 30, BENEFICIOSA, "La salvia repele la mosca de la zanahoria"),
        AsociacionEntity(26, 1, BENEFICIOSA, "El tagete repele nematodos y mosca blanca del tomate"),

        // Fresa (40) asociaciones
        AsociacionEntity(40, 21, BENEFICIOSA, "El ajo protege la fresa de hongos"),
        AsociacionEntity(40, 39, BENEFICIOSA, "La borraja atrae polinizadores para la fresa"),
        AsociacionEntity(40, 27, BENEFICIOSA, "La espinaca aprovecha el espacio entre fresas"),

        // Incompatibilidades
        AsociacionEntity(20, 10, PERJUDICIAL, "La cebolla inhibe el crecimiento de las leguminosas"),
        AsociacionEntity(20, 11, PERJUDICIAL, "La cebolla inhibe el crecimiento de los guisantes"),
        AsociacionEntity(20, 12, PERJUDICIAL, "La cebolla inhibe el crecimiento de las habas"),
        AsociacionEntity(21, 10, PERJUDICIAL, "El ajo inhibe el crecimiento de las leguminosas"),
        AsociacionEntity(21, 11, PERJUDICIAL, "El ajo inhibe el crecimiento de los guisantes"),
        AsociacionEntity(2, 12, PERJUDICIAL, "El pimiento y las habas compiten por nutrientes"),
        AsociacionEntity(4, 6, PERJUDICIAL, "La patata y la calabaza compiten intensamente"),

        // Nuevas asociaciones con cultivos añadidos
        AsociacionEntity(49, 34, BENEFICIOSA, "La judía de enrame trepa por el maíz y fija nitrógeno"),
        AsociacionEntity(80, 30, PERJUDICIAL, "El hinojo inhibe el crecimiento de la zanahoria"),
        AsociacionEntity(80, 1, PERJUDICIAL, "El hinojo perjudica al tomate"),
        AsociacionEntity(81, 80, PERJUDICIAL, "El eneldo y el hinojo se hibridan, no plantar juntos"),
        AsociacionEntity(57, 40, BENEFICIOSA, "La rúcula repele insectos de la fresa"),
        AsociacionEntity(69, 30, BENEFICIOSA, "El cebollino repele la mosca de la zanahoria"),
        AsociacionEntity(70, 1, BENEFICIOSA, "El espárrago y el tomate se protegen mutuamente"),
        AsociacionEntity(82, 13, BENEFICIOSA, "El orégano repele la mariposa de la col"),

        // Asociaciones intercalables (cultivo rápido entre cultivo lento)
        AsociacionEntity(1, 23, BENEFICIOSA, "Intercalado: lechuga ciclo rápido entre tomates", intercalable = true),
        AsociacionEntity(1, 18, BENEFICIOSA, "Intercalado: rábano ciclo rápido entre tomates", intercalable = true),
        AsociacionEntity(1, 27, BENEFICIOSA, "Intercalado: espinaca aprovecha sombra del tomate", intercalable = true),
        AsociacionEntity(2, 23, BENEFICIOSA, "Intercalado: lechuga ciclo rápido entre pimientos", intercalable = true),
        AsociacionEntity(2, 18, BENEFICIOSA, "Intercalado: rábano ciclo rápido entre pimientos", intercalable = true),
        AsociacionEntity(3, 23, BENEFICIOSA, "Intercalado: lechuga ciclo rápido entre berenjenas", intercalable = true),
        AsociacionEntity(3, 18, BENEFICIOSA, "Intercalado: rábano ciclo rápido entre berenjenas", intercalable = true),
        AsociacionEntity(5, 23, BENEFICIOSA, "Intercalado: lechuga antes de que el calabacín cubra", intercalable = true),
        AsociacionEntity(11, 18, BENEFICIOSA, "Intercalado: rábano ciclo rápido entre guisantes", intercalable = true),
        AsociacionEntity(28, 18, BENEFICIOSA, "Intercalado: rábano ciclo rápido entre acelgas", intercalable = true),
        AsociacionEntity(23, 29, BENEFICIOSA, "Intercalado: lechuga rápida entre remolachas", intercalable = true),
        // PDF: Tomate + Cebolleta/Remolacha
        AsociacionEntity(1, 65, BENEFICIOSA, "Intercalado: cebolleta rápida entre tomates", intercalable = true),
        AsociacionEntity(1, 29, BENEFICIOSA, "Intercalado: remolacha aprovecha espacio entre tomates", intercalable = true),
        // PDF: Lechuga + Cebolleta
        AsociacionEntity(23, 65, BENEFICIOSA, "Intercalado: cebolleta y lechuga ciclos complementarios", intercalable = true),
        // PDF: Leguminosas de enrame + Rabanitos/Cebolleta/Lechuga
        AsociacionEntity(49, 18, BENEFICIOSA, "Intercalado: rábano rápido entre judías de enrame", intercalable = true),
        AsociacionEntity(49, 65, BENEFICIOSA, "Intercalado: cebolleta entre judías de enrame", intercalable = true),
        AsociacionEntity(49, 23, BENEFICIOSA, "Intercalado: lechuga rápida entre judías de enrame", intercalable = true),
        AsociacionEntity(12, 18, BENEFICIOSA, "Intercalado: rábano rápido entre habas", intercalable = true),
        AsociacionEntity(12, 65, BENEFICIOSA, "Intercalado: cebolleta entre habas", intercalable = true),
        AsociacionEntity(12, 23, BENEFICIOSA, "Intercalado: lechuga rápida entre habas", intercalable = true),
        AsociacionEntity(11, 23, BENEFICIOSA, "Intercalado: lechuga rápida entre guisantes", intercalable = true),
        AsociacionEntity(11, 65, BENEFICIOSA, "Intercalado: cebolleta entre guisantes", intercalable = true)
    )
}
