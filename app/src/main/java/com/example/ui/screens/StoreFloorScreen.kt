package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.NavigationTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreFloorScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val shelfSlots by viewModel.shelfSlots.collectAsState()
    val productStates by viewModel.productStates.collectAsState()
    val profile by viewModel.storeProfile.collectAsState()
    val browsingCustomers by viewModel.browsingCustomers.collectAsState()

    var selectedShelfForAssignment by remember { mutableStateOf<ShelfSlotEntity?>(null) }
    val statesMap = remember(productStates) { productStates.associateBy { it.productId } }

    val unlockedProducts = remember(productStates) {
        ProductCatalog.ALL_PRODUCTS.filter { prod ->
            val state = statesMap[prod.id]
            state?.isUnlocked == true
        }
    }

    // Modal to change assigned product
    if (selectedShelfForAssignment != null) {
        val targetShelf = selectedShelfForAssignment!!
        AlertDialog(
            onDismissRequest = { selectedShelfForAssignment = null },
            title = {
                Text(
                    text = "Assign Product to Shelf #${targetShelf.slotId}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Choose an unlocked item to display on this ${targetShelf.shelfType.lowercase()}:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    unlockedProducts.forEach { prod ->
                        val state = statesMap[prod.id]
                        val isMatchingType = when (targetShelf.shelfType) {
                            "FRIDGE" -> prod.shelfType == "FRIDGE" || prod.shelfType == "SHELF"
                            "FREEZER" -> prod.shelfType == "FREEZER"
                            else -> prod.shelfType == "SHELF"
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (targetShelf.assignedProductId == prod.id)
                                MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable(enabled = isMatchingType) {
                                    viewModel.assignProductToShelf(targetShelf.slotId, prod.id)
                                    selectedShelfForAssignment = null
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(prod.iconEmoji, fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(prod.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text(
                                            text = "Stockroom: ${state?.backroomStock ?: 0} boxes",
                                            fontSize = 11.sp,
                                            color = if ((state?.backroomStock ?: 0) > 0) Color(0xFF10B981) else Color(0xFFEF4444)
                                        )
                                    }
                                }
                                if (!isMatchingType) {
                                    Text("Requires ${prod.shelfType}", fontSize = 10.sp, color = Color(0xFFEF4444))
                                } else if (targetShelf.assignedProductId == prod.id) {
                                    Text("Active", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedShelfForAssignment = null }) {
                    Text("Close")
                }
            }
        )
    }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFF0F172A))) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: Live Store Aisle & Customer Traffic
            item(span = { GridItemSpan(2) }) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🏪", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Aisle & Shelves Management",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (profile?.isStoreOpen == true) Color(0xFF065F46) else Color(0xFF7F1D1D)
                            ) {
                                Text(
                                    text = if (profile?.isStoreOpen == true) "Customers Shopping" else "Store Closed",
                                    color = if (profile?.isStoreOpen == true) Color(0xFF34D399) else Color(0xFFF87171),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Customer animation avatars walking
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (profile?.isStoreOpen == true && browsingCustomers.isNotEmpty()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    browsingCustomers.take(3).forEach { name ->
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFF3B82F6).copy(alpha = 0.2f),
                                            modifier = Modifier.padding(end = 6.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("🚶‍♂️", fontSize = 12.sp)
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text(name, color = Color(0xFF93C5FD), fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }
                                Text("Browsing aisles...", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            } else {
                                Text(
                                    text = if (profile?.isStoreOpen == true) "Waiting for customers to enter..." else "Open store in top bar to let shoppers in!",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Shelf Slots
            items(shelfSlots.filter { it.isUnlocked }) { shelf ->
                val prod = shelf.assignedProductId?.let { ProductCatalog.getProduct(it) }
                val prodState = shelf.assignedProductId?.let { statesMap[it] }
                val fillRatio = if (shelf.maxCapacity > 0) shelf.currentStock.toFloat() / shelf.maxCapacity else 0f
                val backroomBoxes = prodState?.backroomStock ?: 0

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when {
                            shelf.currentStock == 0 -> Color(0xFFDC2626).copy(alpha = 0.7f)
                            fillRatio < 0.35f -> Color(0xFFF59E0B).copy(alpha = 0.5f)
                            else -> Color(0xFF334155)
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Shelf Slot Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = when (shelf.shelfType) {
                                    "FRIDGE" -> Color(0xFF0284C7)
                                    "FREEZER" -> Color(0xFF4F46E5)
                                    else -> Color(0xFFD97706)
                                }
                            ) {
                                Text(
                                    text = when (shelf.shelfType) {
                                        "FRIDGE" -> "❄️ FRIDGE"
                                        "FREEZER" -> "🧊 FREEZER"
                                        else -> "🪵 SHELF"
                                    },
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }

                            IconButton(
                                onClick = { selectedShelfForAssignment = shelf },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "Assign Product",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Product Visual
                        if (prod != null) {
                            Text(prod.iconEmoji, fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = prod.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Price: $${String.format("%.2f", prodState?.retailPrice ?: prod.baseMarketPrice)}",
                                color = Color(0xFF34D399),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Text("📦", fontSize = 34.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Empty Shelf", color = Color(0xFF94A3B8), fontSize = 12.sp)
                            Text("Tap icon to set", color = Color(0xFF64748B), fontSize = 10.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Stock Bar
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Stock: ${shelf.currentStock}/${shelf.maxCapacity}",
                                    color = if (shelf.currentStock == 0) Color(0xFFEF4444) else Color(0xFFCBD5E1),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Boxes: $backroomBoxes",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { fillRatio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = when {
                                    fillRatio > 0.5f -> Color(0xFF10B981)
                                    fillRatio > 0.2f -> Color(0xFFF59E0B)
                                    else -> Color(0xFFEF4444)
                                },
                                trackColor = Color(0xFF334155)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Restock Button
                        Button(
                            onClick = { viewModel.restockShelf(shelf.slotId) },
                            enabled = shelf.assignedProductId != null && backroomBoxes > 0 && shelf.currentStock < shelf.maxCapacity,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2563EB),
                                disabledContainerColor = Color(0xFF334155)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .testTag("restock_shelf_${shelf.slotId}")
                        ) {
                            Text(
                                text = if (backroomBoxes == 0) "No Boxes" else if (shelf.currentStock >= shelf.maxCapacity) "Full" else "Restock 📦",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Storage overview & Quick Order button
            item(span = { GridItemSpan(2) }) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Running low on items?",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Open Wholesale PC to order delivery boxes",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                        Button(
                            onClick = { viewModel.setTab(NavigationTab.ORDER_STOCK) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Order Stock 🚚", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
