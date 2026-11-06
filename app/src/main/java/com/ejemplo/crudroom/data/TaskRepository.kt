package com.ejemplo.crudroom.data

import kotlinx.coroutines.flow.Flow

/**
 * Repositorio: Fuente de verdad única que abstrae el acceso a los datos
 * para la capa de ViewModel/UI.
 */
class TaskRepository(private val taskDao: TaskDao) {

    // Flujo reactivo con la lista completa de tareas
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    // Operación CREATE
    suspend fun insert(task: TaskEntity): Long {
        return taskDao.insertTask(task)
    }

    // Operación UPDATE
    suspend fun update(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    // Actualizar estado de completado
    suspend fun toggleCompleted(task: TaskEntity) {
        taskDao.updateTaskStatus(task.id, !task.isCompleted)
    }

    // Operación DELETE (elemento individual)
    suspend fun delete(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    // Operación DELETE (todas las tareas)
    suspend fun deleteAll() {
        taskDao.deleteAllTasks()
    }
}
