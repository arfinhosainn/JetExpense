package com.example.jetexpense.domain.usecase.write_datastore

import com.example.jetexpense.domain.repository.DatastoreRepository
import javax.inject.Inject

class EditLimitKeyUseCase @Inject constructor(
    private val datastoreRepository: DatastoreRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        datastoreRepository.writeLimitKeyToDataStore(enabled)
    }
}