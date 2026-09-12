package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import com.example.ui.viewmodel.GameViewModel

@Composable
fun UpgradesScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.storeProfile.collectAsState()
    val money = profile?.money ?: 0.0
    val currentLicense = profile?.unlockedLicenses ?: 1
    val currentSection = profile?.storeExpansionLevel ?: 1
    val cashierHired = profile?.cashierHired ?: false
    val restockerHired = profile?.restockerHired ?: false

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🏗️", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Store Expansions & Licenses",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Grow your supermarket empire from corner store to hypermarket!",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Section 1: Staff Hiring
            item {
                Text(
                    text = "Staff & Automation",
                    color = Color(0xFFCBD5E1),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            // Cashier Staff Card
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (cashierHired) Color(0xFF10B981) else Color(0xFF334155)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF3B82F6).copy(alpha = 0.2f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🤖", fontSize = 22.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Cashier Assistant (Alex)",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    if (cashierHired) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF065F46)
                                        ) {
                                            Text(
                                                text = "ACTIVE",
                                                color = Color(0xFF34D399),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "Automates checkout register & change ($30/day wage)",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (!cashierHired) {
                            Button(
                                onClick = { viewModel.hireStaff("CASHIER", 150.0) },
                                enabled = money >= 150.0,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("hire_cashier_button")
                            ) {
                                Text("Hire $150", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            OutlinedButton(
                                onClick = { viewModel.fireStaff("CASHIER") },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Dismiss", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Restocker Staff Card
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (restockerHired) Color(0xFF10B981) else Color(0xFF334155)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("👷‍♂️", fontSize = 22.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Shelf Restocker (Sam)",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    if (restockerHired) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF065F46)
                                        ) {
                                            Text(
                                                text = "ACTIVE",
                                                color = Color(0xFF34D399),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "Refills empty shelves from backroom boxes ($25/day wage)",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (!restockerHired) {
                            Button(
                                onClick = { viewModel.hireStaff("RESTOCKER", 120.0) },
                                enabled = money >= 120.0,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("hire_restocker_button")
                            ) {
                                Text("Hire $120", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            OutlinedButton(
                                onClick = { viewModel.fireStaff("RESTOCKER") },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Dismiss", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Section 2: Store Expansions
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Store Floor Expansions",
                    color = Color(0xFFCBD5E1),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            val storeExpansions = listOf(
                Triple(2, "Section 2: Coolers & Refrigerators", 350.0),
                Triple(3, "Section 3: Frozen Pantry Wing", 800.0),
                Triple(4, "Section 4: Mega Hypermarket Wing", 1500.0)
            )

            items(storeExpansions.size) { index ->
                val (secLevel, secName, secCost) = storeExpansions[index]
                val isUnlocked = currentSection >= secLevel
                val canUnlock = currentSection == secLevel - 1

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isUnlocked) Color(0xFF10B981) else Color(0xFF334155)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = secName,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                if (isUnlocked) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("✓ Built", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                text = "Adds new shelf racks & refrigerator display bays",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }

                        if (!isUnlocked) {
                            Button(
                                onClick = { viewModel.expandStoreSection(secLevel, secCost) },
                                enabled = canUnlock && money >= secCost,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("expand_section_$secLevel")
                            ) {
                                Text("Expand $$${secCost.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Section 3: Product Licenses
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Product Wholesale Licenses",
                    color = Color(0xFFCBD5E1),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            val licenses = listOf(
                Triple(2, "License 2: Dairy, Milk, Eggs & Cheese", 200.0),
                Triple(3, "License 3: Cereal, Coffee & Honey", 450.0),
                Triple(4, "License 4: Cold Sodas & Energy Drinks", 750.0),
                Triple(5, "License 5: Frozen Pizza & Ice Cream", 1200.0),
                Triple(6, "License 6: Cleaning & Snack Munchies", 1800.0)
            )

            items(licenses.size) { index ->
                val (licLevel, licName, licCost) = licenses[index]
                val isUnlocked = currentLicense >= licLevel
                val canUnlock = currentLicense == licLevel - 1

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isUnlocked) Color(0xFF10B981) else Color(0xFF334155)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = licName,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                if (isUnlocked) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("✓ Acquired", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                text = "Unlocks brand new high-margin goods in Wholesale PC",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }

                        if (!isUnlocked) {
                            Button(
                                onClick = { viewModel.unlockLicense(licLevel, licCost) },
                                enabled = canUnlock && money >= licCost,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("unlock_license_$licLevel")
                            ) {
                                Text("Buy $$${licCost.toInt()}", color = Color(0xFF0F172A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
