package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.BazarRepository
import com.example.model.WholesaleBusiness
import com.example.ui.components.TechChip
import com.example.ui.theme.*

@Composable
fun WholesaleDirectoryScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isNepali by BazarRepository.isNepaliLanguage.collectAsState()
    var searchCompanyQuery by remember { mutableStateOf("") }
    var selectedBusinessForRfq by remember { mutableStateOf<WholesaleBusiness?>(null) }
    var showGeneralRfqDialog by remember { mutableStateOf(false) }

    val directory = BazarRepository.wholesaleDirectory.filter { business ->
        searchCompanyQuery.isBlank() ||
                business.name.contains(searchCompanyQuery, ignoreCase = true) ||
                business.location.contains(searchCompanyQuery, ignoreCase = true) ||
                business.businessType.contains(searchCompanyQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("wholesale_directory_screen"),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Hero Header for Big Businesses & Wholesalers
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SecondaryAmber.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(SecondaryAmber, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "B2B HUB",
                                color = DarkBackground,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = if (isNepali) "ठूला व्यवसायी तथा थोक नेटवर्क" else "Enterprise Wholesale Network",
                            color = SecondaryAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isNepali)
                            "नेपालभरका प्रमाणित थोक वितरक, आयातकर्ता तथा उद्योगहरू"
                        else
                            "Verified Wholesalers, Importers & Manufacturers in Nepal",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isNepali)
                            "न्यूनतम थोक परिमाण, आधिकारिक भ्याट/प्यान बिल, सुरक्षित भुक्तानी तथा प्रत्यक्ष सम्पर्क।"
                        else
                            "Bulk orders with tax clearance, corporate escrow, and direct vendor dispatch.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { showGeneralRfqDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.RequestQuote, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isNepali) "थोक दर माग गर्नुहोस् (Request Bulk RFQ)" else "Request Bulk RFQ Quotation",
                            color = DarkBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Search Bar
        item {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = searchCompanyQuery,
                onValueChange = { searchCompanyQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = if (isNepali) "थोक व्यापारी, उद्योग वा स्थान खोज्नुहोस्..." else "Search wholesalers or locations...",
                        color = TextTertiary,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = SecondaryAmber)
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SecondaryAmber,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Listing of Verified Wholesale Businesses
        items(directory) { business ->
            WholesaleBusinessCard(
                business = business,
                isNepali = isNepali,
                onCall = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${business.phone}"))
                    try { context.startActivity(intent) } catch (e: Exception) {
                        Toast.makeText(context, "सम्पर्क: ${business.phone}", Toast.LENGTH_SHORT).show()
                    }
                },
                onRequestQuote = {
                    selectedBusinessForRfq = business
                },
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }
    }

    // RFQ Dialog
    if (showGeneralRfqDialog || selectedBusinessForRfq != null) {
        val targetCompany = selectedBusinessForRfq?.name ?: if (isNepali) "सम्पूर्ण आबद्ध थोक व्यवसायीहरू" else "All Network Wholesalers"
        RfqSubmissionDialog(
            companyName = targetCompany,
            isNepali = isNepali,
            onDismiss = {
                showGeneralRfqDialog = false
                selectedBusinessForRfq = null
            }
        )
    }
}

@Composable
fun WholesaleBusinessCard(
    business: WholesaleBusiness,
    isNepali: Boolean,
    onCall: () -> Unit,
    onRequestQuote: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = business.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    if (business.verified) {
                        Icon(Icons.Default.Verified, contentDescription = "Verified", tint = PrimaryBlue, modifier = Modifier.size(15.dp))
                    }
                }

                Box(
                    modifier = Modifier
                        .background(AccentEmerald.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "PAN: ${business.panVat}",
                        color = AccentEmerald,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = business.businessType,
                color = SecondaryAmber,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(13.dp))
                    Text(text = business.location, color = TextSecondary, fontSize = 12.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(13.dp))
                    Text(text = business.contactPerson, color = TextSecondary, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Categories
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                business.mainCategories.forEach { cat ->
                    TechChip(text = cat)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCall,
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(PrimaryBlue, PrimaryBlue))),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isNepali) "फोन गर्नुहोस्" else "Call Vendor",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onRequestQuote,
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.RequestQuote, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isNepali) "थोक दर माग" else "Request Quote",
                        color = DarkBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RfqSubmissionDialog(
    companyName: String,
    isNepali: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var itemTitle by remember { mutableStateOf("") }
    var bulkQuantity by remember { mutableStateOf("") }
    var deliveryCity by remember { mutableStateOf("काठमाडौँ (Kathmandu)") }
    var paymentTerms by remember { mutableStateOf("नगद / क्यास अन डेलिभरी (Cash on Delivery)") }
    var submitted by remember { mutableStateOf(false) }

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
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isNepali) "थोक खरिद दर माग फारम (RFQ)" else "Bulk Request for Quotation",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "प्राप्तकर्ता: $companyName",
                            color = SecondaryAmber,
                            fontSize = 12.sp
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                if (submitted) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AccentEmerald.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .border(1.dp, AccentEmerald, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = if (isNepali) "थोक माग सफलतापूर्वक पठाइयो!" else "RFQ Dispatched Successfully!",
                                color = AccentEmerald,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isNepali)
                                    "व्यापारीले २ घण्टाभित्र आधिकारिक कोटेशन तथा भ्याट बिल विवरण सहित सम्पर्क गर्नेछ।"
                                else
                                    "Vendor will respond with commercial quotation within 2 hours.",
                                color = TextPrimary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("सम्पन्न (Done)", color = DarkBackground, fontWeight = FontWeight.Bold)
                    }
                } else {
                    OutlinedTextField(
                        value = itemTitle,
                        onValueChange = { itemTitle = it },
                        label = { Text("आवश्यक सामग्री / सामान (Required Item)", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SecondaryAmber,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = bulkQuantity,
                        onValueChange = { bulkQuantity = it },
                        label = { Text("परिमाण (Bulk Quantity e.g. 50 Boxes, 5 Tons)", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SecondaryAmber,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = deliveryCity,
                        onValueChange = { deliveryCity = it },
                        label = { Text("डेलिभरी गन्तव्य (Delivery Destination)", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
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
                            if (itemTitle.isBlank() || bulkQuantity.isBlank()) {
                                Toast.makeText(context, "कृपया सामग्री र परिमाण खुलाउनुहोस्।", Toast.LENGTH_SHORT).show()
                            } else {
                                submitted = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text(
                            text = if (isNepali) "कोटेशन माग पठाउनुहोस्" else "Send RFQ Request",
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
