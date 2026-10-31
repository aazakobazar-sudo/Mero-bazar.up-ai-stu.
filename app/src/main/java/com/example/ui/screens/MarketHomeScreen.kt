package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.BazarRepository
import com.example.model.MarketCommodityRate
import com.example.model.OrderItem
import com.example.model.ProductItem
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketHomeScreen(
    onNavigateToMerchantPortal: () -> Unit,
    onNavigateToWholesaleDirectory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isNepali by BazarRepository.isNepaliLanguage.collectAsState()
    val isWholesaleMode by BazarRepository.isWholesaleMode.collectAsState()
    val productsList by BazarRepository.products.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("सबै") }
    var selectedProductForDetail by remember { mutableStateOf<ProductItem?>(null) }
    var showCommoditiesModal by remember { mutableStateOf(false) }

    val categoriesNp = listOf(
        "सबै",
        "दैनिक घरेलु सामाग्रीहरु",
        "सवारी साधन",
        "विद्युतीय तथा नेटवर्क सामग्री",
        "शैक्षिक तथा स्वास्थ्य",
        "सामग्री तथा उत्पादनहरू",
        "वित्तीय संस्था",
        "निर्माण सामग्री",
        "अन्य सामग्री तथा औजार"
    )
    val categoriesEn = listOf(
        "All",
        "Daily Household",
        "Vehicles & Auto",
        "Electronics & Network",
        "Education & Health",
        "Products & Mfg",
        "Financial",
        "Construction",
        "Other Goods & Tools"
    )

    val activeCategories = if (isNepali) categoriesNp else categoriesEn

    val filteredProducts = remember(productsList, searchQuery, selectedCategory, isWholesaleMode) {
        productsList.filter { product ->
            val matchesCategory = selectedCategory == "सबै" || selectedCategory == "All" ||
                    product.category.contains(selectedCategory, ignoreCase = true) ||
                    selectedCategory.contains(product.category, ignoreCase = true)

            val matchesSearch = searchQuery.isBlank() ||
                    product.titleNp.contains(searchQuery, ignoreCase = true) ||
                    product.titleEn.contains(searchQuery, ignoreCase = true) ||
                    product.location.contains(searchQuery, ignoreCase = true) ||
                    product.sellerName.contains(searchQuery, ignoreCase = true)

            val matchesWholesale = !isWholesaleMode || product.isWholesaleAvailable

            matchesCategory && matchesSearch && matchesWholesale
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("market_home_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Daily Commodities Rates Ticker (आजको बजार भाउ)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF090E1A))
                    .border(1.dp, Color(0xFF1E293B))
                    .clickable { showCommoditiesModal = true }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
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
                                .background(SecondaryAmber.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (isNepali) "दैनिक भाउ" else "Daily Rates",
                                color = SecondaryAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }

                        val sampleRate = BazarRepository.dailyCommodityRates.first()
                        Text(
                            text = if (isNepali)
                                "${sampleRate.nameNp}: थोक रु. ${sampleRate.wholesalePrice} / खुद्रा रु. ${sampleRate.retailPrice}"
                            else
                                "${sampleRate.nameEn}: Wholesale Rs. ${sampleRate.wholesalePrice} / Retail Rs. ${sampleRate.retailPrice}",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }

                    Text(
                        text = if (isNepali) "हेर्नुहोस् →" else "View All →",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Wholesale Mode Callout Banner (if in Wholesale B2B mode)
        if (isWholesaleMode) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFF2C1E07), Color(0xFF131B2A))),
                            RoundedCornerShape(14.dp)
                        )
                        .border(1.dp, SecondaryAmber.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = SecondaryAmber, modifier = Modifier.size(16.dp))
                                Text(
                                    text = if (isNepali) "ठूला व्यवसायी तथा थोक खरिद बजार" else "B2B & Bulk Wholesale Procurement",
                                    color = SecondaryAmber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isNepali)
                                    "थोक दर, भ्याट बिल, ठूलो परिमाण तथा प्रत्यक्ष निर्माता सम्पर्क"
                                else
                                    "Factory-direct wholesale rates with VAT invoices & bulk delivery",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = onNavigateToWholesaleDirectory,
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isNepali) "डाइरेक्टरी" else "Directory",
                                color = DarkBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Search Bar & Filter Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_bazar_input"),
                    placeholder = {
                        Text(
                            text = if (isNepali) "सामान, व्यापारी वा स्थान खोज्नुहोस्..." else "Search goods, sellers or location...",
                            color = TextTertiary,
                            fontSize = 13.sp
                        )
                    },
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

                // Category Pills Horizontal Scroll
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(activeCategories) { category ->
                        val isSelected = selectedCategory == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PrimaryBlue else DarkSurfaceVariant)
                                .border(
                                    1.dp,
                                    if (isSelected) PrimaryBlue else DarkBorder,
                                    RoundedCornerShape(10.dp)
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

                // Count & Fast Actions Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isNepali)
                            "${filteredProducts.size} सामानहरू उपलब्ध छन्"
                        else
                            "${filteredProducts.size} Products Available",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clickable { onNavigateToMerchantPortal() }
                    ) {
                        Icon(Icons.Default.AddBusiness, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(15.dp))
                        Text(
                            text = if (isNepali) "बिक्रेता पोर्टल" else "Seller Portal",
                            color = AccentEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Product Cards
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
                            text = if (isNepali) "कुनै सामान फेला परेन" else "No matching products found",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isNepali) "फरक शब्द वा श्रेणी छान्नुहोस्" else "Try another search keyword or category",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(filteredProducts) { product ->
                BazarProductCard(
                    product = product,
                    isNepali = isNepali,
                    isWholesaleMode = isWholesaleMode,
                    onOpenDetail = { selectedProductForDetail = product },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }

    // Detail & Bargaining Modal Dialog
    selectedProductForDetail?.let { product ->
        BazarProductDetailDialog(
            product = product,
            isNepali = isNepali,
            isWholesaleMode = isWholesaleMode,
            onDismiss = { selectedProductForDetail = null }
        )
    }

    // Daily Commodities Index Modal
    if (showCommoditiesModal) {
        DailyCommoditiesDialog(
            isNepali = isNepali,
            onDismiss = { showCommoditiesModal = false }
        )
    }
}

@Composable
fun BazarProductCard(
    product: ProductItem,
    isNepali: Boolean,
    isWholesaleMode: Boolean,
    onOpenDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .clickable { onOpenDetail() },
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Category & Badge Row
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

                if (isWholesaleMode) {
                    Box(
                        modifier = Modifier
                            .background(SecondaryAmber.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .border(1.dp, SecondaryAmber.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isNepali) "थोक न्यूनतम: ${product.minOrderQty} ${product.unit}" else "MOQ: ${product.minOrderQty} ${product.unit}",
                            color = SecondaryAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .background(AccentEmerald.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isNepali) "स्टक: ${product.stock} थान" else "Stock: ${product.stock}",
                            color = AccentEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = if (isNepali) product.titleNp else product.titleEn,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Video Ad Badge (Requirement 1)
            if (product.hasVideoAd) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF3B0764), RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0xFFC084FC).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color(0xFFC084FC), modifier = Modifier.size(12.dp))
                        Text(
                            text = if (isNepali) "भिडियो विज्ञापन (${product.videoAdDuration})" else "Video Ad (${product.videoAdDuration})",
                            color = Color(0xFFC084FC),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Location & Seller Meta
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(13.dp))
                    Text(text = product.location, color = TextSecondary, fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Default.Store, contentDescription = null, tint = SecondaryAmber, modifier = Modifier.size(13.dp))
                    Text(text = product.sellerName, color = TextSecondary, fontSize = 12.sp, maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Social Engagement & Share Row (Requirement 2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "👁 ${product.viewsCount} भ्युज", color = TextTertiary, fontSize = 11.sp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        modifier = Modifier.clickable { BazarRepository.likeProduct(product.id) }
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = "Like", tint = Color(0xFFF43F5E), modifier = Modifier.size(13.dp))
                        Text(text = "${product.likesCount}", color = TextSecondary, fontSize = 11.sp)
                    }
                    Text(text = "💬 ${product.comments.size}", color = TextTertiary, fontSize = 11.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.clickable {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "${product.titleNp} - आजको बजारमा रु. ${product.retailPrice} मा उपलब्ध छ!")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "सेयर गर्नुहोस्"))
                    }
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = TextSecondary, modifier = Modifier.size(14.dp))
                    Text(text = "सेयर", color = TextSecondary, fontSize = 11.sp)
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
                    val activePrice = if (isWholesaleMode) product.wholesalePrice else product.retailPrice
                    val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(activePrice)
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "रु. $formattedPrice",
                            color = if (isWholesaleMode) SecondaryAmber else PrimaryBlue,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = if (isWholesaleMode) "(थोक दर)" else "(खुद्रा दर)",
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = onOpenDetail,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isWholesaleMode) SecondaryAmber else PrimaryBlue
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Handshake,
                            contentDescription = null,
                            tint = DarkBackground,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isNepali) "विवरण / मोलतोल" else "Details / Offer",
                            color = DarkBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BazarProductDetailDialog(
    product: ProductItem,
    isNepali: Boolean,
    isWholesaleMode: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var offerAmountInput by remember { mutableStateOf("") }
    var orderQuantityInput by remember {
        mutableStateOf(if (isWholesaleMode) product.minOrderQty.toString() else "1")
    }
    var bargainFeedbackText by remember { mutableStateOf<String?>(null) }
    var orderSuccessDialog by remember { mutableStateOf(false) }
    var isVideoPlaying by remember { mutableStateOf(false) }
    var newCommentText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
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
                    // Header with close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(PrimaryBlue.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = product.category,
                                color = PrimaryBlue,
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
                        text = if (isNepali) product.titleNp else product.titleEn,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dual Pricing Table (खुद्रा र थोक दर)
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
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isNepali) "खुद्रा मूल्य (Retail)" else "Retail Price",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "रु. ${NumberFormat.getNumberInstance(Locale.US).format(product.retailPrice)}",
                                    color = PrimaryBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Box(modifier = Modifier.width(1.dp).height(32.dp).background(DarkBorder))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isNepali) "थोक मूल्य (Wholesale)" else "Wholesale Price",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "रु. ${NumberFormat.getNumberInstance(Locale.US).format(product.wholesalePrice)}",
                                    color = SecondaryAmber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "(न्यूनतम ${product.minOrderQty} ${product.unit})",
                                    color = TextTertiary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description
                    Text(
                        text = if (isNepali) product.descriptionNp else product.descriptionEn,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Seller Card with Direct Call & WhatsApp
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
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(text = product.sellerName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    if (product.isVerifiedBusiness) {
                                        Icon(Icons.Default.Verified, contentDescription = "Verified", tint = PrimaryBlue, modifier = Modifier.size(15.dp))
                                    }
                                }
                                Text(text = "स्थान: ${product.location}", color = TextSecondary, fontSize = 11.sp)
                                Text(text = "सम्पर्क: ${product.sellerPhone}", color = TextTertiary, fontSize = 11.sp)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = {
                                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${product.sellerPhone}"))
                                        try { context.startActivity(dialIntent) } catch (e: Exception) {
                                            Toast.makeText(context, "सम्पर्क: ${product.sellerPhone}", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.size(36.dp).background(PrimaryBlue.copy(alpha = 0.15f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = "Call", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                                }

                                IconButton(
                                    onClick = {
                                        Toast.makeText(context, "WhatsApp प्रत्यक्ष च्याट सुरु भयो: ${product.sellerName}", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(36.dp).background(AccentEmerald.copy(alpha = 0.15f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = AccentEmerald, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    // Requirement 1: Video Advertising Showcase
                    if (product.hasVideoAd) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, SecondaryAmber.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E170A))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.Videocam, contentDescription = null, tint = SecondaryAmber, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = if (isNepali) "विज्ञापन भिडियो (HD Ad)" else "Product Video Commercial",
                                            color = SecondaryAmber,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = product.videoAdDuration, color = Color.White, fontSize = 10.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF0F0F14))
                                        .clickable { isVideoPlaying = !isVideoPlaying },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = if (isVideoPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                            contentDescription = "Play Video Ad",
                                            tint = if (isVideoPlaying) AccentEmerald else SecondaryAmber,
                                            modifier = Modifier.size(40.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (isVideoPlaying)
                                                "विज्ञापन भिडियो बजिरहेको छ (Playing...)"
                                            else
                                                "भिडियो विज्ञापन हेर्न ट्याप गर्नुहोस् (Tap to Play)",
                                            color = Color.White,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Requirement 2: Comments and Community Feedback
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isNepali) "ग्राहक समीक्षा तथा कमेन्टहरू (${product.comments.size})" else "Reviews & Comments (${product.comments.size})",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        product.comments.forEach { comment ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = comment.author, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                        Text(text = comment.timeAgo, color = TextTertiary, fontSize = 10.sp)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = comment.text, color = TextSecondary, fontSize = 11.sp)
                                }
                            }
                        }

                        // Add Comment Input Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedTextField(
                                value = newCommentText,
                                onValueChange = { newCommentText = it },
                                placeholder = {
                                    Text(
                                        if (isNepali) "कमेन्ट लेख्नुहोस्..." else "Write a comment...",
                                        fontSize = 11.sp,
                                        color = TextTertiary
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = DarkSurface,
                                    unfocusedContainerColor = DarkSurface,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = PrimaryBlue,
                                    unfocusedBorderColor = DarkBorder
                                )
                            )
                            Button(
                                onClick = {
                                    if (newCommentText.isNotBlank()) {
                                        BazarRepository.addCommentToProduct(product.id, "ग्राहक", newCommentText.trim())
                                        newCommentText = ""
                                        Toast.makeText(context, "कमेन्ट थपियो!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (isNepali) "पठाउनुहोस्" else "Post",
                                    fontSize = 11.sp,
                                    color = DarkBackground,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Dynamic Smart Bargaining Section (मोलतोल प्रणाली)
                    Text(
                        text = if (isNepali) "मूल्य मोलतोल गर्नुहोस् (Make an Offer)" else "Price Negotiation (Make an Offer)",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = if (isNepali)
                            "तपाईँको प्रस्तावित मूल्य राख्नुहोस्, बिक्रेताले तत्कालै मूल्याङ्कन गर्नेछ:"
                        else
                            "Propose your bid; seller algorithm calculates counter-offer instantly:",
                        color = TextTertiary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = offerAmountInput,
                            onValueChange = { offerAmountInput = it.filter { c -> c.isDigit() } },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text(if (isNepali) "प्रस्तावित मूल्य (रु.)" else "Your Offer (Rs.)", fontSize = 12.sp, color = TextTertiary) },
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
                                val entered = offerAmountInput.toIntOrNull()
                                if (entered == null || entered <= 0) {
                                    bargainFeedbackText = if (isNepali) "कृपया मान्य अंक प्रविष्ट गर्नुहोस्।" else "Please enter a valid amount."
                                } else {
                                    val targetBase = if (isWholesaleMode) product.wholesalePrice else product.retailPrice
                                    val (status, msg) = BazarRepository.submitBargainOffer(
                                        productId = product.id,
                                        productTitle = product.titleNp,
                                        listedPrice = targetBase,
                                        offeredPrice = entered,
                                        minThreshold = product.minBargainThreshold
                                    )
                                    bargainFeedbackText = msg
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (isNepali) "मोलतोल पठाउनुहोस्" else "Send Offer",
                                color = DarkBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Bargaining result message
                    bargainFeedbackText?.let { feedback ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F1E2E), RoundedCornerShape(8.dp))
                                .border(1.dp, PrimaryBlue.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(text = feedback, color = PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Direct Buy / Bulk Order Button
                    if (orderSuccessDialog) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(AccentEmerald.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .border(1.dp, AccentEmerald, RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Text(
                                    text = if (isNepali) "अर्डर सफलतापूर्वक दर्ता भयो!" else "Order Placed Successfully!",
                                    color = AccentEmerald,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isNepali)
                                        "अर्डर नम्बर: NPL-${(100000..999999).random()}\nडेलिभरी ठेगाना: काठमाडौँ उपत्यका तथा बाहिर २४ घण्टाभित्र\nभुक्तानी: क्यास अन डेलिभरी / इसेवा"
                                    else
                                        "Order Token: NPL-${(100000..999999).random()}\nDispatched within 24 hours.\nPayment: Cash on Delivery / eSewa",
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = {
                                val qty = orderQuantityInput.toIntOrNull() ?: 1
                                val unitPrice = if (isWholesaleMode) product.wholesalePrice else product.retailPrice
                                BazarRepository.placeOrder(
                                    customerName = "राजेश श्रेष्ठ (ग्राहक)",
                                    customerPhone = "+977-9851000000",
                                    deliveryAddress = "काठमाडौँ",
                                    items = listOf(OrderItem(product.id, product.titleNp, qty, unitPrice)),
                                    isWholesale = isWholesaleMode,
                                    paymentMethod = "Cash on Delivery / eSewa"
                                )
                                orderSuccessDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = DarkBackground)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isNepali)
                                    (if (isWholesaleMode) "थोक अर्डर बुक गर्नुहोस्" else "सामान अर्डर गर्नुहोस्")
                                else
                                    (if (isWholesaleMode) "Book Bulk Wholesale Order" else "Place Order Now"),
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
fun DailyCommoditiesDialog(
    isNepali: Boolean,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .border(1.dp, SecondaryAmber.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkBackground),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isNepali) "आजको बजार दरभाउ" else "Daily Market Price Index",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = if (isNepali) "कालिमाटी तथा प्रमुख थोक मण्डी मूल्य" else "Kalimati & Major Wholesale Mandi Rates",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(BazarRepository.dailyCommodityRates) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, DarkBorder, RoundedCornerShape(10.dp)),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (isNepali) item.nameNp else item.nameEn,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = item.unit,
                                        color = TextTertiary,
                                        fontSize = 11.sp
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "थोक: रु. ${item.wholesalePrice}",
                                            color = SecondaryAmber,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "खुद्रा: रु. ${item.retailPrice}",
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (item.isUp) AccentEmerald.copy(alpha = 0.15f) else Color(0x33EF4444),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = item.change,
                                            color = if (item.isUp) AccentEmerald else Color(0xFFEF4444),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isNepali) "बन्द गर्नुहोस्" else "Close",
                        color = DarkBackground,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
