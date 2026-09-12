package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SupermartDao {

    @Query("SELECT * FROM store_profile WHERE id = 1")
    fun getStoreProfile(): Flow<StoreProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStoreProfile(profile: StoreProfileEntity)

    @Query("SELECT * FROM product_states")
    fun getAllProductStates(): Flow<List<ProductStateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProductStates(states: List<ProductStateEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProductState(state: ProductStateEntity)

    @Query("SELECT * FROM shelf_slots ORDER BY slotId ASC")
    fun getAllShelfSlots(): Flow<List<ShelfSlotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveShelfSlots(slots: List<ShelfSlotEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveShelfSlot(slot: ShelfSlotEntity)

    @Query("SELECT * FROM daily_records ORDER BY dayNumber DESC")
    fun getDailyRecords(): Flow<List<DailyRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordDay(record: DailyRecordEntity)
}
