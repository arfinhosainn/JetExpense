package com.example.jetexpense.domain.usecase.write_database

import com.example.jetexpense.data.local.entity.AccountDto
import com.example.jetexpense.domain.repository.TransactionRepository
import javax.inject.Inject

class InsertAccountsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(accounts: List<AccountDto>) {
        transactionRepository.insertAccount(accounts = accounts)
    }

}