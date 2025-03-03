package com.merteroglu286.parkfind.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "park_table")
data class ParkModel(
    @PrimaryKey(autoGenerate = true)val id: Int = 0,
    val lat: Double,
    val lon: Double,
    val address: String,
    val time: String,
    val imagePath: String? = null
)