package com.merteroglu286.parkfind.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.merteroglu286.parkfind.utility.constant.DatabaseConstants.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class ParkModel(
    @PrimaryKey(autoGenerate = true)val id: Int = 0,
    val lat: Double,
    val lon: Double,
    val address: String,
    val time: String,
    val imagePath: String? = null
)