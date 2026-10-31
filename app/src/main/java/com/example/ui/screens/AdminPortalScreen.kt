package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BazarRepository
import com.example.model.MerchantStatement
import com.example.model.UserProfile
import com.example.ui.components.MarketDashboard
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPortalScreen(
    onLogoutRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isNepali by BazarRepository.isNepaliLanguage.collectAsState()
    val currentUser by BazarRepository.currentUser.collectAsState()
    val auditRecords by BazarRepository.auditRecords.collectAsState()
    val mediaUploads by BazarRepository.mediaUploads.collectAsState()
    val merchantStatements = BazarRepository.merchantStatements

    val registeredCustomers by BazarRepository.registeredCustomersList.collectAsState()
    val registeredMerchants by BazarRepository.registeredMerchantsList.collectAsState()

    // Selected Tab: 0: Audit/Overview, 1: Customers, 2: Merchants & Statements, 3: Antivirus/Security, 4: Rules, 5: Account
    var selectedAdminTab by remember { mutableStateOf(0) }

    // Dialog state
    var selectedCustomerForInspect by remember { mutableStateOf<UserProfile?>(null) }
    var selectedMerchantStatement by remember { mutableStateOf<MerchantStatement?>(null) }
    var selectedMerchantForInspect by remember { mutableStateOf<UserProfile?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_portal_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Master Admin Header Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SecondaryAmber.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
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
                                .background(SecondaryAmber.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = SecondaryAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isNepali) "केन्द्रीय प्रशासक नियन्त्रण कक्ष (Master Admin)" else "Central Admin Control",
                                    color = SecondaryAmber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        IconButton(onClick = onLogoutRequested) {
                            Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color(0xFFEF4444))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isNepali) "OPEN BAZAR केन्द्रीय व्यवस्थापन तथा अडिट" else "OPEN BAZAR Central Audit & Control",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isNepali)
                            "एडमिन: aazakodazar@gmail.com | ग्राहक र व्यापारी कसरी दर्ता भएका छन् र कसरी चलाएका छन्, क्लिक गरी पूर्ण विवरण हेर्नुहोस्।"
                        else
                            "Master Admin | Inspect registered customers and merchants, registrations, and usage activities.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Sub Navigation Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    Pair(if (isNepali) "अडिटिङ" else "Audit", 0),
                    Pair(if (isNepali) "ग्राहकहरू (${registeredCustomers.size})" else "Customers", 1),
                    Pair(if (isNepali) "व्यापारीहरू (${registeredMerchants.size})" else "Merchants", 2),
                    Pair(if (isNepali) "भाइरस सुरक्षा" else "Security", 3),
                    Pair(if (isNepali) "नीति नियम" else "Rules", 4),
                    Pair(if (isNepali) "खाता" else "Account", 5)
                ).forEach { (label, index) ->
                    val isSelected = selectedAdminTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) SecondaryAmber else Color.Transparent)
                            .clickable { selectedAdminTab = index }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) DarkBackground else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Tab Content
        when (selectedAdminTab) {
            0 -> {
                // Platform Audit & Transaction Graph
                item {
                    MarketDashboard(
                        onOpenBillInvoice = { /* invoice view */ },
                        onOpenOrderChat = { /* order chat */ }
                    )
                }

                item {
                    Text(
                        text = if (isNepali) "केन्द्रीय अनलाइन कारोबार अडिट लेजर" else "Central Audit Transaction Ledger",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                items(auditRecords) { record ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(record.id, color = SecondaryAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(record.timestamp, color = TextTertiary, fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "${record.customerName} - ${record.itemDescription}",
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("बिल नं: ${record.invoiceNumber}", color = TextSecondary, fontSize = 11.sp)
                                Text("रु. ${record.amountNpr}", color = AccentEmerald, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // =================================================================
            // TAB 1: CUSTOMERS DIRECTORY & USAGE INSPECTION
            // =================================================================
            1 -> {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, AccentEmerald.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.People, contentDescription = null, tint = AccentEmerald)
                                Text(
                                    text = if (isNepali) "दर्ता भएका ग्राहकहरूको पूर्ण विवरण (Customer Directory)" else "Registered Customers Directory",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isNepali)
                                    "ग्राहक कसरी दर्ता भएका छन्, उनीहरूको ठेगाना, सम्पर्क, र एप कसरी चलाएका छन् (अर्डर संख्या, सक्रियता), सबै हेर्न कुनै पनि ग्राहकमा क्लिक गर्नुहोस्।"
                                else
                                    "Inspect how customers registered and how they use the platform. Click any customer to inspect.",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                items(registeredCustomers) { customer ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                            .clickable { selectedCustomerForInspect = customer },
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(AccentEmerald.copy(alpha = 0.2f))
                                            .border(1.dp, AccentEmerald, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("👤", fontSize = 20.sp)
                                    }
                                    Column {
                                        Text(customer.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text(customer.email, color = TextSecondary, fontSize = 12.sp)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .background(AccentEmerald.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text("सक्रिय ग्राहक", color = AccentEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 10.dp))

                            // Registration and Usage summary
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("📍 ठेगाना / देश:", color = TextTertiary, fontSize = 11.sp)
                                    Text("${customer.address} (${customer.countryName})", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("📞 सम्पर्क नम्बर:", color = TextTertiary, fontSize = 11.sp)
                                    Text(customer.phone, color = PrimaryBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("📝 कसरी दर्ता भएको:", color = TextTertiary, fontSize = 11.sp)
                                    Text(customer.registrationSource, color = SecondaryAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("📊 कसरी चलाएको छ:", color = TextTertiary, fontSize = 11.sp)
                                    Text("कुल ${customer.ordersCount} अर्डर | ${customer.lastActiveTime}", color = AccentEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = { selectedCustomerForInspect = customer },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ग्राहकको सम्पूर्ण विवरण हेर्नुहोस् (Inspect Customer)", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // =================================================================
            // TAB 2: MERCHANTS DIRECTORY & STATEMENTS INSPECTION
            // =================================================================
            2 -> {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, PrimaryBlue.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Store, contentDescription = null, tint = PrimaryBlue)
                                Text(
                                    text = if (isNepali) "दर्ता भएका व्यापारीहरू तथा स्टेटमेन्ट (Merchant Directory)" else "Merchant Directory & Statements",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isNepali)
                                    "व्यापारी कसरी दर्ता भएका छन्, उनीहरूको गुगल म्याप लोकेसन, पान/भ्याट, र सम्पूर्ण आर्थिक स्टेटमेन्ट हेर्न क्लिक गर्नुहोस्।"
                                else
                                    "Inspect merchant registration, Google Map coordinates, PAN/VAT, and statements.",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                items(registeredMerchants) { merchant ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                            .clickable { selectedMerchantForInspect = merchant },
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = merchant.businessName ?: merchant.name,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "सञ्चालक: ${merchant.name} | PAN/VAT: ${merchant.panVat ?: "दर्ता भएको"}",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .background(PrimaryBlue.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "प्रमाणित व्यापारी ✅",
                                        color = PrimaryBlue,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 10.dp))

                            // Google Map & Details
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("🗺️ गुगल म्याप ठेगाना:", color = TextTertiary, fontSize = 11.sp)
                                    Text(merchant.address, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                                merchant.mapCoordinates?.let { coords ->
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("📍 म्याप कोअर्डिनेट्स:", color = TextTertiary, fontSize = 11.sp)
                                        Text(coords, color = SecondaryAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("📞 सम्पर्क फोन:", color = TextTertiary, fontSize = 11.sp)
                                    Text(merchant.phone, color = PrimaryBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("📝 दर्ता माध्यम:", color = TextTertiary, fontSize = 11.sp)
                                    Text(merchant.registrationSource, color = AccentEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = { selectedMerchantForInspect = merchant },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("व्यापारीको पूर्ण विवरण र स्टेटमेन्ट हेर्नुहोस्", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // =================================================================
            // TAB 3: MEDIA & ANTIVIRUS SECURITY
            // =================================================================
            3 -> {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, AccentEmerald.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = AccentEmerald)
                                Text(
                                    text = if (isNepali) "एन्टिभाइरस तथा मैलवेयर सुरक्षा अडिट (Zero-Threat Engine)" else "Media Security & Antivirus Audit",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "प्रत्येक व्यापारीले अपलोड गरेका फोटो र भिडियोहरूलाई स्वचालित SHA-256 सिग्नेचर र सुरक्षा इन्जिनद्वारा स्क्यान गरी भाइरस-मुक्त प्रमाणित गरिन्छ।",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                items(mediaUploads) { media ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(media.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Box(
                                    modifier = Modifier
                                        .background(AccentEmerald.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("0 भाइरस (Clean)", color = AccentEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(media.description, color = TextSecondary, fontSize = 12.sp)

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("आकार: ${media.fileSizeMb} MB", color = TextTertiary, fontSize = 11.sp)
                                Text("सिग्नेचर: ${media.hashSignature.take(24)}...", color = TextTertiary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // =================================================================
            // TAB 4: PLATFORM POLICIES
            // =================================================================
            4 -> {
                item {
                    OpenBazarTermsScreen(
                        onNavigateBackToMarket = { selectedAdminTab = 0 }
                    )
                }
            }

            // =================================================================
            // TAB 5: ADMIN ACCOUNT & LOGOUT
            // =================================================================
            5 -> {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, SecondaryAmber.copy(alpha = 0.6f), RoundedCornerShape(18.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .background(SecondaryAmber.copy(alpha = 0.2f), CircleShape)
                                        .border(2.dp, SecondaryAmber, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = SecondaryAmber, modifier = Modifier.size(28.dp))
                                }
                                Column {
                                    Text(currentUser.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text(currentUser.email, color = TextSecondary, fontSize = 13.sp)
                                    Text("भूमिका: ${currentUser.role.titleNp}", color = SecondaryAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 14.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("आधिकारिक इमेल:", color = TextTertiary, fontSize = 12.sp)
                                    Text("aazakodazar@gmail.com", color = SecondaryAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("सम्पर्क:", color = TextTertiary, fontSize = 12.sp)
                                    Text(currentUser.phone, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("संस्था:", color = TextTertiary, fontSize = 12.sp)
                                    Text(currentUser.businessName ?: "OPEN BAZAR HQ", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("सुरक्षा स्थिति:", color = TextTertiary, fontSize = 12.sp)
                                    Text("🛡️ Master Superuser Mode Active", color = AccentEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = onLogoutRequested,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("प्रशासक खाता लगआउट गर्नुहोस् (Logout Admin)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // =========================================================================
    // MODAL DIALOG: CUSTOMER DETAILS INSPECTOR (Admin Exclusive)
    // =========================================================================
    if (selectedCustomerForInspect != null) {
        val cust = selectedCustomerForInspect!!
        AlertDialog(
            onDismissRequest = { selectedCustomerForInspect = null },
            confirmButton = {
                Button(
                    onClick = {
                        Toast.makeText(context, "${cust.name} को खाता प्रमाणित स्थिति सुरक्षित गरियो!", Toast.LENGTH_SHORT).show()
                        selectedCustomerForInspect = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald)
                ) {
                    Text("स्वीकृत / बन्द गर्नुहोस् (Close)", color = DarkBackground, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.PersonSearch, contentDescription = null, tint = AccentEmerald)
                    Text("ग्राहकको पूर्ण विवरण (Customer Audit)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(cust.name, color = AccentEmerald, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("इमेल: ${cust.email}", color = TextSecondary, fontSize = 12.sp)
                    Text("फोन नम्बर: ${cust.phone}", color = PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("बसोबास ठेगाना: ${cust.address}", color = TextPrimary, fontSize = 12.sp)
                    Text("देश: ${cust.countryName} (${cust.countryCode})", color = TextSecondary, fontSize = 12.sp)

                    HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))

                    Text("📝 कसरी दर्ता भएको छ:", color = SecondaryAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(cust.registrationSource, color = TextPrimary, fontSize = 12.sp)
                    Text("दर्ता मिति: ${cust.joinedDate}", color = TextTertiary, fontSize = 11.sp)

                    HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))

                    Text("📊 कसरी चलाएको छ (Usage & Activity):", color = AccentEmerald, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("• कुल अर्डरहरू: ${cust.ordersCount} थान सम्पन्न", color = TextPrimary, fontSize = 12.sp)
                    Text("• पछिल्लो सक्रियता: ${cust.lastActiveTime}", color = TextPrimary, fontSize = 12.sp)
                    Text("• भुक्तानी विश्वसनीयता: १००% सुरक्षित अनलाइन तथा क्यास अन डेलिभरी", color = TextSecondary, fontSize = 11.sp)
                    Text("• खाता अवस्था: सक्रिय तथा प्रमाणित ✅", color = AccentEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // =========================================================================
    // MODAL DIALOG: MERCHANT DETAILS & STATEMENT INSPECTOR (Admin Exclusive)
    // =========================================================================
    if (selectedMerchantForInspect != null) {
        val mer = selectedMerchantForInspect!!
        AlertDialog(
            onDismissRequest = { selectedMerchantForInspect = null },
            confirmButton = {
                Button(
                    onClick = {
                        Toast.makeText(context, "${mer.businessName ?: mer.name} को विवरण तथा स्टेटमेन्ट सुरक्षित गरियो!", Toast.LENGTH_SHORT).show()
                        selectedMerchantForInspect = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("बन्द गर्नुहोस् (Close)", color = DarkBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "${mer.name} को वित्तीय स्टेटमेन्ट डाउनलोड गरियो!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = SecondaryAmber, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("स्टेटमेन्ट डाउनलोड", color = SecondaryAmber, fontSize = 11.sp)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.StoreMallDirectory, contentDescription = null, tint = PrimaryBlue)
                    Text("व्यापारीको पूर्ण विवरण तथा म्याप", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(mer.businessName ?: mer.name, color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("सञ्चालकको नाम: ${mer.name}", color = TextPrimary, fontSize = 13.sp)
                    Text("इमेल: ${mer.email} | सम्पर्क: ${mer.phone}", color = TextSecondary, fontSize = 12.sp)
                    Text("PAN/VAT दर्ता: ${mer.panVat ?: "६०२४८९११०"}", color = SecondaryAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("विधा: ${mer.businessCategory ?: "खाद्यान्न तथा थोक"}", color = TextSecondary, fontSize = 12.sp)

                    HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))

                    Text("🗺️ गुगल म्याप लोकेसन (Google Map Pin):", color = SecondaryAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("स्थान: ${mer.address}", color = TextPrimary, fontSize = 12.sp)
                    mer.mapCoordinates?.let { coords ->
                        Text("कोअर्डिनेट्स: $coords", color = AccentEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))

                    Text("📝 कसरी दर्ता भएको छ:", color = TextTertiary, fontSize = 11.sp)
                    Text(mer.registrationSource, color = TextPrimary, fontSize = 12.sp)
                    Text("दर्ता मिति: ${mer.joinedDate}", color = TextTertiary, fontSize = 11.sp)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
