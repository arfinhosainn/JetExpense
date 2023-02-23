package com.example.jetexpense.domain.model

import java.util.*

data class Transaction(
    val date: Date,
    val dateOfEntry: String,
    val amount: Double,
    val account: String,
    val category: String,
    val transactionType: String,
    val title: String,
)