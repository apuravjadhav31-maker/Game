package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.viewmodel.DaySummary

@Composable
fun DaySummaryDialog(
    summary: DaySummary,
    onStartNextDay: () -> Unit
) {
    Dialog(onDismissRequest = { /* Must click start next day */ }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E293B),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🌙 DAY ${summary.dayNumber} COMPLETED",
                    color = Color(0xFFFCD34D),
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Star rating
                val stars = when {
                    summary.satisfactionScore >= 90 -> "⭐⭐⭐⭐⭐"
                    summary.satisfactionScore >= 75 -> "⭐⭐⭐⭐"
                    summary.satisfactionScore >= 50 -> "⭐⭐⭐"
                    else -> "⭐⭐"
                }
                Text(stars, fontSize = 22.sp)
                Text(
                    text = "Shopper Satisfaction: ${summary.satisfactionScore}%",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Financial Breakdown Table
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        FinancialRow("Customers Served", "${summary.customersServed} shoppers", Color.White)
                        FinancialRow("Total Revenue", "+$${String.format("%.2f", summary.revenue)}", Color(0xFF34D399))
                        FinancialRow("Wholesale Stock Cost", "-$${String.format("%.2f", summary.wholesaleCost)}", Color(0xFFF87171))
                        FinancialRow("Store Rent & Electricity", "-$${String.format("%.2f", summary.rentCost)}", Color(0xFFF87171))
                        FinancialRow("Staff Wages", "-$${String.format("%.2f", summary.staffCost)}", Color(0xFFF87171))

                        Divider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "NET PROFIT / LOSS",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${if (summary.netProfit >= 0) "+" else ""}$${String.format("%.2f", summary.netProfit)}",
                                color = if (summary.netProfit >= 0) Color(0xFF10B981) else Color(0xFFEF4444),
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "+${summary.xpEarned} XP Earned Today! 🌟",
                    color = Color(0xFF60A5FA),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onStartNextDay,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("start_next_day_button")
                ) {
                    Text(
                        text = "START NEXT DAY ☀️",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FinancialRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFF94A3B8), fontSize = 12.sp)
        Text(value, color = valueColor, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
    }
}
