package com.app.moneymanager.data.local.mapper

import com.app.moneymanager.data.local.entity.CategoryEntity
import com.app.moneymanager.data.local.entity.TransactionEntity
import com.app.moneymanager.data.local.model.TransactionWithCategory
import com.app.moneymanager.domain.model.Category
import com.app.moneymanager.domain.model.Transaction
import com.app.moneymanager.domain.model.TransactionType

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        name = name,
        isExpense = isExpense,
        colorHex = colorHex
    )
}

fun TransactionWithCategory.toDomain(): Transaction {
    val type =
    if (transaction.isExpense) {
        TransactionType.EXPENSE
    } else {
        TransactionType.INCOME
    }

    return Transaction(
        id = transaction.id,
        amount = transaction.amount,
        description = transaction.description,
        date = transaction.date,
        category = category.toDomain(),
        type = type,
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        isExpense = isExpense,
        colorHex = colorHex
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        amount = amount,
        description = description,
        date = date,
        isExpense = type == TransactionType.EXPENSE,
        categoryId = category.id
    )
}