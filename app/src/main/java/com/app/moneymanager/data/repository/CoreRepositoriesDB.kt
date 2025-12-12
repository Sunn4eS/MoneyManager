package com.app.moneymanager.data.repository

import com.app.moneymanager.data.local.dao.TransactionDao
import com.app.moneymanager.domain.model.Transaction
import com.app.moneymanager.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.app.moneymanager.data.local.mapper.toDomain
import com.app.moneymanager.data.local.mapper.toEntity
import com.app.moneymanager.domain.model.Category
import com.app.moneymanager.domain.repository.CategoryRepository

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        // Получаем поток связанных данных из DAO и преобразуем его в доменные модели
        return dao.getTransactionsWithCategories().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun saveTransaction(transaction: Transaction) {
        // Преобразуем доменную модель в Room Entity перед сохранением
        dao.insertTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transactionId: Long) {
        dao.deleteTransaction(transactionId)
    }
}

class CategoryRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
): CategoryRepository {
    override fun getAllCategories(): Flow<List<Category>>
    {
        return dao.getAllCategories().map { list ->
            list.map { it.toDomain() }
        }
    }
    override suspend fun getCategoryById(categoryId: Long) : Category?
    {
        return dao.getCategoryById(categoryId)?.toDomain()
    }
    override suspend fun saveCategory(category: Category)
    {
        TODO()
    }
    override suspend fun deleteCategory(categoryId: Long)
    {
        TODO()
    }
}