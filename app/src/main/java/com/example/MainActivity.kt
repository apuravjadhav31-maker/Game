package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.TopStoreBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.NavigationTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SupermartApp()
            }
        }
    }
}

@Composable
fun SupermartApp(gameViewModel: GameViewModel = viewModel()) {
    val currentTab by gameViewModel.currentTab.collectAsState()
    val profile by gameViewModel.storeProfile.collectAsState()
    val notification by gameViewModel.cashierNotification.collectAsState()
    val customerQueue by gameViewModel.customerQueue.collectAsState()
    val daySummary by gameViewModel.daySummary.collectAsState()

    // Day End Summary Modal
    daySummary?.let { summary ->
        DaySummaryDialog(
            summary = summary,
            onStartNextDay = { gameViewModel.startNextDay() }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF0F172A),
        topBar = {
            TopStoreBar(
                viewModel = gameViewModel,
                profile = profile,
                notification = notification,
                onDismissNotification = { gameViewModel.dismissNotification() }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1E293B),
                contentColor = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .testTag("main_navigation_bar")
            ) {
                // 1. Store Floor
                NavigationBarItem(
                    selected = currentTab == NavigationTab.STORE_FLOOR,
                    onClick = { gameViewModel.setTab(NavigationTab.STORE_FLOOR) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavigationTab.STORE_FLOOR) Icons.Filled.Storefront else Icons.Outlined.Storefront,
                            contentDescription = "Aisles & Floor"
                        )
                    },
                    label = { Text("Aisles", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF38BDF8),
                        selectedTextColor = Color(0xFF38BDF8),
                        indicatorColor = Color(0xFF0C4A6E),
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("tab_floor")
                )

                // 2. Cashier Register (with badge for waiting customers)
                NavigationBarItem(
                    selected = currentTab == NavigationTab.CASHIER_COUNTER,
                    onClick = { gameViewModel.setTab(NavigationTab.CASHIER_COUNTER) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (customerQueue.isNotEmpty()) {
                                    Badge(
                                        containerColor = Color(0xFFEF4444),
                                        contentColor = Color.White
                                    ) {
                                        Text("${customerQueue.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (currentTab == NavigationTab.CASHIER_COUNTER) Icons.Filled.PointOfSale else Icons.Outlined.PointOfSale,
                                contentDescription = "Cashier Register"
                            )
                        }
                    },
                    label = { Text("Register", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF34D399),
                        selectedTextColor = Color(0xFF34D399),
                        indicatorColor = Color(0xFF064E3B),
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("tab_register")
                )

                // 3. Wholesale Supply (Computer)
                NavigationBarItem(
                    selected = currentTab == NavigationTab.ORDER_STOCK,
                    onClick = { gameViewModel.setTab(NavigationTab.ORDER_STOCK) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavigationTab.ORDER_STOCK) Icons.Filled.Inventory else Icons.Outlined.Inventory2,
                            contentDescription = "Wholesale Supply"
                        )
                    },
                    label = { Text("Supply", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFFCD34D),
                        selectedTextColor = Color(0xFFFCD34D),
                        indicatorColor = Color(0xFF78350F),
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("tab_wholesale")
                )

                // 4. Pricing
                NavigationBarItem(
                    selected = currentTab == NavigationTab.PRICING,
                    onClick = { gameViewModel.setTab(NavigationTab.PRICING) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavigationTab.PRICING) Icons.Filled.Sell else Icons.Outlined.Sell,
                            contentDescription = "Price Management"
                        )
                    },
                    label = { Text("Prices", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFA78BFA),
                        selectedTextColor = Color(0xFFA78BFA),
                        indicatorColor = Color(0xFF4C1D95),
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("tab_pricing")
                )

                // 5. Upgrades & Expansion
                NavigationBarItem(
                    selected = currentTab == NavigationTab.UPGRADES,
                    onClick = { gameViewModel.setTab(NavigationTab.UPGRADES) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavigationTab.UPGRADES) Icons.Filled.Construction else Icons.Outlined.Construction,
                            contentDescription = "Upgrades & Licenses"
                        )
                    },
                    label = { Text("Upgrades", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFF472B6),
                        selectedTextColor = Color(0xFFF472B6),
                        indicatorColor = Color(0xFF831843),
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("tab_upgrades")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavigationTab.STORE_FLOOR -> StoreFloorScreen(viewModel = gameViewModel)
                NavigationTab.CASHIER_COUNTER -> CashierRegisterScreen(viewModel = gameViewModel)
                NavigationTab.ORDER_STOCK -> WholesaleMarketScreen(viewModel = gameViewModel)
                NavigationTab.PRICING -> PricingScreen(viewModel = gameViewModel)
                NavigationTab.UPGRADES -> UpgradesScreen(viewModel = gameViewModel)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
