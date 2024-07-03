package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoApp()
        }
    }
}

data class TodoItem(val title: String, val isCompleted: MutableState<Boolean> = mutableStateOf(false))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp() {
    val todoItems = remember { mutableStateListOf<TodoItem>() }
    var showAddDialog by remember { mutableStateOf(false) }
    var newTodoText by remember { mutableStateOf("") }
    var displayError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(id = R.string.todo)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(painter = painterResource(id = R.drawable.add_icon), contentDescription = stringResource(id = R.string.add))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(todoItems) { item ->
                    TodoRow(item = item)
                }
            }
        }

        if (showAddDialog) {
            AddTodoDialog(
                newItemText = newTodoText,
                onNewItemTextChanged = { newTodoText = it },
                onSave = {
                    if (newTodoText.isNotBlank()) {
                        todoItems.add(TodoItem(newTodoText))
                        newTodoText = ""
                        showAddDialog = false
                        displayError = false
                    } else {
                        displayError = true
                    }
                },
                onCancel = {
                    newTodoText = ""
                    showAddDialog = false
                    displayError = false
                },
                showError = displayError
            )
        }
    }
}

@Composable
fun TodoRow(item: TodoItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = item.title)
        Checkbox(
            checked = item.isCompleted.value,
            onCheckedChange = { item.isCompleted.value = it }
        )
    }
}

@Composable
fun AddTodoDialog(
    newItemText: String,
    onNewItemTextChanged: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    showError: Boolean
) {
    Dialog(
        onDismissRequest = { onCancel() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = newItemText,
                    onValueChange = onNewItemTextChanged,
                    label = { Text(stringResource(id = R.string.new_todo)) },
                    trailingIcon = {
                        IconButton(onClick = { onNewItemTextChanged("") }) {
                            Icon(painter = painterResource(id = R.drawable.close_icon), contentDescription = stringResource(id = R.string.clear))
                        }
                    }
                )
                if (showError) {
                    Text(
                        text = stringResource(id = R.string.please_enter_todo),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onSave,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(id = R.string.save))
                    }
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(id = R.string.cancel))
                    }
                }
            }
        }
    }
}
