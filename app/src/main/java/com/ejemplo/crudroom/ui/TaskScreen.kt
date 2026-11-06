package com.ejemplo.crudroom.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ejemplo.crudroom.data.TaskEntity
import kotlinx.coroutines.launch

/**
 * Pantalla principal donde se integran todas las operaciones CRUD.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    // READ: Observación reactiva del StateFlow proveniente de Room
    val taskList by viewModel.tasks.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TaskEntity?>(null) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Filtrado en memoria de las tareas leídas de SQLite
    val filteredTasks = remember(taskList, searchQuery) {
        if (searchQuery.isBlank()) {
            taskList
        } else {
            taskList.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val completedCount = taskList.count { it.isCompleted }
    val totalCount = taskList.size

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("CRUD SQLite (Room)")
                        Text(
                            text = "$completedCount de $totalCount completadas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    if (taskList.isNotEmpty()) {
                        IconButton(onClick = { showDeleteAllDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Eliminar todas las tareas",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    taskToEdit = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear nueva tarea")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Barra de búsqueda rápida
            if (taskList.isNotEmpty()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar tarea...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isNotBlank()) {
                            "No se encontraron tareas con: \"$searchQuery\""
                        } else {
                            "No hay tareas en la Base de Datos SQLite.\nPresiona '+' para crear una."
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Lista reactiva con LazyColumn
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = filteredTasks,
                        key = { it.id }
                    ) { task ->
                        TaskItem(
                            task = task,
                            onToggleCompletion = {
                                viewModel.toggleTaskCompletion(task)
                            },
                            onEdit = {
                                taskToEdit = task
                                showDialog = true
                            },
                            onDelete = {
                                viewModel.deleteTask(task)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Tarea \"${task.title}\" eliminada"
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

        // Diálogo para CREATE y UPDATE
        if (showDialog) {
            TaskDialog(
                taskToEdit = taskToEdit,
                onDismiss = {
                    showDialog = false
                    taskToEdit = null
                },
                onConfirm = { title, description ->
                    if (taskToEdit == null) {
                        // CREATE
                        viewModel.addTask(title, description)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Tarea agregada correctamente")
                        }
                    } else {
                        // UPDATE
                        viewModel.updateTask(
                            taskToEdit!!.copy(
                                title = title,
                                description = description
                            )
                        )
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Tarea actualizada correctamente")
                        }
                    }
                }
            )
        }

        // Diálogo de confirmación para eliminar todo
        if (showDeleteAllDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteAllDialog = false },
                title = { Text("¿Eliminar todas las tareas?") },
                text = { Text("Esta acción eliminará todos los registros de la base de datos SQLite y no se puede deshacer.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteAllTasks()
                            showDeleteAllDialog = false
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Todas las tareas fueron eliminadas")
                            }
                        }
                    ) {
                        Text("Eliminar todo", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteAllDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
