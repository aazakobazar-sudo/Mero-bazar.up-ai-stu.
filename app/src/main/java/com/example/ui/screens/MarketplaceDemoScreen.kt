package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.PortfolioRepository
import com.example.model.MarketProduct
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceDemoScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedSort by remember { mutableStateOf("Recommended") }
    var selectedProductForDetail by remember { mutableStateOf<MarketProduct?>(null) }
    var showAnalyticsView by remember { mutableStateOf(false) }

    val categories = listOf("All", "Krishi & Farm Fresh", "Electronics & Gadgets", "Vehicles & Bikes", "Fashion & Handcrafts")
    val allProducts = PortfolioRepository.sampleMarketProducts

    val filteredProducts = remember(searchQuery, selectedCategory, selectedSort) {
        var list = allProducts.filter { product ->
            val matchesCategory = selectedCategory == "All" || product.category.contains(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    product.title.contains(searchQuery, ignoreCase = true) ||
                    product.description.contains(searchQuery, ignoreCase = true) ||
                    product.location.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }

        when (selectedSort) {
            "Price: Low to High" -> list.sortedBy { it.priceNpr }
            "Price: High to Low" -> list.sortedByDescending { it.priceNpr }
            "Top Rated" -> list.sortedByDescending { it.rating }
            else -> list
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("marketplace_demo_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Simulation Notice Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .border(1.dp, DarkBorder)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(AccentEmerald, CircleShape)
                        )
                        Column {
                            Text(
                                text = "Aajako Bazar Live Simulation",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Interactive Client Engine by Surya Kafle",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Toggle between Catalog and Analytics
                    Row(
                        modifier = Modifier
                            .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (!showAnalyticsView) PrimaryBlue else Color.Transparent)
                                .clickable { showAnalyticsView = false }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Catalog",
                                color = if (!showAnalyticsView) DarkBackground else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (showAnalyticsView) PrimaryBlue else Color.Transparent)
                                .clickable { showAnalyticsView = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Telemetry",
                                color = if (showAnalyticsView) DarkBackground else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        if (showAnalyticsView) {
            item {
                MarketTelemetrySection()
            }
        } else {
            // Search and Filter Bar
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Search Field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_market_search"),
                        placeholder = { Text("Search 52,000+ listings in Nepal...", color = TextTertiary, fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = PrimaryBlue)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )

                    // Categories Horizontal Scroll
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { category ->
                            val isSelected = selectedCategory == category
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) SecondaryAmber else DarkSurfaceVariant)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) SecondaryAmber else DarkBorder,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedCategory = category }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = category,
                                    color = if (isSelected) DarkBackground else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    // Results header & sort selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${filteredProducts.size} Active Products Shown",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "Sort:", color = TextTertiary, fontSize = 12.sp)
                            Box(
                                modifier = Modifier
                                    .background(DarkSurfaceVariant, RoundedCornerShape(6.dp))
                                    .clickable {
                                        selectedSort = when (selectedSort) {
                                            "Recommended" -> "Price: Low to High"
                                            "Price: Low to High" -> "Price: High to Low"
                                            "Price: High to Low" -> "Top Rated"
                                            else -> "Recommended"
                                        }
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = selectedSort,
                                    color = PrimaryBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Products Listing
            if (filteredProducts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No listings matching '$searchQuery'",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Try searching for Mustang, Honey, Apple or Royal Enfield",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredProducts) { product ->
                    MarketProductCard(
                        product = product,
                        onOpenDetail = { selectedProductForDetail = product },
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }

    // Interactive Detail & Bargaining Modal Dialog
    selectedProductForDetail?.let { product ->
        ProductBargainDetailDialog(
            product = product,
            onDismiss = { selectedProductForDetail = null }
        )
    }
}

@Composable
fun MarketProductCard(
    product: MarketProduct,
    onOpenDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .clickable { onOpenDetail() },
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Category & Condition Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(PrimaryBlue.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = product.category,
                        color = PrimaryBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .background(AccentEmerald.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = product.condition,
                        color = AccentEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = product.title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Location & Rating
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(13.dp))
                    Text(text = product.location, color = TextSecondary, fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = SecondaryAmber, modifier = Modifier.size(13.dp))
                    Text(text = "${product.rating} (${product.reviewsCount})", color = TextSecondary, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Price Row & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(product.priceNpr)
                    val formattedOriginal = NumberFormat.getNumberInstance(Locale.US).format(product.originalPriceNpr)
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Rs. $formattedPrice",
                            color = SecondaryAmber,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Rs. $formattedOriginal",
                            color = TextTertiary,
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.bodySmall.copy(
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                            )
                        )
                    }
                    Text(
                        text = "Seller: ${product.sellerName}",
                        color = TextTertiary,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = onOpenDetail,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Handshake, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Bargain / Buy", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ProductBargainDetailDialog(
    product: MarketProduct,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var offerInput by remember { mutableStateOf("") }
    var bargainResultText by remember { mutableStateOf<String?>(null) }
    var bargainStatusType by remember { mutableStateOf<String?>(null) } // "ACCEPTED", "COUNTER", "DECLINED"
    var showCheckoutConfirmation by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .border(1.dp, PrimaryBlue.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkBackground),
            shape = RoundedCornerShape(20.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(SecondaryAmber.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "LIVE MARKETPLACE DEMO",
                                color = SecondaryAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(product.priceNpr)
                    Text(
                        text = "Listed Price: Rs. $formattedPrice NPR",
                        color = SecondaryAmber,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = product.description,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Seller info card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(text = product.sellerName, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    if (product.verifiedSeller) {
                                        Icon(Icons.Default.Verified, contentDescription = "Verified", tint = PrimaryBlue, modifier = Modifier.size(15.dp))
                                    }
                                }
                                Text(text = "Location: ${product.location}", color = TextSecondary, fontSize = 11.sp)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = {
                                        Toast.makeText(context, "Simulated call connected to ${product.sellerName}", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(PrimaryBlue.copy(alpha = 0.15f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = "Call", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = {
                                        Toast.makeText(context, "WhatsApp direct channel opened for ${product.sellerName}", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(AccentEmerald.copy(alpha = 0.15f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = "Chat", tint = AccentEmerald, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Smart Bargaining Engine Simulation
                    Text(
                        text = "Smart Cultural Bargaining Engine",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Test Surya Kafle's dynamic price negotiation algorithm below:",
                        color = TextTertiary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick shortcut chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(5, 10, 15).forEach { percent ->
                            val calculatedOffer = (product.priceNpr * (100 - percent) / 100)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkSurfaceVariant)
                                    .clickable { offerInput = calculatedOffer.toString() }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "-$percent% (Rs. $calculatedOffer)", color = TextPrimary, fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = offerInput,
                            onValueChange = { offerInput = it.filter { char -> char.isDigit() } },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Your offer (NPR)", fontSize = 13.sp, color = TextTertiary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SecondaryAmber,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Button(
                            onClick = {
                                val entered = offerInput.toIntOrNull()
                                if (entered == null || entered <= 0) {
                                    bargainResultText = "Please enter a valid numeric offer in NPR."
                                    bargainStatusType = "DECLINED"
                                } else {
                                    val threshold = product.bargainAcceptedMinPrice
                                    val discountPercent = ((product.priceNpr - entered).toDouble() / product.priceNpr) * 100
                                    when {
                                        entered >= product.priceNpr -> {
                                            bargainResultText = "Offer Accepted! Deal Token #AB-${(10000..99999).random()} generated."
                                            bargainStatusType = "ACCEPTED"
                                        }
                                        entered >= threshold && discountPercent <= 15.0 -> {
                                            bargainResultText = "Deal Accepted! Seller approved Rs. $entered. Escrow voucher issued."
                                            bargainStatusType = "ACCEPTED"
                                        }
                                        entered >= threshold -> {
                                            val counter = (entered + threshold) / 2
                                            bargainResultText = "Counter-Offer from Seller: 'I can do Rs. $counter.' What do you say?"
                                            bargainStatusType = "COUNTER"
                                        }
                                        else -> {
                                            bargainResultText = "Offer Declined: Rs. $entered is below the seller's minimum reserve threshold."
                                            bargainStatusType = "DECLINED"
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "Send Offer", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    // Bargaining result box
                    bargainResultText?.let { result ->
                        Spacer(modifier = Modifier.height(10.dp))
                        val boxColor = when (bargainStatusType) {
                            "ACCEPTED" -> AccentEmerald
                            "COUNTER" -> PrimaryBlue
                            else -> Color(0xFFF87171)
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(boxColor.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                                .border(1.dp, boxColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(text = result, color = boxColor, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Direct Buy Simulation
                    if (showCheckoutConfirmation) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(AccentEmerald.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .border(1.dp, AccentEmerald, RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Order Successfully Simulated!",
                                    color = AccentEmerald,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tracking ID: NPL-${System.currentTimeMillis().toString().takeLast(6)}\nDelivery: Kathmandu Valley Hub (Express 24h)\nEscrow: Surya Kafle Secured Gateway",
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = { showCheckoutConfirmation = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = DarkBackground)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Simulate Order Checkout",
                                color = DarkBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarketTelemetrySection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Live Marketplace Telemetry & GMV",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Real-time metrics aggregating 7 Provinces across Nepal",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Region breakdown bars
                listOf(
                    Triple("Bagmati (Kathmandu Valley)", 62, "Rs. 1.15M/mo"),
                    Triple("Gandaki (Pokhara & Mustang)", 18, "Rs. 330K/mo"),
                    Triple("Koshi (Ilam & Biratnagar)", 11, "Rs. 205K/mo"),
                    Triple("Lumbini (Chitwan & Butwal)", 9, "Rs. 165K/mo")
                ).forEach { (region, percentage, volume) ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = region, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text(text = volume, color = SecondaryAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .background(DarkSurfaceVariant, RoundedCornerShape(4.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(percentage / 100f)
                                    .height(8.dp)
                                    .background(PrimaryBlue, RoundedCornerShape(4.dp))
                            )
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Database & Server Performance",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Firestore Read/Write", color = TextSecondary, fontSize = 11.sp)
                        Text(text = "1.2M docs / day", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column {
                        Text(text = "Cache Hit Ratio", color = TextSecondary, fontSize = 11.sp)
                        Text(text = "98.4% Edge", color = AccentEmerald, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column {
                        Text(text = "Crash-Free Users", color = TextSecondary, fontSize = 11.sp)
                        Text(text = "99.94%", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
