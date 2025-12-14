package com.app.moneymanager.data.repository

import com.app.moneymanager.data.local.dao.TransactionDao
import com.app.moneymanager.domain.model.Transaction
import com.app.moneymanager.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.app.moneymanager.data.local.mapper.toDomain
import com.app.moneymanager.data.local.mapper.toEntity
import com.app.moneymanager.data.local.model.TransactionWithCategory
import com.app.moneymanager.domain.model.Category
import com.app.moneymanager.domain.repository.CategoryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return dao.getTransactionsWithCategories().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun saveTransaction(transaction: Transaction) {
        dao.insertTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transactionId: Long) {
        dao.deleteTransaction(transactionId)
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        dao.insertTransaction(transaction.toEntity())
    }
    override fun getTransactionWithCategoryById(transactionId: Long): Flow<Transaction> {
        return dao.getTransactionWithCategoryById(transactionId).map { it.toDomain() }
    }
}

class CategoryRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
): CategoryRepository {

    private val initialCategories = listOf(
        Category(id = 1L, name = "Прочее", true, "ff"),
        Category(id = 2L, name = "Еда", true, "ff"),
        Category(id = 3L, name = "Зарплата", false, "ff"),
        Category(id = 4L, name = "Транспорт", true, "ff"),
        Category(id = 5L, name = "Развлечения", true, "ff")
    )

    override fun getAllCategories(): Flow<List<Category>>
    {

//        return dao.getAllCategories().map { list ->
//            list.map { it.toDomain() }
//        }
        return flow {
            delay(300)
            emit(initialCategories)
        }
    }
    override suspend fun getCategoryById(categoryId: Long) : Category?
    {
        return dao.getCategoryById(categoryId)?.toDomain()
    }
    override suspend fun saveCategory(category: Category)
    {
        dao.insertCategory(category.toEntity())
    }
    override suspend fun deleteCategory(categoryId: Long)
    {
        dao.deleteCategory(categoryId)
    }
}