package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "store_profile")
data class StoreProfileEntity(
    @PrimaryKey val id: Int = 1,
    val storeName: String = "Techno Mart",
    val money: Double = 180.00,
    val storeLevel: Int = 1,
    val xp: Int = 0,
    val currentDay: Int = 1,
    val dayMinutes: Int = 480, // 8:00 AM (8 * 60)
    val isStoreOpen: Boolean = false,
    val storeExpansionLevel: Int = 1, // 1 to 4
    val unlockedLicenses: Int = 1, // 1 to 6
    val cashierHired: Boolean = false,
    val restockerHired: Boolean = false,
    val totalRevenue: Double = 0.0,
    val totalCustomersServed: Int = 0,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true
)

@Entity(tableName = "product_states")
data class ProductStateEntity(
    @PrimaryKey val productId: String,
    val retailPrice: Double,
    val marketPrice: Double,
    val backroomStock: Int, // number of boxes in storage
    val isUnlocked: Boolean
)

@Entity(tableName = "shelf_slots")
data class ShelfSlotEntity(
    @PrimaryKey val slotId: Int, // 1..16
    val sectionNumber: Int, // 1..4 (which store expansion section this belongs to)
    val shelfType: String, // "SHELF", "FRIDGE", "FREEZER"
    val assignedProductId: String?,
    val currentStock: Int,
    val maxCapacity: Int = 12,
    val isUnlocked: Boolean = true
)

@Entity(tableName = "daily_records")
data class DailyRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dayNumber: Int,
    val revenue: Double,
    val wholesaleCost: Double,
    val rentCost: Double,
    val staffCost: Double,
    val netProfit: Double,
    val customersServed: Int,
    val satisfactionScore: Int
)
