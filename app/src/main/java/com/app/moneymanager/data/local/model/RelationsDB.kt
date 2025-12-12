package com.app.moneymanager.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.app.moneymanager.data.local.entity.CategoryEntity
import com.app.moneymanager.data.local.entity.TransactionEntity

data class TransactionWithCategory(
    @Embedded
    val transaction: TransactionEntity,

    @Relation(
        parentColumn = "categoryId", // Поле из TransactionEntity
        entityColumn = "id"          // Поле из CategoryEntity
    )
    val category: CategoryEntity
)
