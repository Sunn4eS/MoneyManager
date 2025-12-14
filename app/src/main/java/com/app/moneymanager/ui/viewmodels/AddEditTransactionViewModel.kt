package com.app.moneymanager.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.moneymanager.domain.model.Category
import com.app.moneymanager.domain.model.Transaction
import com.app.moneymanager.domain.model.TransactionType
import com.app.moneymanager.domain.usecase.AddTransactionUseCase
import com.app.moneymanager.domain.usecase.DeleteTransactionUseCase
import com.app.moneymanager.domain.usecase.GetTransactionByUseCase
import com.app.moneymanager.domain.usecase.UpdateTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddEditTransactionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getTransactionByUseCase: GetTransactionByUseCase,

    //TODO use case для получения списка категорий
) : ViewModel() {
    private val transactionId: Long = savedStateHandle.get<Long>("transactionId") ?: 0L

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    init {
        if (transactionId != 0L) {
            loadTransaction(transactionId)
        }
        //TODO загрузить список категорий
    }

    private fun loadTransaction(id: Long) {
        viewModelScope.launch {
            try {
                val transaction = getTransactionByUseCase(id).first()
                if (transaction != null) {
                    _uiState.update { currentState -> currentState.copy(
                        amountInput = "%.2f".format(transaction.amount),
                        description = transaction.description,
                        selectedType = transaction.type,
                        selectedDate = transaction.date.toLocalDate(),
                        selectedCategoryId = transaction.category.id,
                        isEditing = true
                    ) }
                } else {
                    _uiState.update { it.copy(error = "Транзакция не найдена.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Ошибка загрузки: ${e.localizedMessage}") }
            }


        }
    }

    fun onAmountChange(input: String) {
        // Простая валидация: только цифры и одна точка
        val sanitized = input.filter { it.isDigit() || it == '.' }
        if (sanitized.count { it == '.' } <= 1) {
            _uiState.update { it.copy(amountInput = sanitized, amountError = false) }
        }
    }
    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onTypeSelect(type: TransactionType) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun onDateSelect(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun onCategorySelect(categoryId: Long) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun saveTransaction() {
        val amount = _uiState.value.amountInput.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(amountError = true, error = "Введите корректную сумму.") }
            return
        }

        _uiState.update { it.copy(error = null, amountError = false) }

        val transactionToSave = Transaction(
            id = transactionId, // Используем 0L для новой, ID для существующей
            amount = amount,
            description = _uiState.value.description?.trim(),
            date = _uiState.value.selectedDate.toDate(),
            type = _uiState.value.selectedType,
            category = _uiState.value.categoryList[_uiState.value.selectedCategoryId.toInt()] // ID выбранной категории
        )
        viewModelScope.launch {
            try {
                if (transactionId == 0L) {
                    addTransactionUseCase(transactionToSave)
                } else {
                    updateTransactionUseCase(transactionToSave)
                }
                _saveSuccess.value = true // Успешное сохранение, триггер навигации
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Ошибка сохранения: ${e.localizedMessage}") }
            }
        }
    }

    private fun Date.toLocalDate(): LocalDate = this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    private fun LocalDate.toDate(): Date = Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())
}

data class AddEditUiState(
    val amountInput: String = "",
    val description: String? = "",
    val selectedType: TransactionType = TransactionType.EXPENSE,
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedCategoryId: Long = 1L, // Дефолтная категория (например, 'Прочее')
    val categoryList: List<Category> = emptyList(), // Список для выбора
    val isEditing: Boolean = false,
    val amountError: Boolean = false,
    val error: String? = null
)