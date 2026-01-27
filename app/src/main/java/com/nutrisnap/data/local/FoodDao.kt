package com.nutrisnap.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Query("SELECT * FROM food_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<FoodEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: FoodEntry)

    @Update
    suspend fun updateEntry(entry: FoodEntry)

    @Delete
    suspend fun deleteEntry(entry: FoodEntry)

    @Query("SELECT SUM(calories) FROM food_entries WHERE timestamp >= :startOfDay")
    fun getTodayCalories(startOfDay: Long): Flow<Int?>

    @Query(
        """
        SELECT
            COALESCE(SUM(calories), 0) as calories,
            COALESCE(SUM(proteins), 0.0) as proteins,
            COALESCE(SUM(fats), 0.0) as fats,
            COALESCE(SUM(carbs), 0.0) as carbs
        FROM food_entries
        WHERE timestamp >= :startOfDay
    """,
    )
    fun getDailyStats(startOfDay: Long): Flow<DailyStats>

    @Query("SELECT * FROM food_entries WHERE timestamp >= :startOfDay ORDER BY timestamp DESC")
    fun getEntriesAfter(startOfDay: Long): Flow<List<FoodEntry>>
}
