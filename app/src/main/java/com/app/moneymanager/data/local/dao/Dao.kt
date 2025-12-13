package com.app.moneymanager.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.app.moneymanager.data.local.entity.CategoryEntity
import com.app.moneymanager.data.local.entity.TransactionEntity
import com.app.moneymanager.data.local.model.TransactionWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Long): CategoryEntity?

    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    fun getTransactionWithCategoryById(transactionId: Long): Flow<TransactionWithCategory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun deleteTransaction(transactionId: Long)

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getTransactionsWithCategories(): Flow<List<TransactionWithCategory>>


}