package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProductCatalog
import com.example.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WholesaleMarketScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val productStates by viewModel.productStates.collectAsState()
    val wholesaleCart by viewModel.wholesaleCart.collectAsState()
    val profile by viewModel.storeProfile.collectAsState()

    val categories = listOf("All", "Bakery & Pantry", "Dairy & Eggs", "Breakfast & Coffee", "Beverages", "Frozen Foods", "Household & Cleaning")
    var selectedCategory by remember { mutableStateOf("All") }

    val statesMap = remember(productStates) { productStates.associateBy { it.productId } }

    val availableProducts = remember(selectedCategory, productStates) {
        ProductCatalog.ALL_PRODUCTS.filter { prod ->
            val state = statesMap[prod.id]
            val isUnlocked = state?.isUnlocked == true
            val matchesCategory = selectedCategory == "All" || prod.category == selectedCategory
            isUnlocked && matchesCategory
        }
    }

    // Calculate cart total
    val totalCost = wholesaleCart.entries.sumOf { (pId, count) ->
        val def = ProductCatalog.getProduct(pId)
        if (def != null) def.wholesalePrice * def.itemsPerBox * count else 0.0
    }
    val totalBoxes = wholesaleCart.values.sum()
    val canAfford = (profile?.money ?: 0.0) >= totalCost

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (wholesaleCart.isNotEmpty()) 80.dp else 0.dp)
        ) {
            // Header
            Surface(
                color = Color(0xFF1E293B),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💻", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Wholesale Stock Supply",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Order boxes for storage & restocking",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        if (wholesaleCart.isNotEmpty()) {
                            TextButton(
                                onClick = { viewModel.clearWholesaleCart() },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Clear Cart", color = Color(0xFFEF4444), fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Category Tabs
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF2563EB),
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0xFF334155),
                                    labelColor = Color(0xFFCBD5E1)
                                )
                            )
                        }
                    }
                }
            }

            // Products Catalog List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableProducts) { prod ->
                    val state = statesMap[prod.id]
                    val boxCost = prod.wholesalePrice * prod.itemsPerBox
                    val inCartCount = wholesaleCart[prod.id] ?: 0
                    val backroomStock = state?.backroomStock ?: 0

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(prod.iconEmoji, fontSize = 26.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = prod.name,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Box of ${prod.itemsPerBox} units • $${String.format("%.2f", prod.wholesalePrice)}/ea",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "$${String.format("%.2f", boxCost)} / box",
                                            color = Color(0xFF34D399),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Stockroom: $backroomStock boxes",
                                            color = if (backroomStock > 0) Color(0xFF38BDF8) else Color(0xFFF87171),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            // Stepper Controls
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (inCartCount > 0) {
                                    IconButton(
                                        onClick = { viewModel.removeFromWholesaleCart(prod.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.RemoveCircle,
                                            contentDescription = "Remove",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Text(
                                        text = "$inCartCount",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.addToWholesaleCart(prod.id) },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("add_wholesale_${prod.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddCircle,
                                        contentDescription = "Add Box",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Sticky Checkout Bar
        if (wholesaleCart.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "$totalBoxes Boxes in Order",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Total: $${String.format("%.2f", totalCost)}",
                            color = if (canAfford) Color(0xFF34D399) else Color(0xFFEF4444),
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }

                    Button(
                        onClick = { viewModel.checkoutWholesaleOrder() },
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981),
                            disabledContainerColor = Color(0xFF334155)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("checkout_wholesale_button")
                    ) {
                        Text(
                            text = if (canAfford) "Pay & Deliver 🚚" else "Need More Cash",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
