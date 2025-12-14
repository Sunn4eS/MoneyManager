package com.app.moneymanager.domain.repository

import com.app.moneymanager.domain.model.Category
import com.app.moneymanager.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    suspend fun saveTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transactionId: Long)
    suspend fun updateTransaction(transaction: Transaction)
    fun getTransactionWithCategoryById(transactionId: Long): Flow<Transaction>
}

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    suspend fun getCategoryById(categoryId: Long): Category?
    suspend fun saveCategory(category: Category)
    suspend fun deleteCategory(categoryId: Long)
}