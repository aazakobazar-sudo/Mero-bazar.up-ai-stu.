package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BazarRepository
import com.example.model.DailyVolumeData
import com.example.model.DigitalInvoice
import com.example.model.OrderStatus
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

/**
 * MarketDashboard Composable
 * Provides a comprehensive summary view of:
 * 1. Active market trends (daily GMV, growth rate, top categories)
 * 2. Visual Canvas chart of daily transaction volumes
 * 3. Pending orders with one-tap "Approve & Auto-generate Bill" action
 * 4. High-growth vs Declining items analysis (Requirement 6)
 * 5. Complete Online Transaction Audit Ledger (Requirement 6)
 */
@Composable
fun MarketDashboard(
    onOpenBillInvoice: (DigitalInvoice) -> Unit,
    onOpenOrderChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isNepali by BazarRepository.isNepaliLanguage.collectAsState()
    val ordersList by BazarRepository.orders.collectAsState()
    val productsList by BazarRepository.products.collectAsState()
    val auditRecords by BazarRepository.auditRecords.collectAsState()

    val pendingOrders = remember(ordersList) {
        ordersList.filter { it.status == OrderStatus.PENDING }
    }

    val dailyVolumes = BazarRepository.dailyTransactionVolumes

    // High growth items vs declining items
    val highGrowthProducts = remember(productsList) {
        productsList.filter { it.salesGrowthPercent > 0 }.sortedByDescending { it.salesGrowthPercent }
    }
    val decliningProducts = remember(productsList) {
        productsList.filter { it.salesGrowthPercent <= 0 }.sortedBy { it.salesGrowthPercent }
    }

    var showD3MetricsDashboard by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("market_dashboard"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isNepali) "बजार ड्यासबोर्ड तथा अडिटिङ" else "Market Dashboard & Auditing",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = if (isNepali)
                        "सक्रिय कारोबार, दैनिक कारोबार चार्ट र अडिटिङ प्रणाली"
                    else
                        "Active market trends, transaction charts, and audit statements",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .background(AccentEmerald.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(6.dp).background(AccentEmerald, CircleShape))
                    Text(
                        text = if (isNepali) "प्रत्यक्ष डेटा" else "LIVE",
                        color = AccentEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Summary KPI Metrics 4-Box Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DashboardKpiCard(
                title = if (isNepali) "दैनिक कारोबार" else "Daily Volume",
                value = "रु. ३.१ लाख",
                subtitle = "+१८.५% वृद्धि",
                color = PrimaryBlue,
                modifier = Modifier.weight(1f)
            )
            DashboardKpiCard(
                title = if (isNepali) "बाँकी अर्डर" else "Pending Orders",
                value = "${pendingOrders.size} अर्डर",
                subtitle = "तत्काल स्वीकृत चाहिने",
                color = SecondaryAmber,
                modifier = Modifier.weight(1f)
            )
            DashboardKpiCard(
                title = if (isNepali) "कुल अडिट रेकर्ड" else "Audit Records",
                value = "${auditRecords.size} रेकर्ड",
                subtitle = "१००% सुरक्षित",
                color = AccentEmerald,
                modifier = Modifier.weight(1f)
            )
        }

        // 1. Transaction Volumes Chart (Daily Visualization)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isNepali) "दैनिक कारोबार ग्राफ (७ दिन)" else "Daily Transaction Volume Chart",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (isNepali) "रकम (रु.) अनुसार दैनिक उतारचढाव" else "Daily revenue trend in NPR",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(PrimaryBlue.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "रु. १.६० करोड / महिना",
                            color = PrimaryBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // High-performance Compose Canvas Chart
                DailyVolumeCanvasChart(
                    data = dailyVolumes,
                    isNepali = isNepali,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Day Legend Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    dailyVolumes.forEach { d ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isNepali) d.dayNp.take(3) else d.dayEn,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${d.volumeNpr / 1000}k",
                                color = TextPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // D3.js Interactive Metrics Dashboard Expandable Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SecondaryAmber.copy(alpha = 0.4f), RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(SecondaryAmber.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = "D3.js Dashboard",
                                tint = SecondaryAmber,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isNepali) "D3.js म्याक्रो भिजुअल ड्यासबोर्ड" else "D3.js Metrics Dashboard",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .background(AccentEmerald.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "D3.js v7",
                                        color = AccentEmerald,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = if (isNepali)
                                    "प्रयोगकर्ता वृद्धि (१.४८L+), ३० दिने कारोबार (GMV), ७ प्रदेशको नक्सा"
                                else
                                    "User growth (148k+), 30-day GMV, 7 provinces distribution",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = { showD3MetricsDashboard = !showD3MetricsDashboard },
                        modifier = Modifier.testTag("btn_toggle_d3_metrics_dashboard")
                    ) {
                        Icon(
                            imageVector = if (showD3MetricsDashboard) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle D3 Dashboard",
                            tint = SecondaryAmber
                        )
                    }
                }

                AnimatedVisibility(visible = showD3MetricsDashboard) {
                    Column(modifier = Modifier.padding(top = 14.dp)) {
                        MeroBazarD3DashboardSection(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }

        // 2. Pending Orders with Automated Bill Generator (Requirement 4)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    if (pendingOrders.isNotEmpty()) SecondaryAmber.copy(alpha = 0.5f) else DarkBorder,
                    RoundedCornerShape(18.dp)
                ),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.HourglassTop, contentDescription = null, tint = SecondaryAmber, modifier = Modifier.size(18.dp))
                        Text(
                            text = if (isNepali)
                                "प्रतीक्षारत अर्डरहरू (${pendingOrders.size})"
                            else
                                "Pending Orders (${pendingOrders.size})",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Text(
                        text = if (isNepali) "सामान ओके गरेपछि बिल बन्ने" else "Auto-bills upon approval",
                        color = SecondaryAmber,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (pendingOrders.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkBackground, RoundedCornerShape(10.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isNepali) "हाल कुनै बाँकी अर्डर छैन। सबै स्वीकृत भइसके।" else "No pending orders waiting for approval.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        pendingOrders.forEach { order ->
                            PendingOrderApprovalCard(
                                order = order,
                                isNepali = isNepali,
                                onApproveAndGenerateBill = {
                                    val invoice = BazarRepository.approveOrderAndGenerateBill(order.id)
                                    if (invoice != null) {
                                        Toast.makeText(context, "बिल नं: ${invoice.invoiceNumber} जारी भयो!", Toast.LENGTH_SHORT).show()
                                        onOpenBillInvoice(invoice)
                                    }
                                },
                                onOpenChat = { onOpenOrderChat(order.id) }
                            )
                        }
                    }
                }
            }
        }

        // 3. Growth & Decline Auditing Analysis (Requirement 6)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = if (isNepali) "व्यापार वृद्धि तथा विश्लेषण (Growth & Decline Audit)" else "Sales Growth & Trend Audit",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = if (isNepali)
                        "कुन सामान धेरै ग्रो भएको छ र कुन सामान घट्दै आएको छ हेर्नुहोस्"
                    else
                        "Identify top-performing high growth products vs declining items",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // High Growth Items
                Text(
                    text = if (isNepali) "★ उच्च व्यापार वृद्धि भएका सामानहरू (High Growth):" else "High Growth Products:",
                    color = AccentEmerald,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                highGrowthProducts.take(3).forEach { prod ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isNepali) prod.titleNp else prod.titleEn,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "बिक्री: ${prod.monthlySoldUnits} ${prod.unit} / महिना",
                                color = TextTertiary,
                                fontSize = 11.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(AccentEmerald.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "+${prod.salesGrowthPercent}%",
                                color = AccentEmerald,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                if (decliningProducts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isNepali) "▼ घट्दो क्रममा रहेका सामानहरू (Declining / Seasonal):" else "Declining Items:",
                        color = Color(0xFFF87171),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    decliningProducts.forEach { prod ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isNepali) prod.titleNp else prod.titleEn,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "बिक्री: ${prod.monthlySoldUnits} ${prod.unit} / महिना",
                                    color = TextTertiary,
                                    fontSize = 11.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(Color(0x33EF4444), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${prod.salesGrowthPercent}%",
                                    color = Color(0xFFF87171),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Online Transaction Auditing System (Requirement 6)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isNepali) "अनलाइन कारोबार अडिटिङ लेजर" else "Online Transaction Audit Ledger",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (isNepali) "प्रत्येक कारोबारको पूर्ण रेकर्ड तथा नाफा मार्जिन" else "Complete ledger of every sale & profit margin",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(onClick = {
                        Toast.makeText(context, "अडिट स्टेटमेन्ट डाउनलोड गरियो (PDF Exported)", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Download, contentDescription = "Export Audit", tint = PrimaryBlue)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                auditRecords.take(4).forEach { record ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(1.dp, DarkBorder, RoundedCornerShape(10.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkBackground)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${record.itemDescription} • ${record.customerName}",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                                Text(
                                    text = "बिल: ${record.invoiceNumber} | ${record.timestamp}",
                                    color = TextTertiary,
                                    fontSize = 10.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "रु. ${NumberFormat.getNumberInstance(Locale.US).format(record.amountNpr)}",
                                    color = AccentEmerald,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "नाफा: रु. ${record.profitMarginNpr}",
                                    color = SecondaryAmber,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardKpiCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, color = TextSecondary, fontSize = 11.sp, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = TextTertiary, fontSize = 10.sp, maxLines = 1)
        }
    }
}

@Composable
fun DailyVolumeCanvasChart(
    data: List<DailyVolumeData>,
    isNepali: Boolean,
    modifier: Modifier = Modifier
) {
    val maxVolume = remember(data) { (data.maxOfOrNull { it.volumeNpr } ?: 350000).toFloat() }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val barWidth = width / (data.size * 2f)

        // Draw horizontal grid lines
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = height * (i.toFloat() / gridLines)
            drawLine(
                color = Color(0xFF1E293B),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
        }

        // Draw Bars with smooth rounded corners and gradient
        data.forEachIndexed { index, item ->
            val barHeight = (item.volumeNpr / maxVolume) * (height - 20f)
            val x = (index * 2f + 0.5f) * barWidth
            val y = height - barHeight

            // Gradient Brush for Bar
            val barBrush = Brush.verticalGradient(
                colors = listOf(
                    PrimaryBlue,
                    SecondaryAmber.copy(alpha = 0.8f)
                ),
                startY = y,
                endY = height
            )

            drawRoundRect(
                brush = barBrush,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(8f, 8f)
            )
        }
    }
}

@Composable
fun PendingOrderApprovalCard(
    order: com.example.model.OrderRecord,
    isNepali: Boolean,
    onApproveAndGenerateBill: () -> Unit,
    onOpenChat: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SecondaryAmber.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkBackground)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = order.id, color = SecondaryAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(
                    text = "रु. ${NumberFormat.getNumberInstance(Locale.US).format(order.totalAmount)}",
                    color = AccentEmerald,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "ग्राहक: ${order.customerName} (${order.customerPhone})", color = TextPrimary, fontSize = 12.sp)
            Text(text = "सामान: ${order.items.joinToString { "${it.title} (x${it.quantity})" }}", color = TextSecondary, fontSize = 11.sp)

            Spacer(modifier = Modifier.height(10.dp))

            // Action Row: Chat with Customer & Approve Order with Bill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenChat,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isNepali) "च्याट (Chat)" else "Chat",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = onApproveAndGenerateBill,
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isNepali) "सामान ओके र बिल जारी" else "Approve & Bill",
                        color = DarkBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
