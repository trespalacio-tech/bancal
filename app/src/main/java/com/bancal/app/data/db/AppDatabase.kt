package com.bancal.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bancal.app.data.db.dao.*
import com.bancal.app.data.db.entity.*
import com.bancal.app.data.seed.CultivosSeed
import com.bancal.app.data.seed.OnboardingSeed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BancalEntity::class,
        CultivoEntity::class,
        PlantacionEntity::class,
        TratamientoEntity::class,
        AsociacionEntity::class,
        AlertaEntity::class,
        DiarioEntity::class
    ],
    version = 15,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bancalDao(): BancalDao
    abstract fun cultivoDao(): CultivoDao
    abstract fun plantacionDao(): PlantacionDao
    abstract fun tratamientoDao(): TratamientoDao
    abstract fun asociacionDao(): AsociacionDao
    abstract fun alertaDao(): AlertaDao
    abstract fun diarioDao(): DiarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE cultivos ADD COLUMN categoria TEXT NOT NULL DEFAULT 'VEGETAL'")
                db.execSQL("UPDATE cultivos SET categoria = 'CARBONO' WHERE id IN (34, 24)")
                db.execSQL("UPDATE cultivos SET categoria = 'CALORICO' WHERE id IN (4, 6, 10, 11, 12, 19, 20, 21, 22, 29, 30)")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE cultivos ADD COLUMN intervaloSucesionDias INTEGER NOT NULL DEFAULT 0")
                db.execSQL("UPDATE cultivos SET intervaloSucesionDias = 10 WHERE id = 18")  // Rábano
                db.execSQL("UPDATE cultivos SET intervaloSucesionDias = 14 WHERE id IN (23, 33)") // Lechuga, Cilantro
                db.execSQL("UPDATE cultivos SET intervaloSucesionDias = 21 WHERE id IN (10, 11, 19, 27, 29, 30)") // Judía, Guisante, Nabo, Espinaca, Remolacha, Zanahoria
                db.execSQL("UPDATE cultivos SET intervaloSucesionDias = 30 WHERE id = 28") // Acelga
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS diario (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        fecha INTEGER NOT NULL,
                        tempMin INTEGER,
                        tempMax INTEGER,
                        lluviaMm REAL,
                        helada INTEGER NOT NULL DEFAULT 0,
                        tareas TEXT NOT NULL DEFAULT '',
                        observaciones TEXT NOT NULL DEFAULT '',
                        cosechaKg REAL,
                        cosechaNotas TEXT NOT NULL DEFAULT ''
                    )
                """.trimIndent())
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Crear tabla bancales
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS bancales (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nombre TEXT NOT NULL DEFAULT 'Mi Bancal',
                        largoCm INTEGER NOT NULL DEFAULT 1000,
                        anchoCm INTEGER NOT NULL DEFAULT 70
                    )
                """.trimIndent())

                // 2. Insertar el bancal por defecto con las dimensiones originales
                db.execSQL("INSERT INTO bancales (id, nombre, largoCm, anchoCm) VALUES (1, 'Mi Bancal', 1000, 70)")

                // 3. Recrear plantaciones con FK a bancales
                //    (SQLite no soporta ADD FOREIGN KEY, así que recreamos la tabla)
                db.execSQL("""
                    CREATE TABLE plantaciones_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        cultivoId INTEGER NOT NULL,
                        bancalId INTEGER NOT NULL DEFAULT 1,
                        fechaSiembra INTEGER NOT NULL,
                        fechaTrasplanteEstimada INTEGER,
                        fechaCosechaEstimada INTEGER NOT NULL,
                        posicionXCm INTEGER NOT NULL,
                        anchoCm INTEGER NOT NULL,
                        tipoSiembra TEXT NOT NULL,
                        estado TEXT NOT NULL,
                        notas TEXT NOT NULL DEFAULT '',
                        FOREIGN KEY(cultivoId) REFERENCES cultivos(id) ON DELETE CASCADE,
                        FOREIGN KEY(bancalId) REFERENCES bancales(id) ON DELETE CASCADE
                    )
                """.trimIndent())

                db.execSQL("""
                    INSERT INTO plantaciones_new (id, cultivoId, bancalId, fechaSiembra, fechaTrasplanteEstimada, fechaCosechaEstimada, posicionXCm, anchoCm, tipoSiembra, estado, notas)
                    SELECT id, cultivoId, 1, fechaSiembra, fechaTrasplanteEstimada, fechaCosechaEstimada, posicionXCm, anchoCm, tipoSiembra, estado, notas FROM plantaciones
                """.trimIndent())

                db.execSQL("DROP TABLE plantaciones")
                db.execSQL("ALTER TABLE plantaciones_new RENAME TO plantaciones")

                // 4. Recrear índices
                db.execSQL("CREATE INDEX index_plantaciones_cultivoId ON plantaciones(cultivoId)")
                db.execSQL("CREATE INDEX index_plantaciones_bancalId ON plantaciones(bancalId)")
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Deduplicar entradas de diario antes de imponer unique index:
                // mantener la fila con id más alto (la última edición) por fecha.
                db.execSQL("""
                    DELETE FROM diario WHERE id NOT IN (
                        SELECT MAX(id) FROM diario GROUP BY fecha
                    )
                """.trimIndent())
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_diario_fecha ON diario(fecha)")
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE cultivos ADD COLUMN lineasPorBancal INTEGER NOT NULL DEFAULT 2")
                db.execSQL("ALTER TABLE cultivos ADD COLUMN semanasCosechando INTEGER NOT NULL DEFAULT 4")
                // Actualizar ancho de bancal de 70 a 75cm (recomendación huerta regenerativa)
                db.execSQL("UPDATE bancales SET anchoCm = 75 WHERE anchoCm = 70")
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE bancales ADD COLUMN tarpingDesde INTEGER DEFAULT NULL")
            }
        }

        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE cultivos ADD COLUMN exigencia TEXT NOT NULL DEFAULT 'POCO_EXIGENTE'")
                // Solanáceas
                db.execSQL("UPDATE cultivos SET exigencia = 'MUY_EXIGENTE' WHERE id IN (1, 2, 3, 4, 41, 42, 43, 44, 45, 46, 47)")
                // Cucurbitáceas
                db.execSQL("UPDATE cultivos SET exigencia = 'MUY_EXIGENTE' WHERE id IN (5, 6, 7, 8, 9, 48)")
                // Crucíferas
                db.execSQL("UPDATE cultivos SET exigencia = 'MUY_EXIGENTE' WHERE id IN (13, 14, 15, 16, 17, 18, 19, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64)")
                // Leguminosas
                db.execSQL("UPDATE cultivos SET exigencia = 'NADA_EXIGENTE' WHERE id IN (10, 11, 12, 49)")
                // Abono verde en bancales
                db.execSQL("ALTER TABLE bancales ADD COLUMN abonoVerdeDesde INTEGER DEFAULT NULL")
                db.execSQL("ALTER TABLE bancales ADD COLUMN abonoVerdeTipo TEXT DEFAULT NULL")
            }
        }

        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Campo intercaladaCon en plantaciones
                db.execSQL("ALTER TABLE plantaciones ADD COLUMN intercaladaCon INTEGER DEFAULT NULL")
                // 2. Campo intercalable en asociaciones
                db.execSQL("ALTER TABLE asociaciones ADD COLUMN intercalable INTEGER NOT NULL DEFAULT 0")
                // 3. Marcar asociaciones intercalables (combinaciones del PDF)
                // Tomate(1) + Lechuga(23)
                db.execSQL("UPDATE asociaciones SET intercalable = 1 WHERE (cultivoId1 = 1 AND cultivoId2 = 23) OR (cultivoId1 = 23 AND cultivoId2 = 1)")
                // Zanahoria(30) + Lechuga(23)
                db.execSQL("UPDATE asociaciones SET intercalable = 1 WHERE (cultivoId1 = 30 AND cultivoId2 = 23) OR (cultivoId1 = 23 AND cultivoId2 = 30)")
                // Maíz(34) + Judía(10)
                db.execSQL("UPDATE asociaciones SET intercalable = 1 WHERE (cultivoId1 = 34 AND cultivoId2 = 10) OR (cultivoId1 = 10 AND cultivoId2 = 34)")
                // 4. Insertar nuevas asociaciones intercalables que no existían
                // Tomate(1) + Rábano(18)
                db.execSQL("INSERT OR IGNORE INTO asociaciones (cultivoId1, cultivoId2, tipo, motivo, intercalable) VALUES (1, 18, 'BENEFICIOSA', 'Intercalado: rábano ciclo rápido entre tomates', 1)")
                // Tomate(1) + Espinaca(27)
                db.execSQL("INSERT OR IGNORE INTO asociaciones (cultivoId1, cultivoId2, tipo, motivo, intercalable) VALUES (1, 27, 'BENEFICIOSA', 'Intercalado: espinaca aprovecha sombra del tomate', 1)")
                // Pimiento(2) + Lechuga(23)
                db.execSQL("INSERT OR IGNORE INTO asociaciones (cultivoId1, cultivoId2, tipo, motivo, intercalable) VALUES (2, 23, 'BENEFICIOSA', 'Intercalado: lechuga ciclo rápido entre pimientos', 1)")
                // Pimiento(2) + Rábano(18)
                db.execSQL("INSERT OR IGNORE INTO asociaciones (cultivoId1, cultivoId2, tipo, motivo, intercalable) VALUES (2, 18, 'BENEFICIOSA', 'Intercalado: rábano ciclo rápido entre pimientos', 1)")
                // Berenjena(3) + Lechuga(23)
                db.execSQL("INSERT OR IGNORE INTO asociaciones (cultivoId1, cultivoId2, tipo, motivo, intercalable) VALUES (3, 23, 'BENEFICIOSA', 'Intercalado: lechuga ciclo rápido entre berenjenas', 1)")
                // Berenjena(3) + Rábano(18)
                db.execSQL("INSERT OR IGNORE INTO asociaciones (cultivoId1, cultivoId2, tipo, motivo, intercalable) VALUES (3, 18, 'BENEFICIOSA', 'Intercalado: rábano ciclo rápido entre berenjenas', 1)")
                // Calabacín(5) + Lechuga(23)
                db.execSQL("INSERT OR IGNORE INTO asociaciones (cultivoId1, cultivoId2, tipo, motivo, intercalable) VALUES (5, 23, 'BENEFICIOSA', 'Intercalado: lechuga aprovecha espacio antes de que el calabacín cubra', 1)")
                // Guisante(11) + Rábano(18)
                db.execSQL("INSERT OR IGNORE INTO asociaciones (cultivoId1, cultivoId2, tipo, motivo, intercalable) VALUES (11, 18, 'BENEFICIOSA', 'Intercalado: rábano ciclo rápido entre guisantes', 1)")
                // Acelga(28) + Rábano(18)
                db.execSQL("INSERT OR IGNORE INTO asociaciones (cultivoId1, cultivoId2, tipo, motivo, intercalable) VALUES (28, 18, 'BENEFICIOSA', 'Intercalado: rábano ciclo rápido entre acelgas', 1)")
                // Lechuga(23) + Remolacha(29) — del PDF
                db.execSQL("INSERT OR IGNORE INTO asociaciones (cultivoId1, cultivoId2, tipo, motivo, intercalable) VALUES (23, 29, 'BENEFICIOSA', 'Intercalado: lechuga rápida entre remolachas', 1)")
            }
        }

        /**
         * Migración correctiva: la 9→10 falló en insertar/marcar varias asociaciones
         * intercalables. Esta migración garantiza que TODAS las combinaciones existen
         * y están marcadas como intercalable=1.
         */
        private val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Insertar las que no existían (INSERT OR IGNORE no toca las existentes)
                val nuevas = listOf(
                    Triple(1L, 23L, "Intercalado: lechuga ciclo rápido entre tomates"),
                    Triple(1L, 18L, "Intercalado: rábano ciclo rápido entre tomates"),
                    Triple(1L, 27L, "Intercalado: espinaca aprovecha sombra del tomate"),
                    Triple(2L, 23L, "Intercalado: lechuga ciclo rápido entre pimientos"),
                    Triple(2L, 18L, "Intercalado: rábano ciclo rápido entre pimientos"),
                    Triple(3L, 23L, "Intercalado: lechuga ciclo rápido entre berenjenas"),
                    Triple(3L, 18L, "Intercalado: rábano ciclo rápido entre berenjenas"),
                    Triple(5L, 23L, "Intercalado: lechuga antes de que el calabacín cubra"),
                    Triple(11L, 18L, "Intercalado: rábano ciclo rápido entre guisantes"),
                    Triple(28L, 18L, "Intercalado: rábano ciclo rápido entre acelgas"),
                    Triple(23L, 29L, "Intercalado: lechuga rápida entre remolachas")
                )
                for ((id1, id2, motivo) in nuevas) {
                    db.execSQL(
                        "INSERT OR IGNORE INTO asociaciones (cultivoId1, cultivoId2, tipo, motivo, intercalable) VALUES ($id1, $id2, 'BENEFICIOSA', '$motivo', 1)"
                    )
                }
                // Marcar intercalable=1 en TODAS las combinaciones (incluye existentes que no se marcaron)
                val pares = listOf(
                    1L to 23L, 1L to 18L, 1L to 27L,
                    2L to 23L, 2L to 18L,
                    3L to 23L, 3L to 18L,
                    5L to 23L,
                    11L to 18L,
                    28L to 18L,
                    23L to 29L,
                    23L to 18L,  // lechuga+rábano (existía pero no se marcó)
                    30L to 23L,  // zanahoria+lechuga
                    34L to 10L   // maíz+judía
                )
                for ((id1, id2) in pares) {
                    db.execSQL(
                        "UPDATE asociaciones SET intercalable = 1 WHERE (cultivoId1 = $id1 AND cultivoId2 = $id2) OR (cultivoId1 = $id2 AND cultivoId2 = $id1)"
                    )
                }
            }
        }

        /**
         * Migración 11→12: Añadir intercalables del PDF (cebolleta, remolacha con tomate,
         * leguminosas de enrame con rábano/cebolleta/lechuga).
         */
        private val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val nuevas = listOf(
                    Triple(1L, 65L, "Intercalado: cebolleta rápida entre tomates"),
                    Triple(1L, 29L, "Intercalado: remolacha aprovecha espacio entre tomates"),
                    Triple(23L, 65L, "Intercalado: cebolleta y lechuga ciclos complementarios"),
                    Triple(49L, 18L, "Intercalado: rábano rápido entre judías de enrame"),
                    Triple(49L, 65L, "Intercalado: cebolleta entre judías de enrame"),
                    Triple(49L, 23L, "Intercalado: lechuga rápida entre judías de enrame"),
                    Triple(12L, 18L, "Intercalado: rábano rápido entre habas"),
                    Triple(12L, 65L, "Intercalado: cebolleta entre habas"),
                    Triple(12L, 23L, "Intercalado: lechuga rápida entre habas"),
                    Triple(11L, 23L, "Intercalado: lechuga rápida entre guisantes"),
                    Triple(11L, 65L, "Intercalado: cebolleta entre guisantes")
                )
                for ((id1, id2, motivo) in nuevas) {
                    db.execSQL(
                        "INSERT OR IGNORE INTO asociaciones (cultivoId1, cultivoId2, tipo, motivo, intercalable) VALUES ($id1, $id2, 'BENEFICIOSA', '$motivo', 1)"
                    )
                }
                // Marcar intercalable=1 en caso de que ya existieran
                val pares = listOf(
                    1L to 65L, 1L to 29L, 23L to 65L,
                    49L to 18L, 49L to 65L, 49L to 23L,
                    12L to 18L, 12L to 65L, 12L to 23L,
                    11L to 23L, 11L to 65L
                )
                for ((id1, id2) in pares) {
                    db.execSQL(
                        "UPDATE asociaciones SET intercalable = 1 WHERE (cultivoId1 = $id1 AND cultivoId2 = $id2) OR (cultivoId1 = $id2 AND cultivoId2 = $id1)"
                    )
                }
            }
        }

        /**
         * Migración 12→13: Añadir campos admiteSiembraDirecta y admitePlantel a cultivos.
         */
        private val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE cultivos ADD COLUMN admiteSiembraDirecta INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE cultivos ADD COLUMN admitePlantel INTEGER NOT NULL DEFAULT 1")
                // Cultivos que admiten siembra directa (y NO plantel)
                // Patata(4), Boniato(47), Ajo(21), Ajo tierno(68), Chalota(67),
                // Rabanito(18), Nabo(19), Zanahoria(30), Zanahoria granel(78),
                // Maíz(34), Girasol(24), Lechuga mix(72)
                val soloDirecta = listOf(4, 47, 21, 68, 67, 18, 19, 30, 78, 34, 24, 72)
                for (id in soloDirecta) {
                    db.execSQL("UPDATE cultivos SET admiteSiembraDirecta = 1, admitePlantel = 0 WHERE id = $id")
                }
                // Cultivos que admiten AMBOS (siembra directa + plantel)
                // Calabaza(6), Calabaza grande(48), Judía baja(10), Judía enrame(49),
                // Guisante(11), Haba(12), Rábano invierno(56), Espinaca baby(75),
                // Acelga baby(76), Canónigo(74), Caléndula(25), Tagete(26),
                // Romero(36), Salvia(38), Borraja(39)
                val ambos = listOf(6, 48, 10, 49, 11, 12, 56, 75, 76, 74, 25, 26, 36, 38, 39)
                for (id in ambos) {
                    db.execSQL("UPDATE cultivos SET admiteSiembraDirecta = 1 WHERE id = $id")
                }
            }
        }

        private val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE cultivos ADD COLUMN esPersonalizado INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_14_15 = object : Migration(14, 15) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE diario ADD COLUMN fotoPath TEXT DEFAULT NULL")
            }
        }

        fun closeInstance() {
            synchronized(this) {
                INSTANCE?.close()
                INSTANCE = null
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "bancal_database"
                    )
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12, MIGRATION_12_13, MIGRATION_13_14, MIGRATION_14_15)
                        .addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                CoroutineScope(Dispatchers.IO).launch {
                                    val database = getInstance(context)
                                    // Seed bancal por defecto
                                    val bancalDao = database.bancalDao()
                                    if (bancalDao.count() == 0) {
                                        bancalDao.insert(BancalEntity(nombre = "Mi Bancal", largoCm = 1000, anchoCm = 75))
                                    }
                                    // Seed cultivos y asociaciones
                                    val cultivoDao = database.cultivoDao()
                                    val asociacionDao = database.asociacionDao()
                                    if (cultivoDao.count() == 0) {
                                        cultivoDao.insertAll(CultivosSeed.cultivos)
                                        asociacionDao.insertAll(CultivosSeed.asociaciones)
                                    }
                                    // Plantaciones y alertas de ejemplo para onboarding
                                    val plantacionDao = database.plantacionDao()
                                    val alertaDao = database.alertaDao()
                                    for (p in OnboardingSeed.plantaciones()) {
                                        plantacionDao.insert(p)
                                    }
                                    for (a in OnboardingSeed.alertas()) {
                                        alertaDao.insert(a)
                                    }
                                }
                            }
                        })
                        .build()
                    INSTANCE = instance
                    instance
                }
            }
        }
    }
}
