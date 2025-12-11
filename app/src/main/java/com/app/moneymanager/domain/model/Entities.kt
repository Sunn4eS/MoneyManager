package com.app.moneymanager.domain.model

import java.util.Date

enum class TransactionType {
    INCOME,
    EXPENSE,
}

data class Transaction (
    val id: Long = 0,
    val amount: Double,
    val description: String?,
    val date: Date,
    val type: TransactionType,
    val category: Category,
)

data class Category(
    val id: Long = 0,
    val name: String,
    val isExpense: Boolean,
    val colorHex: String,
)