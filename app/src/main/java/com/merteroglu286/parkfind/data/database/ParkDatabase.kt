package com.merteroglu286.parkfind.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters
import com.merteroglu286.parkfind.data.dao.ParkDao
import com.merteroglu286.parkfind.domain.model.ParkModel
import com.merteroglu286.parkfind.utility.UriConverter

@Database(entities = [ParkModel::class], version = 2, exportSchema = false)
@TypeConverters(UriConverter::class)
abstract class ParkDatabase : RoomDatabase() {

    abstract fun parkDao(): ParkDao

    companion object {
        @Volatile
        private var INSTANCE: ParkDatabase? = null

        fun getDatabase(context: Context): ParkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ParkDatabase::class.java,
                    "park_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}