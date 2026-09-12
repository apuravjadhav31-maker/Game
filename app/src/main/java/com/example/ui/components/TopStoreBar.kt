package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StoreProfileEntity
import com.example.ui.viewmodel.GameViewModel

@Composable
fun TopStoreBar(
    viewModel: GameViewModel,
    profile: StoreProfileEntity?,
    notification: String?,
    onDismissNotification: () -> Unit
) {
    val storeName = profile?.storeName ?: "Techno Mart"
    val money = profile?.money ?: 0.0
    val level = profile?.storeLevel ?: 1
    val xp = profile?.xp ?: 0
    val nextLevelXp = level * 100
    val xpProgress = (xp.toFloat() / nextLevelXp).coerceIn(0f, 1f)
    val day = profile?.currentDay ?: 1
    val dayMinutes = profile?.dayMinutes ?: 480
    val hours = dayMinutes / 60
    val mins = dayMinutes % 60
    val timeFormatted = String.format("%02d:%02d", hours, mins)
    val isOpen = profile?.isStoreOpen ?: false

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B)
                    )
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Notification banner if any
        AnimatedVisibility(visible = notification != null) {
            notification?.let { msg ->
                Surface(
                    color = Color(0xFF3B82F6).copy(alpha = 0.95f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = msg,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = onDismissNotification,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Top Row: Store Name & Quick Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🛒", fontSize = 18.sp)
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = storeName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF10B981)
                        ) {
                            Text(
                                text = "LVL $level",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    // XP bar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { xpProgress },
                            modifier = Modifier
                                .width(90.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = Color(0xFF10B981),
                            trackColor = Color(0xFF334155),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$xp/$nextLevelXp XP",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Cash Balance Display
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF065F46),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)),
                modifier = Modifier.testTag("cash_balance_display")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💵", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$${String.format("%.2f", money)}",
                        color = Color(0xFF6EE7B7),
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bottom Row: Clock, Day, Open/Close Switch, End Day button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Day and Time info
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF334155).copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DAY $day",
                        color = Color(0xFFFCD34D),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "⏰ $timeFormatted",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Audio & Haptics toggles
                IconButton(
                    onClick = { viewModel.toggleAudio() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (profile?.soundEnabled == true) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Sound toggle",
                        tint = if (profile?.soundEnabled == true) Color(0xFFFCD34D) else Color(0xFF64748B),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // OPEN / CLOSED Neon Toggle
                Button(
                    onClick = { viewModel.toggleStoreOpen() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isOpen) Color(0xFF059669) else Color(0xFFDC2626)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("store_open_toggle")
                ) {
                    Text(
                        text = if (isOpen) "OPEN 🟢" else "CLOSED 🔴",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Close & Finish Day button
                OutlinedButton(
                    onClick = { viewModel.endDay() },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFE2E8F0)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("end_day_button")
                ) {
                    Text(
                        text = "End Day 🌙",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
