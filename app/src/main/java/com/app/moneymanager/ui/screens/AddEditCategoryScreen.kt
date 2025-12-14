package com.app.moneymanager.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.app.moneymanager.ui.viewmodels.AddEditCategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCategoryScreen(
   viewModel: AddEditCategoryViewModel = hiltViewModel(),
   onActionSuccess: () -> Unit,
   onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val actionSuccess by viewModel.actionSuccess.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(actionSuccess) {
        if (actionSuccess != null) {
            onActionSuccess()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(
                message = it,
                actionLabel = "Ok",
                duration = SnackbarDuration.Short
            )
            viewModel.resetError()
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text =
                            if (state.isEditing)
                                "Редактировать категорию"
                            else
                                "Новая категория"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (state.isEditing) {
                        IconButton(onClick = {showDeleteDialog = true}) {
                            Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                    IconButton(onClick = viewModel::saveCategory) {
                        Icon(Icons.Default.Done, contentDescription = "Сохранить")
                    }
                }
            )
        }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = state.nameInput,
                onValueChange = viewModel::onNameChange,
                label = {
                    Text(
                        text = "Название категории"
                    )
                },
                isError = state.nameError,
                supportingText = {
                    if (state.nameError)
                        Text("Название не может быть пустым")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5F))
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                ) {
                    Text("Иконка (В разработке)", modifier = Modifier.weight(1f))
                    // Placeholder для иконки
                    Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(32.dp))
                }
            }

        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {showDeleteDialog = false},
            title = {
                Text("Удалить категорию?")
            },
            text = {
                Text("Вы уверены, что хотите удалить эту категорию? Транзакции, связанные с ней, могут стать 'без категории'.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCategory()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {showDeleteDialog = false}
                ) {
                    Text("Отмена")
                }
            }
        )
    }

}