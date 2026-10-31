package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
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
import com.example.ui.theme.*

/**
 * Visual Timeline Component using D3.js
 * Displays developmental history (Phases 1-6) and future milestones (Phases 7-9)
 * of the Aajako Bazar project within the engineer portfolio.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ProjectTimelineSection(
    modifier: Modifier = Modifier,
    onOpenFullscreen: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var selectedFilter by remember { mutableStateOf("all") }
    var isNepali by remember { mutableStateOf(false) }
    var isAutoTourRunning by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("d3_project_timeline_section")
    ) {
        SectionHeader(
            title = if (isNepali) "परियोजना विकास इतिहास र रणनीतिक मार्गचित्र" else "Project Evolution & Future Milestones",
            badge = "D3.js Visualization",
            subtitle = if (isNepali)
                "२०२३ को शुरुआती अवधारणा, अफलाइन इन्जिन, शून्य-विश्वास सुरक्षादेखि २०२७+ मेश नेटवर्कसम्मको यात्रा"
            else
                "Interactive D3.js timeline detailing architectural phases from 2023 genesis to 2027+ mesh networks"
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Top Action Toolbar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Aajako Bazar Roadmap",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "9 Major Strategic Phases",
                                color = SecondaryAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Auto-Tour and Language Switcher
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilledTonalButton(
                            onClick = {
                                isAutoTourRunning = !isAutoTourRunning
                                webViewInstance?.evaluateJavascript("document.getElementById('btnAutoTour')?.click();", null)
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (isAutoTourRunning) Color(0xFFEF4444) else DarkSurfaceVariant
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_timeline_auto_tour")
                        ) {
                            Icon(
                                imageVector = if (isAutoTourRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = if (isAutoTourRunning) Color.White else SecondaryAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAutoTourRunning) "Pause" else "Tour",
                                color = if (isAutoTourRunning) Color.White else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                isNepali = !isNepali
                                webViewInstance?.evaluateJavascript("document.getElementById('btnLangToggle')?.click();", null)
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
                        ) {
                            Text(
                                text = if (isNepali) "EN" else "नेपाली",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Buttons in Compose
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val filterChips = listOf(
                        "all" to if (isNepali) "सबै (All 9)" else "All Milestones (9)",
                        "history" to if (isNepali) "सम्पन्न (History 6)" else "History (6)",
                        "future" to if (isNepali) "मार्गचित्र (Future 3)" else "Roadmap (3)"
                    )

                    filterChips.forEach { (filterKey, label) ->
                        val isSelected = selectedFilter == filterKey
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedFilter = filterKey
                                webViewInstance?.evaluateJavascript(
                                    "document.querySelector('.tl-chip[data-filter=\"$filterKey\"]')?.click();",
                                    null
                                )
                            },
                            label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (filterKey == "future") Color(0xFF7C3AED) else PrimaryBlue,
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
                            modifier = Modifier.testTag("filter_chip_$filterKey")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Embedded D3.js Timeline Canvas via AndroidView
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(640.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0B1220))
                        .border(1.dp, Color(0xFF1E2D4A), RoundedCornerShape(14.dp))
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
                                            "timeline.html",
                                            "open-bazar/timeline.html"
                                        )

                                        for (cand in candidates) {
                                            try {
                                                val stream = ctx.assets.open(cand)
                                                val mime = when {
                                                    cand.endsWith(".html") -> "text/html"
                                                    cand.endsWith(".js") -> "application/javascript"
                                                    cand.endsWith(".css") -> "text/css"
                                                    else -> "application/octet-stream"
                                                }
                                                return WebResourceResponse(mime, "UTF-8", 200, "OK", mapOf("Access-Control-Allow-Origin" to "*"), stream)
                                            } catch (_: Exception) {}
                                        }
                                        return null
                                    }
                                }

                                loadUrl("https://appassets.androidplatform.net/assets/timeline.html")
                                webViewInstance = this
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("d3_timeline_webview")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Hint & Navigation Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Interactive: Drag timeline · Pinch to zoom · Tap milestone node",
                        color = TextTertiary,
                        fontSize = 11.sp
                    )

                    TextButton(
                        onClick = {
                            try {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://appassets.androidplatform.net/assets/timeline.html"))
                                context.startActivity(browserIntent)
                            } catch (_: Exception) {
                                webViewInstance?.reload()
                            }
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Full View",
                            color = PrimaryBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
