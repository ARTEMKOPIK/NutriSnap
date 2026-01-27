package com.nutrisnap.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FoodEntry::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
}
