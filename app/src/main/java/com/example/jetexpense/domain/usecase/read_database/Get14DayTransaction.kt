package com.example.jetexpense.domain.usecase.read_database

import com.example.jetexpense.data.local.entity.TransactionDto
import com.example.jetexpense.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Get14DayTransaction @Inject constructor(
    private val transactionRepository: TransactionRepository
) {

    operator fun invoke(transactionType: String): Flow<List<TransactionDto>> {
        return transactionRepository.get14DayTransaction(transactionType = transactionType)
    }
}