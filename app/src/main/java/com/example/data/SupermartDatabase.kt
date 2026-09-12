package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        StoreProfileEntity::class,
        ProductStateEntity::class,
        ShelfSlotEntity::class,
        DailyRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SupermartDatabase : RoomDatabase() {
    abstract fun supermartDao(): SupermartDao

    companion object {
        @Volatile
        private var INSTANCE: SupermartDatabase? = null

        fun getInstance(context: Context): SupermartDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SupermartDatabase::class.java,
                    "supermart_simulator_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
