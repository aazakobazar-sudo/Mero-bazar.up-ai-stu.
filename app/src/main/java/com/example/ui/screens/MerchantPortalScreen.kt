package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.BazarRepository
import com.example.model.DigitalInvoice
import com.example.model.OrderStatus
import com.example.model.ProductItem
import com.example.ui.components.DigitalInvoiceDialog
import com.example.ui.components.MarketDashboard
import com.example.ui.components.OrderMessengerDialog
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantPortalScreen(
    onNavigateBackToMarket: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isNepali by BazarRepository.isNepaliLanguage.collectAsState()
    val productsList by BazarRepository.products.collectAsState()
    val ordersList by BazarRepository.orders.collectAsState()
    val bargainsList by BazarRepository.bargains.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0 = Dashboard & Charts, 1 = Add Product, 2 = Inventory, 3 = Orders, 4 = Bargains
    var selectedInvoiceForModal by remember { mutableStateOf<DigitalInvoice?>(null) }
    var selectedChatOrderId by remember { mutableStateOf<String?>(null) }

    // Add Product Form State
    var newTitleNp by remember { mutableStateOf("") }
    var newTitleEn by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("कृषि तथा ताजा उत्पादन") }
    var newRetailPrice by remember { mutableStateOf("") }
    var newWholesalePrice by remember { mutableStateOf("") }
    var newMinMoq by remember { mutableStateOf("5") }
    var newStock by remember { mutableStateOf("50") }
    var newUnit by remember { mutableStateOf("थान (Pcs)") }
    var newLocation by remember { mutableStateOf("काठमाडौँ (Kathmandu)") }
    var newDescriptionNp by remember { mutableStateOf("") }
    var newSellerName by remember { mutableStateOf("मेरो व्यापार केन्द्र (My Business)") }
    var newSellerPhone by remember { mutableStateOf("+977-9851000000") }
    var newHasVideoAd by remember { mutableStateOf(true) }

    val categories = listOf("कृषि तथा ताजा उत्पादन", "खाद्यान्न तथा किराना", "हार्डवेयर तथा निर्माण सामग्री", "इलेक्ट्रोनिक्स तथा ग्याजेट", "फेसन तथा लत्ताकपडा")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("merchant_portal_screen"),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Portal Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PrimaryBlue.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(AccentEmerald.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (isNepali) "व्यापारी तथा बिक्रेता प्यानल" else "Merchant & Seller Dashboard",
                                color = AccentEmerald,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        IconButton(onClick = onNavigateBackToMarket) {
                            Icon(Icons.Default.Storefront, contentDescription = "Back to Market", tint = PrimaryBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isNepali) "व्यापार, स्टक तथा बिल व्यवस्थापन" else "Business, Stock & Invoicing Hub",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isNepali)
                            "दैनिक कारोबार ग्राफ, अटोमेटिक भ्याट बिल, ग्राहक च्याट तथा अनलाइन अडिटिङ प्रणाली।"
                        else
                            "Daily volume charts, automated VAT invoices, customer chat & transaction audit ledger.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Sub-Navigation Tabs (Dashboard, Add, Stock, Orders, Bargains)
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceVariant, RoundedCornerShape(10.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(
                    Pair(if (isNepali) "ड्यासबोर्ड" else "Dashboard", 0),
                    Pair(if (isNepali) "सामान थप्ने" else "Add Item", 1),
                    Pair(if (isNepali) "स्टक" else "Stock", 2),
                    Pair(if (isNepali) "अर्डर (${ordersList.size})" else "Orders", 3),
                    Pair(if (isNepali) "मोलतोल" else "Offers", 4)
                ).forEach { (label, index) ->
                    val isSelected = selectedTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) PrimaryBlue else Color.Transparent)
                            .clickable { selectedTab = index }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) DarkBackground else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Tab Content
        when (selectedTab) {
            // TAB 0: MARKET DASHBOARD (Daily Charts, Pending Orders, Growth & Auditing)
            0 -> {
                item {
                    MarketDashboard(
                        onOpenBillInvoice = { invoice -> selectedInvoiceForModal = invoice },
                        onOpenOrderChat = { orderId -> selectedChatOrderId = orderId }
                    )
                }
            }

            // TAB 1: ADD NEW PRODUCT LISTING (Requirement 1 & 3: unlimited items, media ads)
            1 -> {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (isNepali) "नयाँ सामान दर्ता फारम (New Product Listing)" else "Create Product Listing",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = newTitleNp,
                                onValueChange = { newTitleNp = it },
                                label = { Text(if (isNepali) "सामानको नाम (नेपालीमा)" else "Title (Nepali)", color = TextSecondary) },
                                placeholder = { Text("उदा: मुस्ताङी स्याउ / फेवा बासमती चामल", color = TextTertiary, fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth().testTag("input_new_prod_title_np"),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = newTitleEn,
                                onValueChange = { newTitleEn = it },
                                label = { Text(if (isNepali) "सामानको नाम (English)" else "Title (English)", color = TextSecondary) },
                                placeholder = { Text("e.g. Mustang Royal Apples", color = TextTertiary, fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth().testTag("input_new_prod_title_en"),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )

                            // Media & Video Ad Toggle (Requirement 1)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, SecondaryAmber.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                                colors = CardDefaults.cardColors(containerColor = DarkBackground)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isNepali) "आकर्षक फोटो तथा भिडियो विज्ञापन" else "Enable Video Ad & Photo Promo",
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = if (isNepali) "भिडियो विज्ञापन ब्याज सहित सामान हाइलाइट हुनेछ" else "Displays video ad badge & media banner",
                                            color = TextSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                    Switch(
                                        checked = newHasVideoAd,
                                        onCheckedChange = { newHasVideoAd = it },
                                        colors = SwitchDefaults.colors(checkedThumbColor = SecondaryAmber)
                                    )
                                }
                            }

                            // Category Selector Chips
                            Text(text = if (isNepali) "श्रेणी (Category):" else "Category:", color = TextSecondary, fontSize = 12.sp)
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                categories.chunked(2).forEach { rowCats ->
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        rowCats.forEach { cat ->
                                            val isSelected = newCategory == cat
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isSelected) PrimaryBlue.copy(alpha = 0.2f) else DarkSurfaceVariant)
                                                    .border(1.dp, if (isSelected) PrimaryBlue else DarkBorder, RoundedCornerShape(8.dp))
                                                    .clickable { newCategory = cat }
                                                    .padding(vertical = 6.dp, horizontal = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = cat, color = if (isSelected) PrimaryBlue else TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                            }
                                        }
                                    }
                                }
                            }

                            // Prices (Retail & Wholesale)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = newRetailPrice,
                                    onValueChange = { newRetailPrice = it.filter { c -> c.isDigit() } },
                                    label = { Text(if (isNepali) "खुद्रा मूल्य (रु.)" else "Retail Price (Rs.)", color = TextSecondary) },
                                    modifier = Modifier.weight(1f).testTag("input_new_retail_price"),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = newWholesalePrice,
                                    onValueChange = { newWholesalePrice = it.filter { c -> c.isDigit() } },
                                    label = { Text(if (isNepali) "थोक मूल्य (रु.)" else "Wholesale (Rs.)", color = TextSecondary) },
                                    modifier = Modifier.weight(1f).testTag("input_new_wholesale_price"),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true
                                )
                            }

                            // MOQ, Stock
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = newMinMoq,
                                    onValueChange = { newMinMoq = it.filter { c -> c.isDigit() } },
                                    label = { Text(if (isNepali) "न्यूनतम थोक" else "MOQ", color = TextSecondary) },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = newStock,
                                    onValueChange = { newStock = it.filter { c -> c.isDigit() } },
                                    label = { Text(if (isNepali) "कुल स्टक" else "Stock", color = TextSecondary) },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true
                                )
                            }

                            OutlinedTextField(
                                value = newLocation,
                                onValueChange = { newLocation = it },
                                label = { Text(if (isNepali) "गोदाम / पसल स्थान" else "Depot / Location", color = TextSecondary) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = newDescriptionNp,
                                onValueChange = { newDescriptionNp = it },
                                label = { Text(if (isNepali) "सामानको विवरण (Description)" else "Description", color = TextSecondary) },
                                modifier = Modifier.fillMaxWidth().height(90.dp),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Button(
                                onClick = {
                                    if (newTitleNp.isBlank() || newRetailPrice.isBlank()) {
                                        Toast.makeText(context, "कृपया सामानको नाम र मूल्य भर्नुहोस्।", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val retail = newRetailPrice.toIntOrNull() ?: 100
                                        val wholesale = newWholesalePrice.toIntOrNull() ?: (retail * 0.8).toInt()
                                        val moq = newMinMoq.toIntOrNull() ?: 1
                                        val stockQty = newStock.toIntOrNull() ?: 50

                                        val newProduct = ProductItem(
                                            id = "prod-${System.currentTimeMillis().toString().takeLast(5)}",
                                            titleNp = newTitleNp,
                                            titleEn = if (newTitleEn.isNotBlank()) newTitleEn else newTitleNp,
                                            category = newCategory,
                                            retailPrice = retail,
                                            wholesalePrice = wholesale,
                                            minOrderQty = moq,
                                            stock = stockQty,
                                            unit = newUnit,
                                            location = newLocation,
                                            rating = 5.0f,
                                            reviewCount = 1,
                                            sellerName = newSellerName,
                                            sellerPhone = newSellerPhone,
                                            isVerifiedBusiness = true,
                                            isWholesaleAvailable = true,
                                            descriptionNp = newDescriptionNp.ifBlank { "उच्च गुणस्तरीय प्रमाणित सामान।" },
                                            descriptionEn = "Verified quality product listed on Aajako Bazar.",
                                            minBargainThreshold = (wholesale * 0.95).toInt(),
                                            tags = listOf("नयाँ सामान", "बिक्रेता लिस्टिङ"),
                                            viewsCount = 1,
                                            likesCount = 0,
                                            hasVideoAd = newHasVideoAd,
                                            videoAdDuration = "0:30 HD",
                                            salesGrowthPercent = 10.0,
                                            monthlySoldUnits = 0
                                        )

                                        BazarRepository.addProduct(newProduct)
                                        Toast.makeText(context, "नयाँ सामान सफलतापूर्वक बजारमा प्रकाशित भयो!", Toast.LENGTH_LONG).show()

                                        newTitleNp = ""
                                        newTitleEn = ""
                                        newRetailPrice = ""
                                        newWholesalePrice = ""
                                        newDescriptionNp = ""
                                        selectedTab = 2 // Switch to inventory tab
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_publish_product")
                            ) {
                                Icon(Icons.Default.Publish, contentDescription = null, tint = DarkBackground)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isNepali) "बजारमा सामान प्रकाशित गर्नुहोस्" else "Publish Item to Marketplace",
                                    color = DarkBackground,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // TAB 2: INVENTORY & STOCK
            2 -> {
                items(productsList) { product ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isNepali) product.titleNp else product.titleEn,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Box(
                                    modifier = Modifier
                                        .background(AccentEmerald.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "स्टक: ${product.stock} ${product.unit}",
                                        color = AccentEmerald,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "खुद्रा: रु. ${product.retailPrice}", color = PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = "थोक: रु. ${product.wholesalePrice} (MOQ: ${product.minOrderQty})", color = SecondaryAmber, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = "भ्युज: ${product.viewsCount}", color = TextTertiary, fontSize = 11.sp)
                                Text(text = "लाइक: ${product.likesCount}", color = TextTertiary, fontSize = 11.sp)
                                Text(text = "कमेन्ट: ${product.comments.size}", color = TextTertiary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // TAB 3: CUSTOMER ORDERS & AUTO-GENERATED INVOICES (Requirement 4 & 5)
            3 -> {
                items(ordersList) { order ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = order.id, color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Box(
                                    modifier = Modifier
                                        .background(SecondaryAmber.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = if (isNepali) order.status.titleNp else order.status.titleEn,
                                        color = SecondaryAmber,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(text = "ग्राहक: ${order.customerName}", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(text = "फोन: ${order.customerPhone} | ठेगाना: ${order.deliveryAddress}", color = TextSecondary, fontSize = 11.sp)

                            Spacer(modifier = Modifier.height(8.dp))

                            order.items.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "• ${item.title} (x${item.quantity})", color = TextSecondary, fontSize = 12.sp)
                                    Text(text = "रु. ${item.quantity * item.unitPrice}", color = TextPrimary, fontSize = 12.sp)
                                }
                            }

                            HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "जम्मा: रु. ${NumberFormat.getNumberInstance(Locale.US).format(order.totalAmount)}",
                                    color = AccentEmerald,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    // Chat button
                                    OutlinedButton(
                                        onClick = { selectedChatOrderId = order.id },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Chat, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(text = "च्याट", color = PrimaryBlue, fontSize = 11.sp)
                                    }

                                    // Bill button (Requirement 4: Auto generated upon approval)
                                    if (order.generatedInvoice != null) {
                                        Button(
                                            onClick = { selectedInvoiceForModal = order.generatedInvoice },
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = "बिल हेर्नुहोस्", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    } else {
                                        Button(
                                            onClick = {
                                                val invoice = BazarRepository.approveOrderAndGenerateBill(order.id)
                                                if (invoice != null) {
                                                    selectedInvoiceForModal = invoice
                                                    Toast.makeText(context, "सामान स्वीकृत! बिल जारी भयो।", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = "ओके र बिल", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // TAB 4: BARGAIN OFFERS
            4 -> {
                if (bargainsList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(30.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isNepali) "हाल कुनै मोलतोल प्रस्ताव बाँकी छैन।" else "No active bargain offers yet.",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    items(bargainsList) { bargain ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(text = bargain.productTitle, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "सूचीकृत: रु. ${bargain.listedPrice}", color = TextSecondary, fontSize = 12.sp)
                                    Text(text = "ग्राहक प्रस्ताव: रु. ${bargain.offeredPrice}", color = SecondaryAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = bargain.message, color = AccentEmerald, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Invoice Dialog (Requirement 4)
    selectedInvoiceForModal?.let { invoice ->
        DigitalInvoiceDialog(
            invoice = invoice,
            isNepali = isNepali,
            onSendToChat = { inv ->
                BazarRepository.sendChatMessage(
                    orderId = inv.orderId,
                    text = "तपाईँको अर्डर #${inv.orderId.takeLast(4)} को आधिकारिक भ्याट बिल संलग्न गरिएको छ।",
                    isMerchantSender = true,
                    attachedInvoiceNumber = inv.invoiceNumber
                )
                selectedChatOrderId = inv.orderId
            },
            onDismiss = { selectedInvoiceForModal = null }
        )
    }

    // Messenger / Chat Dialog (Requirement 5)
    selectedChatOrderId?.let { orderId ->
        OrderMessengerDialog(
            orderId = orderId,
            isNepali = isNepali,
            onDismiss = { selectedChatOrderId = null }
        )
    }
}
