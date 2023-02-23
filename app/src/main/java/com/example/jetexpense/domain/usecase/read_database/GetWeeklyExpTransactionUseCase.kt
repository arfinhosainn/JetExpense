package com.example.jetexpense.domain.usecase.read_database

import com.example.jetexpense.data.local.entity.TransactionDto
import com.example.jetexpense.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeeklyExpTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(): Flow<List<TransactionDto>> {
        return transactionRepository.getWeeklyExpTransaction()
    }
}