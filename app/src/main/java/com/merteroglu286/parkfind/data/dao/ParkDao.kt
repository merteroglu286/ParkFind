package com.merteroglu286.parkfind.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.merteroglu286.parkfind.domain.model.ParkModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkDao {
    @Insert
    suspend fun insert(park: ParkModel)

    @Query("SELECT * FROM park_table")
    fun getAllParks(): Flow<List<ParkModel>>

    @Delete
    suspend fun delete(park: ParkModel)
}