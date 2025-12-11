package com.app.moneymanager.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class TransactionWithCategory(
    @Embedded
    val transaction: TransactionEntity,

    @Relation(
        parentColumn = "categoryId", // Поле из TransactionEntity
        entityColumn = "id"          // Поле из CategoryEntity
    )
    val category: CategoryEntity
)
