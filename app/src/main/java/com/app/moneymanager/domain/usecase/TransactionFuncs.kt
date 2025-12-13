package com.app.moneymanager.domain.usecase

import com.app.moneymanager.data.local.model.TransactionWithCategory
import com.app.moneymanager.domain.model.Transaction
import com.app.moneymanager.domain.model.TransactionType
import com.app.moneymanager.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
)  {
    operator fun invoke(): Flow<List<Transaction>> {
        return repository.getAllTransactions().map {
            transitions -> transitions.sortedByDescending { it.date }}
    }
}

class GetTransactionByUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(transactionId: Long): Flow<Transaction> {
        return repository.getTransactionWithCategoryById(transactionId)
    }
}

class AddTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        if (transaction.amount <= 0) {
            throw IllegalArgumentException("Sum of transaction should be above zero.")
        }
        repository.saveTransaction(transaction)
    }
}

class CalculateBalanceUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<Double> {
        return repository.getAllTransactions().map {
            transactions -> transactions.sumOf {
                transaction ->
                when (transaction.type) {
                    TransactionType.INCOME -> transaction.amount
                    TransactionType.EXPENSE -> -transaction.amount
                }
        }
        }
    }
}

class UpdateTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
     suspend operator fun invoke(transaction: Transaction) {
        if (transaction.amount <= 0)
            throw IllegalArgumentException("Sum of transaction should be above zero.")
        if (transaction.id == 0L)
            throw IllegalArgumentException("Id can't be zero")
        return repository.updateTransaction(transaction)
    }
}
class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transactionId: Long) {
        repository.deleteTransaction(transactionId)
    }
}