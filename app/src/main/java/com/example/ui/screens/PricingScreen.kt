package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@Composable
fun PricingScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val productStates by viewModel.productStates.collectAsState()
    val statesMap = remember(productStates) { productStates.associateBy { it.productId } }

    val unlockedProducts = remember(productStates) {
        ProductCatalog.ALL_PRODUCTS.filter { prod ->
            val state = statesMap[prod.id]
            state?.isUnlocked == true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                color = Color(0xFF1E293B),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🏷️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Product Pricing & Profit Margins",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Set retail prices. Overpriced goods make shoppers angry!",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(unlockedProducts) { prod ->
                    val state = statesMap[prod.id] ?: return@items
                    val currentPrice = state.retailPrice
                    val marketPrice = state.marketPrice
                    val wholesale = prod.wholesalePrice
                    val profitPerUnit = currentPrice - wholesale
                    val marginPercent = if (wholesale > 0) ((profitPerUnit / wholesale) * 100).toInt() else 0

                    val priceRatio = if (marketPrice > 0) currentPrice / marketPrice else 1.0

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Product Title Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(prod.iconEmoji, fontSize = 26.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = prod.name,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "Wholesale: $${String.format("%.2f", wholesale)} • Market: $${String.format("%.2f", marketPrice)}",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                // Customer Reaction Status Pill
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when {
                                        priceRatio <= 1.0 -> Color(0xFF065F46)
                                        priceRatio <= 1.15 -> Color(0xFF047857)
                                        priceRatio <= 1.25 -> Color(0xFF92400E)
                                        else -> Color(0xFF7F1D1D)
                                    }
                                ) {
                                    Text(
                                        text = when {
                                            priceRatio <= 1.0 -> "Hot Deal 🔥"
                                            priceRatio <= 1.15 -> "Fair Price 👍"
                                            priceRatio <= 1.25 -> "High Price ⚠️"
                                            else -> "Overpriced 😡"
                                        },
                                        color = when {
                                            priceRatio <= 1.15 -> Color(0xFF6EE7B7)
                                            priceRatio <= 1.25 -> Color(0xFFFDE047)
                                            else -> Color(0xFFFCA5A5)
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Price and Margin display
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("YOUR RETAIL PRICE", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                    Text(
                                        text = "$${String.format("%.2f", currentPrice)}",
                                        color = Color(0xFF38BDF8),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 20.sp
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("PROFIT PER ITEM", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                    Text(
                                        text = "+$${String.format("%.2f", profitPerUnit)} ($marginPercent%)",
                                        color = if (profitPerUnit >= 0) Color(0xFF34D399) else Color(0xFFEF4444),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Price Stepper Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { viewModel.updateRetailPrice(prod.id, currentPrice - 0.50) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.weight(1f).height(32.dp)
                                ) {
                                    Text("-0.50", fontSize = 11.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.updateRetailPrice(prod.id, currentPrice - 0.10) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.weight(1f).height(32.dp)
                                ) {
                                    Text("-0.10", fontSize = 11.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.updateRetailPrice(prod.id, marketPrice) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.weight(1.3f).height(32.dp)
                                ) {
                                    Text("Market", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.updateRetailPrice(prod.id, currentPrice + 0.10) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.weight(1f).height(32.dp).testTag("increase_price_${prod.id}")
                                ) {
                                    Text("+0.10", fontSize = 11.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.updateRetailPrice(prod.id, currentPrice + 0.50) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.weight(1f).height(32.dp)
                                ) {
                                    Text("+0.50", fontSize = 11.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
