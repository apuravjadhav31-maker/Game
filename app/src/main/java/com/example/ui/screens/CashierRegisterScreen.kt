package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.Customer
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.PaymentMethod

@Composable
fun CashierRegisterScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val customerQueue by viewModel.customerQueue.collectAsState()
    val profile by viewModel.storeProfile.collectAsState()
    val selectedChange by viewModel.selectedChange.collectAsState()
    val cardPinInput by viewModel.cardPinInput.collectAsState()
    val currentCustomer = customerQueue.firstOrNull()

    // Animation for conveyor scan laser
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_offset"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        if (currentCustomer == null) {
            // Empty counter state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.size(90.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🛒", fontSize = 42.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Checkout Counter is Clear",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (profile?.isStoreOpen == true)
                        "Customers are shopping in the aisles and will queue up soon!"
                    else
                        "The store is currently CLOSED. Toggle 'OPEN 🟢' in top bar to begin trading!",
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
                if (profile?.cashierHired == true) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF065F46)
                    ) {
                        Text(
                            text = "🤖 Cashier Assistant Alex is on duty",
                            color = Color(0xFF6EE7B7),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        } else {
            // Active Cashier Register Simulation
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Customer Bar with Queue status
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF3B82F6).copy(alpha = 0.2f),
                                    modifier = Modifier.size(46.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(currentCustomer.avatarEmoji, fontSize = 24.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = currentCustomer.name,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = if (currentCustomer.paymentMethod == PaymentMethod.CARD) "Paying by Credit Card 💳" else "Paying with Cash 💵",
                                        color = if (currentCustomer.paymentMethod == PaymentMethod.CARD) Color(0xFF60A5FA) else Color(0xFF34D399),
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            // Queue badge
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF334155)
                            ) {
                                Text(
                                    text = "Line: ${customerQueue.size} shoppers",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // POS Terminal Green Screen
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF064E3B),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF059669)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TECHNO POS SYSTEM v2.4",
                                    color = Color(0xFF6EE7B7),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "REGISTER #01",
                                    color = Color(0xFF6EE7B7),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    val scannedCount = currentCustomer.basket.count { it.isScanned }
                                    val totalCount = currentCustomer.basket.size
                                    Text(
                                        text = "Scanned: $scannedCount / $totalCount items",
                                        color = Color(0xFFA7F3D0),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    val currentTotal = currentCustomer.basket.filter { it.isScanned }.sumOf { it.retailPrice }
                                    Text(
                                        text = "SUBTOTAL: $${String.format("%.2f", currentTotal)}",
                                        color = Color(0xFFA7F3D0),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Text(
                                    text = "$${String.format("%.2f", currentCustomer.totalAmount)}",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                // Conveyor Belt: Items to scan
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Conveyor Belt (Tap item to scan)",
                                color = Color(0xFFCBD5E1),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            if (!currentCustomer.areAllItemsScanned) {
                                TextButton(
                                    onClick = { viewModel.scanAllRemaining() },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Scan All (Beep) ⚡", fontSize = 11.sp, color = Color(0xFF38BDF8))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        // Items list on conveyor belt
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1E293B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                currentCustomer.basket.forEachIndexed { index, item ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (item.isScanned) Color(0xFF064E3B).copy(alpha = 0.4f) else Color(0xFF0F172A),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (item.isScanned) Color(0xFF10B981) else Color(0xFF475569)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clickable(enabled = !item.isScanned) {
                                                viewModel.scanItem(index)
                                            }
                                            .testTag("scan_item_$index")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(item.product.iconEmoji, fontSize = 24.sp)
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = item.product.name,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 13.sp
                                                    )
                                                    Text(
                                                        text = "$${String.format("%.2f", item.retailPrice)}",
                                                        color = Color(0xFF34D399),
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }

                                            if (item.isScanned) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = "Scanned",
                                                        tint = Color(0xFF10B981),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Scanned", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            } else {
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = Color(0xFFEF4444)
                                                ) {
                                                    Text(
                                                        text = "TAP SCAN 🔴",
                                                        color = Color.White,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Payment Section (Unlocks when all items scanned)
                item {
                    if (!currentCustomer.areAllItemsScanned) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1E293B).copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🔒", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Scan all grocery items above to proceed with payment!",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    } else if (currentCustomer.paymentMethod == PaymentMethod.CASH) {
                        // CASH PAYMENT MODE: Cash Drawer
                        val totalDue = currentCustomer.totalAmount
                        val cashGiven = currentCustomer.cashGiven
                        val expectedChange = (cashGiven - totalDue).coerceAtLeast(0.0)

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E293B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Customer Cash Given:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                        Text(
                                            text = "$${String.format("%.2f", cashGiven)} 💵",
                                            color = Color(0xFFFCD34D),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Change Needed:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                        Text(
                                            text = "$${String.format("%.2f", expectedChange)}",
                                            color = Color(0xFF34D399),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 18.sp
                                        )
                                    }
                                }

                                Divider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 10.dp))

                                // Current Change selected
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Your Selected Change: $${String.format("%.2f", selectedChange)}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    TextButton(
                                        onClick = { viewModel.clearCashChange() },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("Clear", color = Color(0xFFEF4444), fontSize = 12.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Cash Bills ($50, $20, $10, $5, $1)
                                Text("Bills:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(50.0, 20.0, 10.0, 5.0, 1.0).forEach { bill ->
                                        Button(
                                            onClick = { viewModel.addCashChange(bill) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(vertical = 6.dp),
                                            modifier = Modifier.weight(1f).height(36.dp)
                                        ) {
                                            Text("$$${bill.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Coins ($0.50, $0.25, $0.10, $0.05, $0.01)
                                Text("Coins:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(0.50, 0.25, 0.10, 0.05, 0.01).forEach { coin ->
                                        OutlinedButton(
                                            onClick = { viewModel.addCashChange(coin) },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFCD34D)),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(vertical = 4.dp),
                                            modifier = Modifier.weight(1f).height(34.dp)
                                        ) {
                                            Text("${(coin * 100).toInt()}¢", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Submit Change Button
                                Button(
                                    onClick = { viewModel.submitCashPayment() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .testTag("hand_change_button")
                                ) {
                                    Text(
                                        text = "Hand Change & Complete Transaction 🛎️",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    } else {
                        // CREDIT CARD POS TERMINAL MODE
                        val totalDue = currentCustomer.totalAmount

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E293B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("💳", fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Card Payment Terminal",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Text(
                                        text = "DUE: $${String.format("%.2f", totalDue)}",
                                        color = Color(0xFF60A5FA),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Terminal Display Screen
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF0F172A),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "ENTER AMOUNT / PIN",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = if (cardPinInput.isEmpty()) "$ 0.00" else "$ $cardPinInput",
                                            color = Color(0xFF38BDF8),
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Numeric Keypad
                                val rows = listOf(
                                    listOf("1", "2", "3"),
                                    listOf("4", "5", "6"),
                                    listOf("7", "8", "9"),
                                    listOf(".", "0", "⌫")
                                )

                                Column(
                                    modifier = Modifier.widthIn(max = 260.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    rows.forEach { row ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            row.forEach { key ->
                                                Button(
                                                    onClick = {
                                                        when (key) {
                                                            "⌫" -> viewModel.backspaceCardPin()
                                                            else -> viewModel.appendCardDigit(key)
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                                    shape = RoundedCornerShape(6.dp),
                                                    contentPadding = PaddingValues(0.dp),
                                                    modifier = Modifier.weight(1f).height(38.dp)
                                                ) {
                                                    Text(key, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Approve / Enter Button
                                Button(
                                    onClick = { viewModel.submitCardPayment() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .testTag("submit_card_payment_button")
                                ) {
                                    Text(
                                        text = "APPROVE CARD PAYMENT 💳",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
