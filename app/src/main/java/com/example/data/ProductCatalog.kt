package com.example.data

data class ProductDef(
    val id: String,
    val name: String,
    val category: String,
    val iconEmoji: String,
    val wholesalePrice: Double,
    val baseMarketPrice: Double,
    val itemsPerBox: Int,
    val licenseRequired: Int,
    val shelfType: String = "SHELF" // SHELF, FRIDGE, FREEZER
)

object ProductCatalog {
    val ALL_PRODUCTS = listOf(
        // License 1: Basic Bakery & Pantry
        ProductDef("sliced_bread", "Sliced White Bread", "Bakery & Pantry", "🍞", 2.20, 3.80, 10, 1, "SHELF"),
        ProductDef("flour_bag", "All-Purpose Flour 1kg", "Bakery & Pantry", "🌾", 1.50, 2.70, 8, 1, "SHELF"),
        ProductDef("white_sugar", "Refined White Sugar", "Bakery & Pantry", "🧂", 1.80, 3.00, 8, 1, "SHELF"),
        ProductDef("pasta_spaghetti", "Spaghetti Pasta 500g", "Bakery & Pantry", "🍝", 1.40, 2.60, 12, 1, "SHELF"),

        // License 2: Dairy & Fresh Fridge
        ProductDef("fresh_milk", "Whole Farm Milk 1L", "Dairy & Eggs", "🥛", 2.10, 3.90, 8, 2, "FRIDGE"),
        ProductDef("farm_eggs", "Organic Eggs 12pk", "Dairy & Eggs", "🥚", 2.60, 4.50, 6, 2, "FRIDGE"),
        ProductDef("cheddar_cheese", "Cheddar Cheese Block", "Dairy & Eggs", "🧀", 3.20, 5.50, 6, 2, "FRIDGE"),
        ProductDef("salted_butter", "Irish Salted Butter", "Dairy & Eggs", "🧈", 2.80, 4.90, 8, 2, "FRIDGE"),

        // License 3: Breakfast & Coffee
        ProductDef("choco_cereal", "Choco Crunch Cereal", "Breakfast & Coffee", "🥣", 3.40, 5.80, 8, 3, "SHELF"),
        ProductDef("ground_coffee", "Arabica Ground Coffee", "Breakfast & Coffee", "☕", 4.50, 7.80, 6, 3, "SHELF"),
        ProductDef("black_tea", "English Breakfast Tea", "Breakfast & Coffee", "🫖", 2.50, 4.20, 10, 3, "SHELF"),
        ProductDef("pure_honey", "Raw Wildflower Honey", "Breakfast & Coffee", "🍯", 4.00, 6.90, 6, 3, "SHELF"),

        // License 4: Cold Beverages & Drinks
        ProductDef("sparkling_cola", "Techno Cola 330ml", "Beverages", "🥤", 1.10, 2.20, 12, 4, "FRIDGE"),
        ProductDef("orange_juice", "Fresh Orange Juice 1L", "Beverages", "🍊", 2.30, 4.00, 8, 4, "FRIDGE"),
        ProductDef("energy_drink", "Cyber Volt Energy Drink", "Beverages", "⚡", 1.70, 3.50, 12, 4, "FRIDGE"),
        ProductDef("mineral_water", "Spring Mineral Water 6pk", "Beverages", "💧", 1.90, 3.40, 6, 4, "SHELF"),

        // License 5: Frozen Foods
        ProductDef("frozen_pizza", "Stone Baked Pepperoni Pizza", "Frozen Foods", "🍕", 4.80, 8.50, 6, 5, "FREEZER"),
        ProductDef("vanilla_icecream", "Artisan Vanilla Ice Cream", "Frozen Foods", "🍨", 3.90, 6.80, 6, 5, "FREEZER"),
        ProductDef("french_fries", "Crispy French Fries 1kg", "Frozen Foods", "🍟", 2.40, 4.30, 8, 5, "FREEZER"),
        ProductDef("beef_burgers", "Gourmet Beef Patties 4pk", "Frozen Foods", "🍔", 5.20, 9.20, 6, 5, "FREEZER"),

        // License 6: Cleaning & Household
        ProductDef("dish_soap", "Citrus Sparkle Dish Soap", "Household & Cleaning", "🧼", 2.00, 3.60, 8, 6, "SHELF"),
        ProductDef("detergent_pods", "Ultra Clean Laundry Pods", "Household & Cleaning", "🧺", 5.50, 9.80, 4, 6, "SHELF"),
        ProductDef("paper_towels", "Absorbent Paper Towels 2pk", "Household & Cleaning", "🧻", 2.20, 3.90, 6, 6, "SHELF"),
        ProductDef("potato_chips", "Crispy BBQ Potato Chips", "Snacks & Sweets", "🥔", 1.60, 3.10, 10, 6, "SHELF")
    )

    fun getProduct(id: String): ProductDef? = ALL_PRODUCTS.find { it.id == id }
}
