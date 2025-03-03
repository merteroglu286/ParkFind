package com.merteroglu286.parkfind.domain.usecase

import com.merteroglu286.parkfind.domain.model.ParkModel
import com.merteroglu286.parkfind.domain.repository.ParkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ParkUseCase @Inject constructor(
    private val parkRepository: ParkRepository
) {
    fun getAllParks(): Flow<List<ParkModel>> = parkRepository.getAllParks()

    suspend fun insertPark(park: ParkModel) = parkRepository.insertPark(park)

    suspend fun deletePark(park: ParkModel) = parkRepository.deletePark(park)
}
