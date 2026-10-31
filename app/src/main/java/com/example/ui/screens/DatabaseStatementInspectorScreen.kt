package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.BazarRepository
import com.example.ui.theme.*

/**
 * PDF Section C.4.2: DATABASE STATEMENT SYSTEM (INTERACTIVE INSPECTOR MODE)
 * All database tables rendered as clickable statement logs with schema inspector:
 * - 4.2.1 USERS TABLE
 * - 4.2.2 SHOPS TABLE
 * - 4.2.3 PRODUCTS TABLE
 * - 4.2.4 ORDERS TABLE
 * - 4.2.5 PAYMENTS TABLE (IRD Prize portal integration: prize.ird.gov.np)
 * - 4.2.6 KHATA TABLE (Credit उधारो ledger)
 * - 4.2.7 VIDEOS TABLE (Reels & marketing override)
 * - 4.2.8 CHAT TABLE (Audit & purge)
 */
enum class DatabaseTableType(val displayName: String, val icon: String, val schemaFields: List<String>) {
    USERS(
        "USERS TABLE (प्रयोगकर्ता)",
        "👥",
        listOf("user_id", "name", "phone", "email", "password_hash", "role", "address", "ward_number", "created_date")
    ),
    SHOPS(
        "SHOPS TABLE (पसल विवरण)",
        "🏪",
        listOf("shop_id", "owner_id", "shop_name", "shop_logo", "shop_location", "ward_number", "opening_time", "closing_time", "status")
    ),
    PRODUCTS(
        "PRODUCTS TABLE (सामान सूची)",
        "📦",
        listOf("product_id", "shop_id", "product_name", "category", "price", "discount_price", "stock", "product_image", "created_date")
    ),
    ORDERS(
        "ORDERS TABLE (अर्डर विवरण)",
        "🛒",
        listOf("order_id", "customer_id", "shop_id", "product_id", "quantity", "total_price", "payment_method", "order_status", "order_date")
    ),
    PAYMENTS(
        "PAYMENTS TABLE (भुक्तानी तथा कर)",
        "💳",
        listOf("payment_id", "order_id", "customer_id", "amount", "payment_method", "transaction_id", "payment_date", "ird_prize_status")
    ),
    KHATA(
        "KHATA TABLE (उधारो खाता)",
        "📖",
        listOf("khata_id", "customer_id", "shop_id", "amount_due", "last_payment", "due_date")
    ),
    VIDEOS(
        "VIDEOS TABLE (भिडियो रिल्स)",
        "🎥",
        listOf("video_id", "shop_id", "product_id", "video_url", "views", "likes", "upload_date")
    ),
    CHAT(
        "CHAT TABLE (च्याट अडिट)",
        "💬",
        listOf("message_id", "sender_id", "receiver_id", "message", "message_type", "time")
    )
}

