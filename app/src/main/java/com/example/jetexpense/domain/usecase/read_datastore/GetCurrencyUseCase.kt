package com.example.jetexpense.domain.usecase.read_datastore

import com.example.jetexpense.domain.repository.DatastoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrencyUseCase @Inject constructor(private val datastoreRepository: DatastoreRepository) {
    suspend operator fun invoke() : Flow<String> =
        datastoreRepository.readCurrencyFromDataStore()
}