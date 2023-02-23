package com.example.jetexpense.domain.usecase.write_datastore

import com.example.jetexpense.domain.repository.DatastoreRepository
import javax.inject.Inject

class EraseDatastoreUseCase @Inject constructor(private val datastoreRepository: DatastoreRepository) {
    suspend operator fun invoke() {
        datastoreRepository.eraseDataStore()
    }
}