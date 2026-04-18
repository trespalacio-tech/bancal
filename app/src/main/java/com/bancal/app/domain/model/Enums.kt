package com.bancal.app.domain.model

enum class TipoSiembra {
    DIRECTA, SEMILLERO, TRASPLANTE
}

enum class EstadoPlantacion {
    SEMILLERO, TRASPLANTADO, CRECIENDO, COSECHANDO, RETIRADO
}

enum class NivelRiego {
    BAJO, MEDIO, ALTO
}

enum class TipoTratamiento {
    RIEGO, ABONO, FUNGICIDA, INSECTICIDA, PODA, ACOLCHADO, TE_COMPOST, DESHERBADO, OTRO
}

/**
 * Exigencia nutricional del cultivo según sección 5.7 del PDF de huerta regenerativa.
 * MUY_EXIGENTE: solanáceas, cucurbitáceas, crucíferas (ciclo largo).
 * POCO_EXIGENTE: la mayoría de hortalizas.
 * NADA_EXIGENTE: leguminosas (fijan nitrógeno).
 */
enum class ExigenciaNutricional {
    MUY_EXIGENTE, POCO_EXIGENTE, NADA_EXIGENTE
}

enum class TipoAsociacion {
    BENEFICIOSA, PERJUDICIAL
}

enum class TipoAlerta {
    COSECHA, TRASPLANTE, HELADA, RIEGO, TRATAMIENTO, ROTACION, INFO
}

/**
 * Categoría biointensiva según la regla 60/30/10 de John Jeavons.
 * CARBONO (60%): cultivos que producen biomasa para compost (cereales, gramíneas, girasol).
 * CALORICO (30%): cultivos densos en calorías por m² (raíces, tubérculos, leguminosas, calabazas).
 * VEGETAL (10%): cultivos de vitaminas y minerales (hortalizas, aromáticas, flores auxiliares).
 */
enum class CategoriaBiointensiva {
    CARBONO, CALORICO, VEGETAL
}

enum class FamiliaCultivo {
    SOLANACEAS,    // tomate, pimiento, berenjena, patata
    CUCURBITACEAS, // calabacín, calabaza, pepino, melón, sandía
    LEGUMINOSAS,   // judía, guisante, haba
    CRUCIFERAS,    // col, brócoli, coliflor, repollo, kale, nabo, rábano
    LILIÁCEAS,     // cebolla, ajo, puerro
    COMPUESTAS,    // lechuga, girasol, caléndula, tagete
    QUENOPODIÁCEAS,// espinaca, acelga, remolacha
    UMBELÍFERAS,   // zanahoria, apio, perejil, cilantro
    GRAMÍNEAS,     // maíz
    LABIADAS,      // albahaca, romero, tomillo, salvia
    BORAGINÁCEAS,  // borraja
    ROSÁCEAS       // fresa
}