data class GenericDbRecord(
    val id: String,
    val title: String,
    val subtitle: String,
    val statusBadge: String,
    val statusColor: Color,
    val fieldsMap: Map<String, String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatabaseStatementInspectorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTable by remember { mutableStateOf(DatabaseTableType.USERS) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedRecordForDetail by remember { mutableStateOf<GenericDbRecord?>(null) }

    BackHandler {
        if (selectedRecordForDetail != null) {
            selectedRecordForDetail = null
        } else {
            onBack()
        }
    }

    // Sample data generation based on selected table
    val tableRecords = remember(selectedTable) {
        when (selectedTable) {
            DatabaseTableType.USERS -> listOf(
                GenericDbRecord(
                    id = "usr-adm-01",
                    title = "केन्द्रीय व्यवस्थापक (Master Admin)",
                    subtitle = "aazakobazar@gmail.com · +977-9851000001",
                    statusBadge = "ADMIN (सक्रिय)",
                    statusColor = SecondaryAmber,
                    fieldsMap = mapOf(
                        "user_id" to "usr-adm-01",
                        "name" to "केन्द्रीय व्यवस्थापक",
                        "phone" to "+977-9851000001",
                        "email" to "aazakobazar@gmail.com",
                        "password_hash" to "PBKDF2-SHA256:100000:SALT-VERIFIED",
                        "role" to "admin",
                        "address" to "दरबारमार्ग, काठमाडौँ",
                        "ward_number" to "वडा नं. १",
                        "created_date" to "२०२६-०१-०१"
                    )
                ),
                GenericDbRecord(
                    id = "usr-mer-02",
                    title = "सुमन श्रेष्ठ (व्यापारी)",
                    subtitle = "merchant@openbazar.com · 9841223344",
                    statusBadge = "MERCHANT (प्रमाणित)",
                    statusColor = AccentEmerald,
                    fieldsMap = mapOf(
                        "user_id" to "usr-mer-02",
                        "name" to "सुमन श्रेष्ठ",
                        "phone" to "+977-9841223344",
                        "email" to "merchant@openbazar.com",
                        "password_hash" to "PBKDF2-SHA256:100000:ENC",
                        "role" to "merchant",
                        "address" to "कालिमाटी, काठमाडौँ",
                        "ward_number" to "वडा नं. १३",
                        "created_date" to "२०२६-०१-१०"
                    )
                ),
                GenericDbRecord(
                    id = "usr-cus-03",
                    title = "रोशन अधिकारी (ग्राहक)",
                    subtitle = "customer@openbazar.com · 9801998877",
                    statusBadge = "CUSTOMER",
                    statusColor = PrimaryBlue,
                    fieldsMap = mapOf(
                        "user_id" to "usr-cus-03",
                        "name" to "रोशन अधिकारी",
                        "phone" to "+977-9801998877",
                        "email" to "customer@openbazar.com",
                        "password_hash" to "PBKDF2-SHA256:100000:ENC",
                        "role" to "customer",
                        "address" to "नयाँ बानेश्वर, काठमाडौँ",
                        "ward_number" to "वडा नं. १०",
                        "created_date" to "२०२६-०१-१५"
                    )
                )
            )
            DatabaseTableType.SHOPS -> listOf(
                GenericDbRecord(
                    id = "shp-01",
                    title = "कालिमाटी फलफूल तथा कृषि थोक मण्डी",
                    subtitle = "वडा नं. १३, काठमाडौँ · ०६:०० - २०:००",
                    statusBadge = "APPROVED (सक्रिय)",
                    statusColor = AccentEmerald,
                    fieldsMap = mapOf(
                        "shop_id" to "shp-01",
                        "owner_id" to "usr-mer-02",
                        "shop_name" to "कालिमाटी फलफूल तथा कृषि थोक मण्डी",
                        "shop_logo" to "assets/logo.png",
                        "shop_location" to "27.6983, 85.3012 (कालिमाटी)",
                        "ward_number" to "वडा नं. १३",
                        "opening_time" to "०६:०० AM",
                        "closing_time" to "०८:०० PM",
                        "status" to "सक्रिय (Live)"
                    )
                ),
                GenericDbRecord(
                    id = "shp-02",
                    title = "श्रेष्ठ खाद्यान्न तथा थोक सप्लायर्स",
                    subtitle = "वडा नं. १०, काठमाडौँ",
                    statusBadge = "VERIFIED",
                    statusColor = PrimaryBlue,
                    fieldsMap = mapOf(
                        "shop_id" to "shp-02",
                        "owner_id" to "usr-mer-05",
                        "shop_name" to "श्रेष्ठ खाद्यान्न तथा थोक सप्लायर्स",
                        "shop_logo" to "assets/logo.png",
                        "shop_location" to "27.6915, 85.3340",
                        "ward_number" to "वडा नं. १०",
                        "opening_time" to "०८:०० AM",
                        "closing_time" to "०७:०० PM",
                        "status" to "सक्रिय (Live)"
                    )
                )
            )
            DatabaseTableType.PRODUCTS -> listOf(
                GenericDbRecord(
                    id = "prd-01",
                    title = "जुम्ला अर्गानिक स्याउ (Jumla Apple)",
                    subtitle = "स्टक: ४५० केजी · रु. १६० प्रति केजी",
                    statusBadge = "IN STOCK",
                    statusColor = AccentEmerald,
                    fieldsMap = mapOf(
                        "product_id" to "prd-01",
                        "shop_id" to "shp-01",
                        "product_name" to "जुम्ला अर्गानिक स्याउ",
                        "category" to "ताजा फलफूल तथा कृषि",
                        "price" to "रु. १८०",
                        "discount_price" to "रु. १६०",
                        "stock" to "४५० केजी",
                        "product_image" to "apple.jpg",
                        "created_date" to "२०२६-०१-१२"
                    )
                ),
                GenericDbRecord(
                    id = "prd-02",
                    title = "बासमती प्रिमियम चामल (२५ केजी बोरा)",
                    subtitle = "स्टक: १२० बोरा · रु. २,४५०",
                    statusBadge = "IN STOCK",
                    statusColor = AccentEmerald,
                    fieldsMap = mapOf(
                        "product_id" to "prd-02",
                        "shop_id" to "shp-02",
                        "product_name" to "बासमती प्रिमियम चामल",
                        "category" to "किराना तथा खाद्यान्न",
                        "price" to "रु. २,६००",
                        "discount_price" to "रु. २,४५०",
                        "stock" to "१२० बोरा",
                        "product_image" to "rice.jpg",
                        "created_date" to "२०२६-०१-१५"
                    )
                )
            )
            DatabaseTableType.ORDERS -> listOf(
                GenericDbRecord(
                    id = "ord-9821",
                    title = "अर्डर #ORD-9821 · रोशन अधिकारी",
                    subtitle = "रकम: रु. ६,५०० · eSewa · डेलिभरीमा छ",
                    statusBadge = "DISPATCHED",
                    statusColor = SecondaryAmber,
                    fieldsMap = mapOf(
                        "order_id" to "ord-9821",
                        "customer_id" to "usr-cus-03",
                        "shop_id" to "shp-01",
                        "product_id" to "prd-01 (Apple), prd-02 (Rice)",
                        "quantity" to "२ थान",
                        "total_price" to "रु. ६,५००",
                        "payment_method" to "eSewa Dynamic QR",
                        "order_status" to "बाटोमा (On the way)",
                        "order_date" to "२०२६-०१-२२"
                    )
                )
            )
            DatabaseTableType.PAYMENTS -> listOf(
                GenericDbRecord(
                    id = "pay-3011",
                    title = "भुक्तानी #PAY-3011 · eSewa QR",
                    subtitle = "रु. ६,५०० · IRD लटरी पोर्टल दर्ता भएको",
                    statusBadge = "TAX VERIFIED",
                    statusColor = AccentEmerald,
                    fieldsMap = mapOf(
                        "payment_id" to "pay-3011",
                        "order_id" to "ord-9821",
                        "customer_id" to "usr-cus-03",
                        "amount" to "रु. ६,५००",
                        "payment_method" to "eSewa / Fonepay",
                        "transaction_id" to "TXN-884920198",
                        "payment_date" to "२०२६-०१-२२",
                        "ird_prize_status" to "प्रमाणित (prize.ird.gov.np)"
                    )
                )
            )
            DatabaseTableType.KHATA -> listOf(
                GenericDbRecord(
                    id = "kht-101",
                    title = "उधारो खाता: रोशन अधिकारी",
                    subtitle = "बाँकी उधारो रकम: रु. १,२०० · भाका: १५ दिन",
                    statusBadge = "DUE (उधारो)",
                    statusColor = AccentCrimson,
                    fieldsMap = mapOf(
                        "khata_id" to "kht-101",
                        "customer_id" to "usr-cus-03",
                        "shop_id" to "shp-02",
                        "amount_due" to "रु. १,२००",
                        "last_payment" to "रु. ५०० (१० दिन अघि)",
                        "due_date" to "२०२६-०२-०५"
                    )
                )
            )
            DatabaseTableType.VIDEOS -> listOf(
                GenericDbRecord(
                    id = "vid-01",
                    title = "जुम्ला स्याउ प्याकिङ तथा भण्डारण रिल्स",
                    subtitle = "हेराइ: ४,२८० · लाइक्स: ३२०",
                    statusBadge = "ACTIVE REEL",
                    statusColor = PrimaryBlue,
                    fieldsMap = mapOf(
                        "video_id" to "vid-01",
                        "shop_id" to "shp-01",
                        "product_id" to "prd-01",
                        "video_url" to "assets/reels/apple_pack.mp4",
                        "views" to "४,२८०",
                        "likes" to "३२०",
                        "upload_date" to "२०२६-०१-१८"
                    )
                )
            )
            DatabaseTableType.CHAT -> listOf(
                GenericDbRecord(
                    id = "cht-901",
                    title = "अर्डर #9821 सम्बन्धित संवाद",
                    subtitle = "प्रेषक: usr-cus-03 -> प्रापक: usr-mer-02",
                    statusBadge = "AUDITED",
                    statusColor = AccentEmerald,
                    fieldsMap = mapOf(
                        "message_id" to "cht-901",
                        "sender_id" to "usr-cus-03",
                        "receiver_id" to "usr-mer-02",
                        "message" to "नमस्ते, स्याउ कहिले सम्म आइपुग्छ होला?",
                        "message_type" to "text",
                        "time" to "१०:१५ AM"
                    )
                )
            )
        }
    }

    val filteredRecords = remember(tableRecords, searchQuery) {
        if (searchQuery.isBlank()) tableRecords
        else tableRecords.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.subtitle.contains(searchQuery, ignoreCase = true) ||
            it.id.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("database_inspector_screen"),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "केन्द्रीय डाटाबेस स्टेटमेन्ट (Database Inspector)",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "PDF Section C.4.2 · आठवटै मुख्य तालिकाहरू (8 Tables)",
                            color = SecondaryAmber,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            Toast.makeText(context, "डाटाबेस सिङ्क भयो (Database in Sync)", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = "Sync", tint = AccentEmerald)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Horizontal Table Selector Pills
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF101B2E))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(DatabaseTableType.values()) { table ->
                    val isSelected = selectedTable == table
                    Card(
                        modifier = Modifier.clickable {
                            selectedTable = table
                            searchQuery = ""
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) PrimaryBlue else DarkSurface
                        ),
                        shape = RoundedCornerShape(10.dp),
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(table.icon, fontSize = 14.sp)
                            Text(
                                text = table.displayName.split(" ")[0],
                                color = if (isSelected) Color.White else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Schema Definition Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E36)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "📋 रजिस्टर्ड स्किमा (Registered Schema Fields):",
                        color = SecondaryAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedTable.schemaFields.joinToString(", "),
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.5.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                placeholder = { Text("तालिकामा खोज्नुहोस् (Search by ID, name, or field)...", color = TextTertiary, fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            // Table Records List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredRecords) { record ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedRecordForDetail = record },
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = record.title,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(record.statusColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = record.statusBadge,
                                            color = record.statusColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.5.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = record.subtitle,
                                    color = TextSecondary,
                                    fontSize = 11.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "ID: ${record.id} · विवरण हेर्न ट्याप गर्नुहोस् ›",
                                    color = SecondaryAmber,
                                    fontSize = 10.sp
                                )
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextTertiary)
                        }
                    }
                }
            }
        }

        // Deep Relational Inspector Modal
        if (selectedRecordForDetail != null) {
            val record = selectedRecordForDetail!!
            Dialog(onDismissRequest = { selectedRecordForDetail = null }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1B2E)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "डेटाबेस रेकर्ड अडिट (Record Details)",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            IconButton(onClick = { selectedRecordForDetail = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                            }
                        }

                        HorizontalDivider(color = DarkBorder)

                        Text(record.title, color = SecondaryAmber, fontWeight = FontWeight.Bold, fontSize = 15.sp)

                        // Render key-value schema table
                        record.fieldsMap.forEach { (key, value) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(DarkSurface, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(key, color = TextTertiary, fontFamily = FontFamily.Monospace, fontSize = 10.5.sp)
                                Text(value, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                            }
                        }

                        // Special Action for Payments Table (IRD Lottery Portal)
                        if (selectedTable == DatabaseTableType.PAYMENTS) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://prize.ird.gov.np/"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("IRD करदाता उपहार पोर्टल (prize.ird.gov.np) खोल्नुहोस्", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Admin Override Action
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    Toast.makeText(context, "${record.id} अपडेट गरियो", Toast.LENGTH_SHORT).show()
                                    selectedRecordForDetail = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("प्रमाणित गर्नुहोस्", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    Toast.makeText(context, "${record.id} निलम्बित / हटाइयो", Toast.LENGTH_SHORT).show()
                                    selectedRecordForDetail = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentCrimson),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("हटाउनुहोस् (Purge)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
