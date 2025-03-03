package com.merteroglu286.parkfind.data.repository

import com.merteroglu286.parkfind.data.dao.ParkDao
import com.merteroglu286.parkfind.domain.model.ParkModel
import com.merteroglu286.parkfind.domain.repository.ParkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ParkRepositoryImpl @Inject constructor(
    private val parkDao: ParkDao
) : ParkRepository {

    override fun getAllParks(): Flow<List<ParkModel>> = parkDao.getAllParks()

    override suspend fun insertPark(park: ParkModel) = parkDao.insert(park)

    override suspend fun deletePark(park: ParkModel) = parkDao.delete(park)
}
