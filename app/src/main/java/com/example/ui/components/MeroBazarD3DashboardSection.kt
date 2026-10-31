package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewAssetLoader
import com.example.data.BazarRepository
import com.example.ui.theme.*

/**
 * Visual Dashboard Component using D3.js
 * Displays key metrics from the Mero Bazar project:
 * - User Growth Trends (Shoppers, Verified Merchants, Farmers)
 * - Transaction Volume Trends (Daily & Monthly GMV in रु / NPR)
 * - Order Velocity & Completion Rates
 * - Regional Distribution across Nepal's 7 Provinces
 * - Payment Gateway Breakdown (eSewa, Fonepay QR, Khalti, Digital Khata)
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MeroBazarD3DashboardSection(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAppNepali by BazarRepository.isNepaliLanguage.collectAsState()
    var isNepali by remember(isAppNepali) { mutableStateOf(isAppNepali) }
    var selectedMetric by remember { mutableStateOf("gmv") }
    var selectedRange by remember { mutableStateOf("30d") }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("mero_bazar_d3_dashboard_section")
    ) {
        // Section Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top Title & Badges
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SecondaryAmber.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = "D3 Analytics",
                                tint = SecondaryAmber,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isNepali) "मेरो बजार D3.js भिजुअल ड्यासबोर्ड" else "Mero Bazar D3.js Dashboard",
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
                                    "प्रयोगकर्ता वृद्धि, कारोबार (GMV), ७ प्रदेशको हिस्सा र भुक्तानी ट्रेन्ड"
                                else
                                    "User growth, GMV volume, 7 provinces distribution & payment splits",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Quick Action Tools
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Language Toggle
                        OutlinedButton(
                            onClick = {
                                isNepali = !isNepali
                                webViewInstance?.evaluateJavascript(
                                    "if (window.MeroBazarD3Dashboard) MeroBazarD3Dashboard.toggleLanguage();",
                                    null
                                )
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_d3_dash_lang_toggle")
                        ) {
                            Text(
                                text = if (isNepali) "EN" else "नेपाली",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Export CSV
                        FilledTonalButton(
                            onClick = {
                                webViewInstance?.evaluateJavascript(
                                    "if (window.MeroBazarD3Dashboard) MeroBazarD3Dashboard.exportCsv();",
                                    null
                                )
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = DarkSurfaceVariant),
                            modifier = Modifier.testTag("btn_d3_dash_export_csv")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "CSV",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "CSV",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Save Chart PNG
                        Button(
                            onClick = {
                                webViewInstance?.evaluateJavascript(
                                    "if (window.MeroBazarD3Dashboard) MeroBazarD3Dashboard.exportPng();",
                                    null
                                )
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber),
                            modifier = Modifier.testTag("btn_d3_dash_export_png")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "PNG",
                                tint = DarkBackground,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "PNG",
                                color = DarkBackground,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Metric Selection Chips
                val metrics = listOf(
                    "gmv" to (if (isNepali) "कारोबार (GMV)" else "Transaction GMV"),
                    "users" to (if (isNepali) "सक्रिय प्रयोगकर्ता" else "Active Users"),
                    "orders" to (if (isNepali) "अर्डर संख्या" else "Order Velocity"),
                    "aov" to (if (isNepali) "औसत रकम (AOV)" else "Avg Order Value")
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(metrics) { (key, label) ->
                        val isSelected = selectedMetric == key
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedMetric = key
                                webViewInstance?.evaluateJavascript(
                                    "if (window.MeroBazarD3Dashboard) MeroBazarD3Dashboard.setMetric('$key');",
                                    null
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SecondaryAmber,
                                selectedLabelColor = DarkBackground,
                                containerColor = DarkSurfaceVariant,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = DarkBorder,
                                selectedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier.testTag("metric_chip_$key")
                        )
                    }
                }

                // Timeframe Selection Chips
                val timeframes = listOf(
                    "7d" to (if (isNepali) "७ दिन" else "7 Days"),
                    "30d" to (if (isNepali) "३० दिन" else "30 Days"),
                    "90d" to (if (isNepali) "९० दिन" else "90 Days"),
                    "1y" to (if (isNepali) "१ वर्ष" else "1 Year")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    timeframes.forEach { (rangeKey, label) ->
                        val isSelected = selectedRange == rangeKey
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedRange = rangeKey
                                webViewInstance?.evaluateJavascript(
                                    "if (window.MeroBazarD3Dashboard) MeroBazarD3Dashboard.setRange('$rangeKey');",
                                    null
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlue,
                                selectedLabelColor = Color.White,
                                containerColor = DarkSurfaceVariant,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = DarkBorder,
                                selectedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier.testTag("range_chip_$rangeKey")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Embedded D3.js Visual Dashboard Canvas via AndroidView
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(680.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0B1220))
                .border(1.dp, Color(0xFF1E2D4A), RoundedCornerShape(16.dp))
        ) {
            AndroidView(
                factory = { ctx ->
                    val assetLoader = WebViewAssetLoader.Builder()
                        .setDomain("appassets.androidplatform.net")
                        .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(ctx))
                        .addPathHandler("/assets/open-bazar/", WebViewAssetLoader.AssetsPathHandler(ctx))
                        .build()

                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            allowFileAccess = true
                            allowContentAccess = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            cacheMode = WebSettings.LOAD_DEFAULT
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        }

                        setBackgroundColor(0xFF0B1220.toInt())

                        webViewClient = object : WebViewClient() {
                            override fun shouldInterceptRequest(
                                view: WebView,
                                request: WebResourceRequest
                            ): WebResourceResponse? {
                                val loaderResp = assetLoader.shouldInterceptRequest(request.url)
                                if (loaderResp != null) return loaderResp

                                val path = request.url.path?.removePrefix("/") ?: ""
                                val candidates = listOf(
                                    path,
                                    path.removePrefix("assets/"),
                                    "open-bazar/" + path.removePrefix("assets/"),
                                    "d3-dashboard.html",
                                    "open-bazar/d3-dashboard.html"
                                )

                                for (cand in candidates) {
                                    try {
                                        val stream = ctx.assets.open(cand)
                                        val mime = when {
                                            cand.endsWith(".html") -> "text/html"
                                            cand.endsWith(".css") -> "text/css"
                                            cand.endsWith(".js") -> "application/javascript"
                                            cand.endsWith(".svg") -> "image/svg+xml"
                                            else -> "application/octet-stream"
                                        }
                                        return WebResourceResponse(
                                            mime, "UTF-8", 200, "OK",
                                            mapOf("Access-Control-Allow-Origin" to "*"), stream
                                        )
                                    } catch (_: Exception) {}
                                }
                                return null
                            }
                        }

                        loadUrl("https://appassets.androidplatform.net/assets/d3-dashboard.html")
                        webViewInstance = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
