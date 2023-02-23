package com.example.jetexpense.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.jetexpense.data.local.entity.AccountDto
import com.example.jetexpense.data.local.entity.TransactionDto

@Database(entities = [TransactionDto::class, AccountDto::class], exportSchema = true, version = 1)
abstract class TransactionDatabase : RoomDatabase() {

    abstract val transactionDao: TransactionDao


}