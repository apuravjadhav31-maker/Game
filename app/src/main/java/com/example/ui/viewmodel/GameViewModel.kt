package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundEffects
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.roundToInt
import kotlin.random.Random

data class CustomerBasketItem(
    val product: ProductDef,
    val retailPrice: Double,
    val isScanned: Boolean = false
)

enum class PaymentMethod { CASH, CARD }

data class Customer(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val avatarEmoji: String,
    val basket: List<CustomerBasketItem>,
    val paymentMethod: PaymentMethod,
    val cashGiven: Double,
    var dialogBubble: String? = null,
    var isAngry: Boolean = false
) {
    val totalAmount: Double
        get() = basket.sumOf { it.retailPrice }

    val areAllItemsScanned: Boolean
        get() = basket.isNotEmpty() && basket.all { it.isScanned }
}

enum class NavigationTab {
    STORE_FLOOR,
    CASHIER_COUNTER,
    ORDER_STOCK,
    PRICING,
    UPGRADES
}

data class DaySummary(
    val dayNumber: Int,
    val customersServed: Int,
    val revenue: Double,
    val wholesaleCost: Double,
    val rentCost: Double,
    val staffCost: Double,
    val netProfit: Double,
    val satisfactionScore: Int,
    val xpEarned: Int
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = SupermartDatabase.getInstance(application)
    private val dao = db.supermartDao()
    val soundEffects = SoundEffects(application)

    // Reactive database states
    val storeProfile: StateFlow<StoreProfileEntity?> = dao.getStoreProfile()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val productStates: StateFlow<List<ProductStateEntity>> = dao.getAllProductStates()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val shelfSlots: StateFlow<List<ShelfSlotEntity>> = dao.getAllShelfSlots()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val dailyRecords: StateFlow<List<DailyRecordEntity>> = dao.getDailyRecords()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // In-game dynamic state
    private val _currentTab = MutableStateFlow(NavigationTab.STORE_FLOOR)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    // Customer queue at register
    private val _customerQueue = MutableStateFlow<List<Customer>>(emptyList())
    val customerQueue: StateFlow<List<Customer>> = _customerQueue.asStateFlow()

    // Browsing customers in store aisles
    private val _browsingCustomers = MutableStateFlow<List<String>>(emptyList())
    val browsingCustomers: StateFlow<List<String>> = _browsingCustomers.asStateFlow()

    // Cashier UI State
    private val _selectedChange = MutableStateFlow(0.0)
    val selectedChange: StateFlow<Double> = _selectedChange.asStateFlow()

    private val _cardPinInput = MutableStateFlow("")
    val cardPinInput: StateFlow<String> = _cardPinInput.asStateFlow()

    private val _cashierNotification = MutableStateFlow<String?>(null)
    val cashierNotification: StateFlow<String?> = _cashierNotification.asStateFlow()

    // Wholesale Cart (productId -> box count)
    private val _wholesaleCart = MutableStateFlow<Map<String, Int>>(emptyMap())
    val wholesaleCart: StateFlow<Map<String, Int>> = _wholesaleCart.asStateFlow()

    // Daily Summary Dialog
    private val _daySummary = MutableStateFlow<DaySummary?>(null)
    val daySummary: StateFlow<DaySummary?> = _daySummary.asStateFlow()

    // Daily trackers
    private var dailyRevenue = 0.0
    private var dailyWholesaleExpense = 0.0
    private var dailyCustomersServed = 0
    private var dailyPositiveReactions = 0
    private var dailyTotalReactions = 0

    // Background game loops
    private var gameLoopJob: Job? = null
    private var cashierAutoJob: Job? = null

    init {
        initializeDatabaseDefaults()
        startGameLoop()
    }

    private fun initializeDatabaseDefaults() {
        viewModelScope.launch {
            // Check if profile exists
            val existing = dao.getStoreProfile().firstOrNull()
            if (existing == null) {
                // First launch
                val defaultProfile = StoreProfileEntity(
                    id = 1,
                    storeName = "Techno Mart",
                    money = 250.00,
                    storeLevel = 1,
                    xp = 0,
                    currentDay = 1,
                    dayMinutes = 480, // 8:00 AM
                    isStoreOpen = false,
                    storeExpansionLevel = 1,
                    unlockedLicenses = 1,
                    cashierHired = false,
                    restockerHired = false
                )
                dao.saveStoreProfile(defaultProfile)

                // Initialize products
                val initialProductStates = ProductCatalog.ALL_PRODUCTS.map { p ->
                    ProductStateEntity(
                        productId = p.id,
                        retailPrice = p.baseMarketPrice,
                        marketPrice = p.baseMarketPrice,
                        backroomStock = if (p.licenseRequired == 1) 4 else 0,
                        isUnlocked = p.licenseRequired == 1
                    )
                }
                dao.saveProductStates(initialProductStates)

                // Initialize shelves for Section 1 (8 initial shelf slots)
                val initialShelves = listOf(
                    ShelfSlotEntity(1, 1, "SHELF", "sliced_bread", 8, 12, true),
                    ShelfSlotEntity(2, 1, "SHELF", "flour_bag", 6, 12, true),
                    ShelfSlotEntity(3, 1, "SHELF", "white_sugar", 6, 12, true),
                    ShelfSlotEntity(4, 1, "SHELF", "pasta_spaghetti", 8, 12, true),
                    ShelfSlotEntity(5, 1, "SHELF", null, 0, 12, true),
                    ShelfSlotEntity(6, 1, "SHELF", null, 0, 12, true),
                    ShelfSlotEntity(7, 1, "FRIDGE", null, 0, 10, true),
                    ShelfSlotEntity(8, 1, "FRIDGE", null, 0, 10, true),
                    // Section 2 Shelves (Unlocked with Section 2)
                    ShelfSlotEntity(9, 2, "SHELF", null, 0, 12, false),
                    ShelfSlotEntity(10, 2, "SHELF", null, 0, 12, false),
                    ShelfSlotEntity(11, 2, "FRIDGE", null, 0, 10, false),
                    ShelfSlotEntity(12, 2, "FREEZER", null, 0, 8, false),
                    // Section 3 & 4 Shelves
                    ShelfSlotEntity(13, 3, "SHELF", null, 0, 14, false),
                    ShelfSlotEntity(14, 3, "FREEZER", null, 0, 10, false),
                    ShelfSlotEntity(15, 4, "SHELF", null, 0, 16, false),
                    ShelfSlotEntity(16, 4, "FRIDGE", null, 0, 12, false)
                )
                dao.saveShelfSlots(initialShelves)
            }
        }
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            while (true) {
                delay(2000) // 2 seconds tick
                val profile = storeProfile.value ?: continue

                if (profile.isStoreOpen) {
                    // Advance clock by 5 game minutes every 2 real seconds
                    val newMinutes = profile.dayMinutes + 5
                    if (newMinutes >= 1260) { // 21:00 (9:00 PM)
                        // Auto end day
                        endDay()
                    } else {
                        dao.saveStoreProfile(profile.copy(dayMinutes = newMinutes))

                        // Customer shopping generation
                        simulateCustomerShopping()

                        // Restocker staff automation
                        if (profile.restockerHired) {
                            runRestockerAutomation()
                        }

                        // Cashier staff automation
                        if (profile.cashierHired) {
                            runCashierAutomation()
                        }
                    }
                }
            }
        }
    }

    private fun simulateCustomerShopping() {
        val activeQueue = _customerQueue.value
        if (activeQueue.size >= 4) return // Register line full

        val shelves = shelfSlots.value.filter { it.isUnlocked && it.assignedProductId != null && it.currentStock > 0 }
        if (shelves.isEmpty()) return

        // Chance to spawn customer
        if (Random.nextDouble() < 0.65) {
            val names = listOf("Rohan", "Anjali", "Vikram", "Pooja", "Arjun", "Kavita", "Sameer", "Neha", "Kabir", "Simran", "Aman")
            val avatars = listOf("👨‍💼", "👩‍🦰", "🧑‍🦱", "👩‍⚕️", "👨‍🎓", "👩‍🍳", "🧔‍♂️", "👱‍♀️", "🧕", "👨‍🎨")
            val customerName = names.random()
            val customerAvatar = avatars.random()

            // Pick 1 to 3 items
            val itemCount = Random.nextInt(1, 4)
            val basketItems = mutableListOf<CustomerBasketItem>()

            val states = productStates.value.associateBy { it.productId }
            val updatedShelves = shelves.toMutableList()

            for (i in 0 until itemCount) {
                if (updatedShelves.isEmpty()) break
                val shelf = updatedShelves.random()
                val prodState = states[shelf.assignedProductId] ?: continue
                val prodDef = ProductCatalog.getProduct(shelf.assignedProductId ?: "") ?: continue

                // Check price tolerance
                dailyTotalReactions++
                if (prodState.retailPrice > prodState.marketPrice * 1.25) {
                    // Too expensive! Customer leaves without item
                    continue
                }

                dailyPositiveReactions++
                basketItems.add(CustomerBasketItem(product = prodDef, retailPrice = prodState.retailPrice))

                // Deduct from shelf stock
                val newStock = (shelf.currentStock - 1).coerceAtLeast(0)
                viewModelScope.launch {
                    dao.saveShelfSlot(shelf.copy(currentStock = newStock))
                }
            }

            if (basketItems.isNotEmpty()) {
                val total = basketItems.sumOf { it.retailPrice }
                val isCard = Random.nextBoolean()
                val cashGiven = if (isCard) 0.0 else {
                    // Pick a realistic bill (e.g. $10, $20, $50, $100)
                    when {
                        total <= 5.0 -> if (Random.nextBoolean()) 5.0 else 10.0
                        total <= 10.0 -> if (Random.nextBoolean()) 10.0 else 20.0
                        total <= 20.0 -> if (Random.nextBoolean()) 20.0 else 50.0
                        total <= 50.0 -> if (Random.nextBoolean()) 50.0 else 100.0
                        else -> 100.0
                    }
                }

                val customer = Customer(
                    name = customerName,
                    avatarEmoji = customerAvatar,
                    basket = basketItems,
                    paymentMethod = if (isCard) PaymentMethod.CARD else PaymentMethod.CASH,
                    cashGiven = cashGiven,
                    dialogBubble = if (isCard) "Paying with Card! 💳" else "Here is $${String.format("%.2f", cashGiven)} 💵"
                )

                _customerQueue.update { it + customer }
                _browsingCustomers.update { (it + customerName).takeLast(4) }
            }
        }
    }

    private fun runRestockerAutomation() {
        val shelves = shelfSlots.value.filter { it.isUnlocked && it.assignedProductId != null }
        val states = productStates.value.associateBy { it.productId }

        for (shelf in shelves) {
            val prodId = shelf.assignedProductId ?: continue
            val state = states[prodId] ?: continue
            val prodDef = ProductCatalog.getProduct(prodId) ?: continue

            // If shelf needs items and backroom has boxes
            if (shelf.currentStock < shelf.maxCapacity && state.backroomStock > 0) {
                val restockAmount = (shelf.maxCapacity - shelf.currentStock).coerceAtMost(prodDef.itemsPerBox)
                viewModelScope.launch {
                    dao.saveShelfSlot(shelf.copy(currentStock = shelf.currentStock + restockAmount))
                    dao.saveProductState(state.copy(backroomStock = (state.backroomStock - 1).coerceAtLeast(0)))
                }
                break // restock 1 box per cycle
            }
        }
    }

    private fun runCashierAutomation() {
        val queue = _customerQueue.value
        if (queue.isEmpty()) return

        val currentCustomer = queue.first()
        // If not all scanned, scan next item
        val unscannedIndex = currentCustomer.basket.indexOfFirst { !it.isScanned }
        if (unscannedIndex != -1) {
            val updatedBasket = currentCustomer.basket.toMutableList()
            updatedBasket[unscannedIndex] = updatedBasket[unscannedIndex].copy(isScanned = true)
            _customerQueue.update { listOf(currentCustomer.copy(basket = updatedBasket)) + it.drop(1) }
        } else {
            // Completed! Finish customer automatically
            completeCurrentCustomer(isAutomated = true)
        }
    }

    fun setTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun toggleStoreOpen() {
        val profile = storeProfile.value ?: return
        val newStatus = !profile.isStoreOpen
        viewModelScope.launch {
            dao.saveStoreProfile(profile.copy(isStoreOpen = newStatus))
        }
        if (newStatus) {
            soundEffects.playSuccess(profile.soundEnabled, profile.hapticsEnabled)
        }
    }

    // Cashier Actions
    fun scanItem(index: Int) {
        val queue = _customerQueue.value
        if (queue.isEmpty()) return
        val customer = queue.first()
        if (index in customer.basket.indices && !customer.basket[index].isScanned) {
            val updated = customer.basket.toMutableList()
            updated[index] = updated[index].copy(isScanned = true)
            _customerQueue.update { listOf(customer.copy(basket = updated)) + it.drop(1) }

            val profile = storeProfile.value
            soundEffects.playBarcodeBeep(profile?.soundEnabled ?: true, profile?.hapticsEnabled ?: true)
        }
    }

    fun scanAllRemaining() {
        val queue = _customerQueue.value
        if (queue.isEmpty()) return
        val customer = queue.first()
        val updated = customer.basket.map { it.copy(isScanned = true) }
        _customerQueue.update { listOf(customer.copy(basket = updated)) + it.drop(1) }
        val profile = storeProfile.value
        soundEffects.playBarcodeBeep(profile?.soundEnabled ?: true, profile?.hapticsEnabled ?: true)
    }

    // Cash Change Handling
    fun addCashChange(amount: Double) {
        val rounded = ((_selectedChange.value + amount) * 100.0).roundToInt() / 100.0
        _selectedChange.value = rounded
        val profile = storeProfile.value
        soundEffects.playCashDing(profile?.soundEnabled ?: true, profile?.hapticsEnabled ?: true)
    }

    fun clearCashChange() {
        _selectedChange.value = 0.0
    }

    fun submitCashPayment() {
        val queue = _customerQueue.value
        if (queue.isEmpty()) return
        val customer = queue.first()
        val expectedChange = ((customer.cashGiven - customer.totalAmount) * 100.0).roundToInt() / 100.0
        val givenChange = ((_selectedChange.value) * 100.0).roundToInt() / 100.0

        val diff = kotlin.math.abs(expectedChange - givenChange)
        val profile = storeProfile.value

        if (diff <= 0.05) { // within 5 cents tolerance
            _cashierNotification.value = "Exact Change Given! +$${String.format("%.2f", customer.totalAmount)}"
            completeCurrentCustomer(isAutomated = false)
        } else {
            soundEffects.playError(profile?.soundEnabled ?: true, profile?.hapticsEnabled ?: true)
            _cashierNotification.value = if (givenChange < expectedChange) {
                "Not enough change! Customer needs $${String.format("%.2f", expectedChange)}"
            } else {
                "Too much change! Correct change is $${String.format("%.2f", expectedChange)}"
            }
        }
    }

    // Card PIN handling
    fun appendCardDigit(digit: String) {
        if (_cardPinInput.value.length < 8) {
            _cardPinInput.value += digit
            val profile = storeProfile.value
            soundEffects.playCardKeyBeep(profile?.soundEnabled ?: true, profile?.hapticsEnabled ?: true)
        }
    }

    fun backspaceCardPin() {
        if (_cardPinInput.value.isNotEmpty()) {
            _cardPinInput.value = _cardPinInput.value.dropLast(1)
        }
    }

    fun clearCardPin() {
        _cardPinInput.value = ""
    }

    fun submitCardPayment() {
        val queue = _customerQueue.value
        if (queue.isEmpty()) return
        val customer = queue.first()
        val profile = storeProfile.value

        val enteredVal = _cardPinInput.value.toDoubleOrNull() ?: 0.0
        val expectedVal = ((customer.totalAmount) * 100.0).roundToInt() / 100.0

        if (kotlin.math.abs(enteredVal - expectedVal) <= 0.05 || _cardPinInput.value.length >= 4) {
            _cashierNotification.value = "Payment Approved! 💳 +$${String.format("%.2f", customer.totalAmount)}"
            clearCardPin()
            completeCurrentCustomer(isAutomated = false)
        } else {
            soundEffects.playError(profile?.soundEnabled ?: true, profile?.hapticsEnabled ?: true)
            _cashierNotification.value = "Card Declined! Total is $${String.format("%.2f", customer.totalAmount)}"
        }
    }

    private fun completeCurrentCustomer(isAutomated: Boolean) {
        val queue = _customerQueue.value
        if (queue.isEmpty()) return
        val customer = queue.first()

        dailyRevenue += customer.totalAmount
        dailyCustomersServed++

        val profile = storeProfile.value ?: return
        val newMoney = ((profile.money + customer.totalAmount) * 100.0).roundToInt() / 100.0
        val xpBonus = (customer.totalAmount * 2).toInt() + 10
        var newXp = profile.xp + xpBonus
        var newLevel = profile.storeLevel

        // Level up formula
        val requiredXp = newLevel * 100
        if (newXp >= requiredXp) {
            newXp -= requiredXp
            newLevel++
            soundEffects.playSuccess(profile.soundEnabled, profile.hapticsEnabled)
            _cashierNotification.value = "🎉 STORE LEVEL UP! Now Level $newLevel! Unlocked new upgrades!"
        } else if (!isAutomated) {
            soundEffects.playCashDing(profile.soundEnabled, profile.hapticsEnabled)
        }

        viewModelScope.launch {
            dao.saveStoreProfile(
                profile.copy(
                    money = newMoney,
                    xp = newXp,
                    storeLevel = newLevel,
                    totalRevenue = profile.totalRevenue + customer.totalAmount,
                    totalCustomersServed = profile.totalCustomersServed + 1
                )
            )
        }

        _selectedChange.value = 0.0
        _cardPinInput.value = ""
        _customerQueue.update { it.drop(1) }
    }

    // Shelf Stocking
    fun restockShelf(slotId: Int) {
        val shelves = shelfSlots.value
        val shelf = shelves.find { it.slotId == slotId } ?: return
        val prodId = shelf.assignedProductId ?: return
        val state = productStates.value.find { it.productId == prodId } ?: return
        val prodDef = ProductCatalog.getProduct(prodId) ?: return

        if (state.backroomStock <= 0) {
            _cashierNotification.value = "No boxes of ${prodDef.name} in backroom! Order from Wholesale."
            return
        }

        val spaceAvailable = shelf.maxCapacity - shelf.currentStock
        if (spaceAvailable <= 0) {
            _cashierNotification.value = "Shelf is already full!"
            return
        }

        val addCount = spaceAvailable.coerceAtMost(prodDef.itemsPerBox)
        viewModelScope.launch {
            dao.saveShelfSlot(shelf.copy(currentStock = shelf.currentStock + addCount))
            dao.saveProductState(state.copy(backroomStock = (state.backroomStock - 1).coerceAtLeast(0)))
        }

        val profile = storeProfile.value
        soundEffects.playBarcodeBeep(profile?.soundEnabled ?: true, profile?.hapticsEnabled ?: true)
    }

    fun assignProductToShelf(slotId: Int, productId: String) {
        val shelf = shelfSlots.value.find { it.slotId == slotId } ?: return
        viewModelScope.launch {
            dao.saveShelfSlot(shelf.copy(assignedProductId = productId, currentStock = 0))
        }
    }

    // Pricing
    fun updateRetailPrice(productId: String, newPrice: Double) {
        val rounded = ((newPrice * 100.0).roundToInt()) / 100.0
        if (rounded < 0.10) return
        val state = productStates.value.find { it.productId == productId } ?: return
        viewModelScope.launch {
            dao.saveProductState(state.copy(retailPrice = rounded))
        }
    }

    // Wholesale Ordering
    fun addToWholesaleCart(productId: String) {
        _wholesaleCart.update { current ->
            val count = current[productId] ?: 0
            current + (productId to count + 1)
        }
    }

    fun removeFromWholesaleCart(productId: String) {
        _wholesaleCart.update { current ->
            val count = current[productId] ?: 0
            if (count <= 1) current - productId else current + (productId to count - 1)
        }
    }

    fun clearWholesaleCart() {
        _wholesaleCart.value = emptyMap()
    }

    fun checkoutWholesaleOrder() {
        val cart = _wholesaleCart.value
        if (cart.isEmpty()) return

        var totalCost = 0.0
        for ((pId, boxes) in cart) {
            val def = ProductCatalog.getProduct(pId) ?: continue
            totalCost += def.wholesalePrice * def.itemsPerBox * boxes
        }

        val profile = storeProfile.value ?: return
        if (profile.money < totalCost) {
            soundEffects.playError(profile.soundEnabled, profile.hapticsEnabled)
            _cashierNotification.value = "Not enough cash! Need $${String.format("%.2f", totalCost)}"
            return
        }

        dailyWholesaleExpense += totalCost
        val newMoney = ((profile.money - totalCost) * 100.0).roundToInt() / 100.0

        viewModelScope.launch {
            dao.saveStoreProfile(profile.copy(money = newMoney))

            // Add stock to backroom
            val currentStates = productStates.value.associateBy { it.productId }.toMutableMap()
            for ((pId, boxes) in cart) {
                val existing = currentStates[pId]
                if (existing != null) {
                    val updated = existing.copy(backroomStock = existing.backroomStock + boxes)
                    currentStates[pId] = updated
                    dao.saveProductState(updated)
                }
            }
        }

        clearWholesaleCart()
        soundEffects.playSuccess(profile.soundEnabled, profile.hapticsEnabled)
        _cashierNotification.value = "Delivery Arrived! $${String.format("%.2f", totalCost)} stock added to backroom 📦"
    }

    // Upgrades
    fun unlockLicense(licenseLevel: Int, cost: Double) {
        val profile = storeProfile.value ?: return
        if (profile.money < cost) {
            soundEffects.playError(profile.soundEnabled, profile.hapticsEnabled)
            _cashierNotification.value = "Need $${cost.toInt()} for License $licenseLevel!"
            return
        }

        viewModelScope.launch {
            dao.saveStoreProfile(profile.copy(
                money = profile.money - cost,
                unlockedLicenses = licenseLevel
            ))

            // Unlock corresponding products
            val states = productStates.value
            for (state in states) {
                val def = ProductCatalog.getProduct(state.productId) ?: continue
                if (def.licenseRequired <= licenseLevel && !state.isUnlocked) {
                    dao.saveProductState(state.copy(isUnlocked = true))
                }
            }
        }
        soundEffects.playSuccess(profile.soundEnabled, profile.hapticsEnabled)
        _cashierNotification.value = "Unlocked Product License $licenseLevel! 🎉"
    }

    fun expandStoreSection(sectionLevel: Int, cost: Double) {
        val profile = storeProfile.value ?: return
        if (profile.money < cost) {
            soundEffects.playError(profile.soundEnabled, profile.hapticsEnabled)
            _cashierNotification.value = "Need $${cost.toInt()} for Store Expansion $sectionLevel!"
            return
        }

        viewModelScope.launch {
            dao.saveStoreProfile(profile.copy(
                money = profile.money - cost,
                storeExpansionLevel = sectionLevel
            ))

            // Unlock section shelf slots
            val shelves = shelfSlots.value
            for (shelf in shelves) {
                if (shelf.sectionNumber <= sectionLevel && !shelf.isUnlocked) {
                    dao.saveShelfSlot(shelf.copy(isUnlocked = true))
                }
            }
        }
        soundEffects.playSuccess(profile.soundEnabled, profile.hapticsEnabled)
        _cashierNotification.value = "Store Expanded to Section $sectionLevel! More aisles & shelves! 🏬"
    }

    fun hireStaff(role: String, cost: Double) {
        val profile = storeProfile.value ?: return
        if (profile.money < cost) {
            soundEffects.playError(profile.soundEnabled, profile.hapticsEnabled)
            _cashierNotification.value = "Need $${cost.toInt()} to hire $role!"
            return
        }

        viewModelScope.launch {
            val updated = when (role) {
                "CASHIER" -> profile.copy(cashierHired = true, money = profile.money - cost)
                "RESTOCKER" -> profile.copy(restockerHired = true, money = profile.money - cost)
                else -> profile
            }
            dao.saveStoreProfile(updated)
        }
        soundEffects.playSuccess(profile.soundEnabled, profile.hapticsEnabled)
        _cashierNotification.value = "Hired $role! They will now assist your store operations."
    }

    fun fireStaff(role: String) {
        val profile = storeProfile.value ?: return
        viewModelScope.launch {
            val updated = when (role) {
                "CASHIER" -> profile.copy(cashierHired = false)
                "RESTOCKER" -> profile.copy(restockerHired = false)
                else -> profile
            }
            dao.saveStoreProfile(updated)
        }
    }

    fun toggleAudio() {
        val profile = storeProfile.value ?: return
        viewModelScope.launch {
            dao.saveStoreProfile(profile.copy(soundEnabled = !profile.soundEnabled))
        }
    }

    fun toggleHaptics() {
        val profile = storeProfile.value ?: return
        viewModelScope.launch {
            dao.saveStoreProfile(profile.copy(hapticsEnabled = !profile.hapticsEnabled))
        }
    }

    fun dismissNotification() {
        _cashierNotification.value = null
    }

    // End Day & Summary
    fun endDay() {
        val profile = storeProfile.value ?: return
        val rentCost = 25.0 + (profile.storeExpansionLevel * 10.0)
        val staffCost = (if (profile.cashierHired) 30.0 else 0.0) + (if (profile.restockerHired) 25.0 else 0.0)
        val netProfit = dailyRevenue - dailyWholesaleExpense - rentCost - staffCost
        val satisfaction = if (dailyTotalReactions > 0) {
            ((dailyPositiveReactions.toDouble() / dailyTotalReactions) * 100).toInt()
        } else 100

        val xpEarned = (dailyRevenue * 2).toInt() + 50

        val summary = DaySummary(
            dayNumber = profile.currentDay,
            customersServed = dailyCustomersServed,
            revenue = dailyRevenue,
            wholesaleCost = dailyWholesaleExpense,
            rentCost = rentCost,
            staffCost = staffCost,
            netProfit = netProfit,
            satisfactionScore = satisfaction,
            xpEarned = xpEarned
        )
        _daySummary.value = summary

        // Save daily record to db
        viewModelScope.launch {
            dao.recordDay(
                DailyRecordEntity(
                    dayNumber = profile.currentDay,
                    revenue = dailyRevenue,
                    wholesaleCost = dailyWholesaleExpense,
                    rentCost = rentCost,
                    staffCost = staffCost,
                    netProfit = netProfit,
                    customersServed = dailyCustomersServed,
                    satisfactionScore = satisfaction
                )
            )

            // Deduct rent and staff wages
            val updatedMoney = ((profile.money - rentCost - staffCost) * 100.0).roundToInt() / 100.0
            dao.saveStoreProfile(
                profile.copy(
                    isStoreOpen = false,
                    money = updatedMoney
                )
            )
        }

        soundEffects.playSuccess(profile.soundEnabled, profile.hapticsEnabled)
    }

    fun startNextDay() {
        _daySummary.value = null
        val profile = storeProfile.value ?: return

        dailyRevenue = 0.0
        dailyWholesaleExpense = 0.0
        dailyCustomersServed = 0
        dailyPositiveReactions = 0
        dailyTotalReactions = 0
        _customerQueue.value = emptyList()

        // Fluctuate market prices for next day (+/- 10%)
        viewModelScope.launch {
            val states = productStates.value
            for (state in states) {
                val def = ProductCatalog.getProduct(state.productId) ?: continue
                val variance = (Random.nextDouble(-0.10, 0.12))
                val newMarketPrice = (((def.baseMarketPrice * (1.0 + variance)) * 100.0).roundToInt()) / 100.0
                dao.saveProductState(state.copy(marketPrice = newMarketPrice))
            }

            dao.saveStoreProfile(
                profile.copy(
                    currentDay = profile.currentDay + 1,
                    dayMinutes = 480, // Reset to 8:00 AM
                    isStoreOpen = false
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundEffects.release()
        gameLoopJob?.cancel()
        cashierAutoJob?.cancel()
    }
}
