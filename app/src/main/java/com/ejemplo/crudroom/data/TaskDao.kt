package com.ejemplo.crudroom.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object): Interfaz que define las operaciones de Base de Datos.
 * Room implementa automáticamente estos métodos generando el código SQL necesario.
 */
@Dao
interface TaskDao {

    // ──────────────────────────── READ (Lectura) ────────────────────────────
    /**
     * Consulta todas las tareas ordenadas por fecha/id descendente.
     * Devuelve un Flow reactivo: cada vez que la tabla cambie en SQLite,
     * Room emitirá automáticamente la nueva lista de tareas a la UI.
     */
    @Query("SELECT * FROM tasks_table ORDER BY id DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    /**
     * Consulta una tarea específica por su ID.
     */
    @Query("SELECT * FROM tasks_table WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Int): TaskEntity?

    // ──────────────────────────── CREATE (Creación) ──────────────────────────
    /**
     * Inserta una nueva tarea en SQLite.
     * En caso de conflicto de clave primaria, reemplaza el registro existente.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    // ──────────────────────────── UPDATE (Actualización) ──────────────────────
    /**
     * Actualiza los datos de una tarea existente comparando por su @PrimaryKey.
     * Retorna el número de filas modificadas.
     */
    @Update
    suspend fun updateTask(task: TaskEntity): Int

    /**
     * Actualización específica: cambiar estado completado por ID.
     */
    @Query("UPDATE tasks_table SET is_completed = :isCompleted WHERE id = :id")
    suspend fun updateTaskStatus(id: Int, isCompleted: Boolean): Int

    // ──────────────────────────── DELETE (Eliminación) ────────────────────────
    /**
     * Elimina una tarea específica de SQLite.
     * Retorna el número de filas eliminadas.
     */
    @Delete
    suspend fun deleteTask(task: TaskEntity): Int

    /**
     * Elimina una tarea por su ID.
     */
    @Query("DELETE FROM tasks_table WHERE id = :id")
    suspend fun deleteTaskById(id: Int): Int

    /**
     * Elimina todas las tareas de la tabla.
     */
    @Query("DELETE FROM tasks_table")
    suspend fun deleteAllTasks(): Int
}
