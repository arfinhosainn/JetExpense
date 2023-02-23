package com.example.jetexpense.domain.usecase.read_database

import com.example.jetexpense.data.local.entity.AccountDto
import com.example.jetexpense.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAccountsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {

    operator fun invoke(): Flow<List<AccountDto>> {
        return transactionRepository.getAccounts()
    }
}