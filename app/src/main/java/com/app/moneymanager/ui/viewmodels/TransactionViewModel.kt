package com.app.moneymanager.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.moneymanager.domain.model.Transaction
import com.app.moneymanager.domain.usecase.CalculateBalanceUseCase
import com.app.moneymanager.domain.usecase.DeleteTransactionUseCase
import com.app.moneymanager.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val calculateBalanceUseCase: CalculateBalanceUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    // TODO Delete
): ViewModel() {
    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState

    init {
        collectData()
    }

    private fun collectData() {
        viewModelScope.launch {
            combine(
                getTransactionsUseCase(),
                calculateBalanceUseCase(),
            ) {
               transactions, balance ->
               _uiState.value.copy(
                   transactions = transactions,
                   currentBalance = balance,
                   isLoading = false
               )
            }.collect {newState ->
                _uiState.value = newState
            }
        }
    }

    // --- События UI: Обработка действий пользователя ---

    /**
     * Вызывается Composable, когда пользователь нажимает "Удалить".
     */
    fun onDeleteTransactionClicked(transaction: Transaction) {
        // В реальной реализации здесь будет запуск DeleteTransactionUseCase
        // viewModelScope.launch { deleteTransactionUseCase(transaction.id) }
        println("Попытка удалить транзакцию с ID: ${transaction.id}")
    }

    // Здесь будут другие функции, например, fun onFilterToggled(type: TransactionType)...
}

data class TransactionsUiState(
    val transactions: List<Transaction> = emptyList(),
    val currentBalance: Double = 0.0,
    val isLoading: Boolean = true,
    val error: String? = null
)