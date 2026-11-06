package com.ejemplo.crudroom.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de Datos Room principal que actúa como punto de acceso a SQLite.
 */
@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Método abstracto para acceder al DAO
    abstract fun taskDao(): TaskDao

    companion object {
        // @Volatile garantiza que los cambios a INSTANCE sean visibles inmediatamente para todos los hilos
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene la instancia única (Singleton) de la base de datos.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tasks_database.db" // Nombre del archivo físico SQLite
                )
                    // En caso de cambios en el esquema sin migración definida, recrea las tablas (útil en desarrollo)
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
