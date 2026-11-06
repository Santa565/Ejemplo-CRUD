package com.ejemplo.crudroom.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa la tabla SQLite 'tasks_table'.
 * Room mapea esta clase de datos a una tabla relacional en SQLite.
 */
@Entity(tableName = "tasks_table")
data class TaskEntity(
    // Clave primaria autoincremental en SQLite
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Columna para el título de la tarea
    @ColumnInfo(name = "title")
    val title: String,

    // Columna para la descripción
    @ColumnInfo(name = "description")
    val description: String,

    // Columna booleana (almacenada como INTEGER 0 o 1 en SQLite)
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,

    // Marca de tiempo de creación (milisegundos)
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
