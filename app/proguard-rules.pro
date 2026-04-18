# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *

# Enums usados por Room (se almacenan como TEXT, R8 no debe renombrarlos)
-keepclassmembers enum com.bancal.app.domain.model.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Room TypeConverters
-keep class com.bancal.app.data.db.Converters { *; }

# WorkManager
-keep class androidx.work.** { *; }
-keep class com.bancal.app.worker.** { *; }

# Compose: mantener lambdas y metadata
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# Kotlin coroutines
-dontwarn kotlinx.coroutines.**
