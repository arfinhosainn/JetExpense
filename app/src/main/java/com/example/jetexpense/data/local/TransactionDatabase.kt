package com.example.jetexpense.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.jetexpense.data.local.converter.DateConverter
import com.example.jetexpense.data.local.entity.AccountDto
import com.example.jetexpense.data.local.entity.TransactionDto


@TypeConverters(value = [DateConverter::class])
@Database(entities = [TransactionDto::class, AccountDto::class], exportSchema = true, version = 1)

abstract class TransactionDatabase : RoomDatabase() {


    abstract val transactionDao: TransactionDao


}