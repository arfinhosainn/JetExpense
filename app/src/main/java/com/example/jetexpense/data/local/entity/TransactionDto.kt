package com.example.jetexpense.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.jetexpense.domain.model.Transaction
import java.util.*

@Entity(tableName = "transaction_table")
data class TransactionDto(
    @PrimaryKey
    @ColumnInfo(name = "timestamp")
    val date: Date,
    @ColumnInfo(name = "entry_date")
    val dateOfEntry: String,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "account")
    val account: String,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "transaction_type")
    val transactionType: String,
    @ColumnInfo(name = "transaction_title")
    val title: String
) {
    fun TransactionDto.toTransaction(): Transaction {
        return Transaction(
            date = date,
            amount = amount,
            account = account,
            category = category,
            transactionType = transactionType,
            title = title,
            dateOfEntry = dateOfEntry
        )
    }
}
