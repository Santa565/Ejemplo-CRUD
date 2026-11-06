package com.ejemplo.crudroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.ejemplo.crudroom.data.AppDatabase
import com.ejemplo.crudroom.data.TaskRepository
import com.ejemplo.crudroom.ui.TaskScreen
import com.ejemplo.crudroom.ui.TaskViewModel
import com.ejemplo.crudroom.ui.TaskViewModelFactory
import com.ejemplo.crudroom.ui.theme.EjemploCRUDTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Inicialización de la Base de Datos SQLite mediante Room
        val database = AppDatabase.getDatabase(applicationContext)

        // 2. Inicialización del Repositorio
        val repository = TaskRepository(database.taskDao())

        // 3. Inicialización del ViewModel con su Factory
        val viewModelFactory = TaskViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]

        // 4. Configurar la UI con Jetpack Compose
        setContent {
            EjemploCRUDTheme {
                TaskScreen(viewModel = viewModel)
            }
        }
    }
}
