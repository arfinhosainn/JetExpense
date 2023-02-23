package com.example.jetexpense.domain.usecase.write_database

import com.example.jetexpense.data.local.entity.TransactionDto
import com.example.jetexpense.domain.repository.TransactionRepository
import javax.inject.Inject

class InsertNewTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {

    suspend operator fun invoke(dailyExpense: TransactionDto) {
        transactionRepository.insertTransaction(dailyExpense = dailyExpense)
    }

}