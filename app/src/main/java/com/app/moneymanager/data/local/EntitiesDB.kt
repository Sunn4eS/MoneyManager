package com.app.moneymanager.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.app.moneymanager.domain.model.Category
import com.app.moneymanager.domain.model.Transaction
import com.app.moneymanager.domain.model.TransactionType
import java.util.Date

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val isExpense: Boolean,
    val colorHex: String

)

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val description: String?,
    val date: Date,
    val isExpense: Boolean,
    val categoryId: Long
)