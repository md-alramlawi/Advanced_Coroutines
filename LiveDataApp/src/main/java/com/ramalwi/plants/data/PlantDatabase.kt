package com.ramalwi.plants.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ramalwi.plant.models.Plant

@Database(entities = [Plant::class], version = 1, exportSchema = false)
abstract class PlantDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

    companion object {

        private const val DATABASE_NAME = "plants_database"

        @Volatile
        private var instance: PlantDatabase? = null

        fun getInstance(context: Context): PlantDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(
                        context
                    ).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): PlantDatabase {
            return Room.databaseBuilder(context, PlantDatabase::class.java, DATABASE_NAME).build()
        }
    }
}


