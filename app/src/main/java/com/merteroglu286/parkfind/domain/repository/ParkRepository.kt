package com.merteroglu286.parkfind.domain.repository

import com.merteroglu286.parkfind.domain.model.ParkModel
import kotlinx.coroutines.flow.Flow

interface ParkRepository {
    fun getAllParks(): Flow<List<ParkModel>>
    suspend fun insertPark(park: ParkModel)
    suspend fun deletePark(park: ParkModel)
}