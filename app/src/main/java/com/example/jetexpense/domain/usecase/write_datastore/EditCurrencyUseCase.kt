package com.example.jetexpense.domain.usecase.write_datastore

import com.example.jetexpense.domain.repository.DatastoreRepository
import javax.inject.Inject

class EditCurrencyUseCase @Inject constructor(private val datastoreRepository: DatastoreRepository) {
    suspend operator fun invoke(currency: String) {
        datastoreRepository.writeCurrencyToDataStore(currency)
    }
}