package com.example.jetexpense.di

import android.content.Context
import androidx.room.Room
import com.example.jetexpense.data.local.TransactionDao
import com.example.jetexpense.data.local.TransactionDatabase
import com.example.jetexpense.data.repository.DataStoreRepositoryImpl
import com.example.jetexpense.data.repository.TransactionRepositoryImpl
import com.example.jetexpense.domain.repository.DatastoreRepository
import com.example.jetexpense.domain.repository.TransactionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatastoreRepository(@ApplicationContext context: Context) : DatastoreRepository {
        return DataStoreRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideExpenseRepository(transactionDao: TransactionDao) : TransactionRepository
            = TransactionRepositoryImpl(transactionDao)

    @Provides
    @Singleton
    fun provideExpenseDatabase(@ApplicationContext context: Context) : TransactionDatabase {
        return Room.databaseBuilder(context, TransactionDatabase::class.java, "transactionDB")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideExpenseDao(database: TransactionDatabase) = database.transactionDao

}