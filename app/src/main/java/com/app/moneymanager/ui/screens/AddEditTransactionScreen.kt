package com.app.moneymanager.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.app.moneymanager.domain.model.Category
import com.app.moneymanager.domain.model.TransactionType
import com.app.moneymanager.ui.theme.ExpenseRed
import com.app.moneymanager.ui.theme.IncomeGreen
import com.app.moneymanager.ui.viewmodels.AddEditTransactionViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    viewModel: AddEditTransactionViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            onSaveSuccess()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(
                message = it,
                actionLabel = "OK",
                duration = SnackbarDuration.Short
            )
            //TODO В реальной реализации здесь должна быть очистка ошибки в ViewModel
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = if (state.isEditing) "Редактировать транзакцию" else "Новая транзакция") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::saveTransaction) {
                        Icon(
                            imageVector = Icons.Filled.Check, contentDescription = "Сохранить")
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
                value = state.amountInput,
                onValueChange = viewModel::onAmountChange,
                label = { Text("Сумма (₽)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = state.amountError,
                supportingText = { if (state.amountError) Text("Введите корректную сумму") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            state.description?.let {
                OutlinedTextField(
                    value = it,
                    onValueChange = viewModel::onDescriptionChange,
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TransactionTypeButton(
                    label = "Расход",
                    type = TransactionType.EXPENSE,
                    isSelected = state.selectedType == TransactionType.EXPENSE,
                    color = ExpenseRed,
                    onClick = viewModel::onTypeSelect,
                    modifier = Modifier.weight(1f)
                )
                TransactionTypeButton(
                    label = "Доход",
                    type = TransactionType.INCOME,
                    isSelected = state.selectedType == TransactionType.INCOME,
                    color = IncomeGreen,
                    onClick = viewModel::onTypeSelect,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Выбор Даты (Placeholder)
            DateField(
                selectedDate = state.selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy",
                    Locale("ru")
                )),
                // TODOСейчас просто выбираем текущую дату, пока нет DatePicker
                onDateSelected = viewModel::onDateSelect
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Выбор Категории (Placeholder)
            CategoryDropdown(
                selectedCategoryId = state.selectedCategoryId,
                categoryList = state.categoryList,
                onCategorySelected = viewModel::onCategorySelect
            )
        }
    }
}

@Composable
fun TransactionTypeButton(
    label: String,
    type: TransactionType,
    isSelected: Boolean,
    color: Color,
    onClick: (TransactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = { onClick(type)},
        modifier = Modifier.height(56.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor =
                if (isSelected)
                    color.copy(alpha = 0.1f)
                else
                    MaterialTheme.colorScheme.surface,
            contentColor = color
        ),
        border =
            if (isSelected)
                ButtonDefaults.outlinedButtonBorder //.copy(color = color)
            else
                ButtonDefaults.outlinedButtonBorder

    ) {
        Text(label)
    }
}

@Composable
fun DateField(
    selectedDate: String,
    onDateSelected: (java.time.LocalDate) -> Unit
) {
    var isDatePickerVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedDate,
        onValueChange = {}, // Нельзя редактировать вручную
        label = { Text("Дата") },
        readOnly = true,
        trailingIcon = {
            Icon(
                Icons.Default.DateRange,
                contentDescription = "Выбрать дату",
                modifier = Modifier.clickable { isDatePickerVisible = true }
            )
        },
        modifier = Modifier.fillMaxWidth()
    )

    //TODO Временная реализация DatePicker Placeholder
    if (isDatePickerVisible) {
        AlertDialog(
            onDismissRequest = { isDatePickerVisible = false },
            title = { Text("Выбор даты (Заглушка)") },
            text = { Text("Здесь будет Material3 DatePicker. Текущая дата: $selectedDate") },
            confirmButton = {
                Button(onClick = {
                    // Просто закрываем диалог
                    isDatePickerVisible = false
                }) {
                    Text("OK")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    selectedCategoryId: Long,
    categoryList: List<Category>,
    onCategorySelected: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedName = categoryList.find { it.id == selectedCategoryId }?.name ?: "Выберите категорию"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Категория") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categoryList.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onCategorySelected(category.id)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

