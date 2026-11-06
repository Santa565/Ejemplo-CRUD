package com.ejemplo.crudroom.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ejemplo.crudroom.data.TaskEntity
import com.ejemplo.crudroom.data.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel que conecta la UI de Jetpack Compose con el repositorio de Room/SQLite.
 * Mantiene el estado de la UI sobreviviendo a cambios de configuración.
 */
class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    // ──────────────────────────── READ ────────────────────────────
    /**
     * StateFlow que emite la lista actualizada de tareas.
     * Al utilizar stateIn con WhileSubscribed(5000), se detiene la recolección
     * si la app pasa a segundo plano, ahorrando recursos.
     */
    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // ──────────────────────────── CREATE ────────────────────────────
    /**
     * Inserta una nueva tarea en la base de datos SQLite.
     */
    fun addTask(title: String, description: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val newTask = TaskEntity(
                title = title.trim(),
                description = description.trim(),
                isCompleted = false
            )
            repository.insert(newTask)
        }
    }

    // ──────────────────────────── UPDATE ────────────────────────────
    /**
     * Actualiza los datos de una tarea existente.
     */
    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.update(task)
        }
    }

    /**
     * Alterna el estado completado / pendiente de una tarea.
     */
    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            repository.toggleCompleted(task)
        }
    }

    // ──────────────────────────── DELETE ────────────────────────────
    /**
     * Elimina una tarea específica de SQLite.
     */
    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }

    /**
     * Elimina todas las tareas de la base de datos.
     */
    fun deleteAllTasks() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}

/**
 * Factory para crear la instancia de TaskViewModel con la dependencia de TaskRepository.
 */
class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida: ${modelClass.name}")
    }
}
