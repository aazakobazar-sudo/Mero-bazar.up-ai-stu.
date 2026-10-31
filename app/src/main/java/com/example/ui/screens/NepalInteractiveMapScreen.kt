package com.example.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.webkit.WebViewAssetLoader
import com.example.data.BazarRepository
import com.example.ui.theme.*

/**
 * Real Nepal Interactive Map Screen
 * Implements a fully interactive OpenStreetMap & Leaflet mapping system for Nepal:
 * - Real GPS geolocation with permission handling
 * - Tapping places live Red Pin (🔴 रातो पिन) with exact coordinates
 * - Search by city, market, or district
 * - Radius filtering: 5km, 15km, 50km, All-Nepal
 * - Pre-indexed market hubs across all 7 provinces
 * - Configured with primary handling email: aazakobazar@gmail.com
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NepalInteractiveMapScreen(
    onBack: () -> Unit,
    onLocationSelected: ((lat: String, lng: String, address: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isNepali by BazarRepository.isNepaliLanguage.collectAsState()

    var selectedCityKey by remember { mutableStateOf("kathmandu") }
    var selectedRadiusKm by remember { mutableStateOf(50) }
    var currentCoords by remember { mutableStateOf("27.6983° N, 85.3012° E") }
    var currentLocationName by remember { mutableStateOf("कालिमाटी फलफूल तथा तरकारी बजार, काठमाडौँ") }
    var rawLat by remember { mutableStateOf("27.6983") }
    var rawLng by remember { mutableStateOf("85.3012") }

    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    // Native GPS Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            try {
                val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val loc: Location? = if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                } else null

                if (loc != null) {
                    val lat = loc.latitude
                    val lng = loc.longitude
                    rawLat = lat.toString()
                    rawLng = lng.toString()
                    currentCoords = "${String.format("%.4f", lat)}° N, ${String.format("%.4f", lng)}° E"
                    currentLocationName = "मेरो प्रत्यक्ष GPS लोकेसन (Device GPS)"
                    webViewInstance?.evaluateJavascript(
                        "updateSelectedLocation($lat, $lng, 'मेरो प्रत्यक्ष GPS लोकेसन'); map.flyTo([$lat, $lng], 15);",
                        null
                    )
                    Toast.makeText(context, "📍 तपाईंको प्रत्यक्ष GPS लोकेसन प्राप्त भयो!", Toast.LENGTH_SHORT).show()
                } else {
                    webViewInstance?.evaluateJavascript("locateUserGps();", null)
                }
            } catch (e: Exception) {
                webViewInstance?.evaluateJavascript("locateUserGps();", null)
            }
        } else {
            Toast.makeText(context, "लोकेसन अनुमति दिइएन। कृपया नक्सामा ट्याप गरी रातो पिन छान्नुहोस्।", Toast.LENGTH_LONG).show()
        }
    }

    val nepalCities = listOf(
        "kathmandu" to "काठमाडौँ",
        "pokhara" to "पोखरा",
        "chitwan" to "चितवन",
        "biratnagar" to "विराटनगर",
        "butwal" to "बुटवल",
        "dharan" to "धरान",
        "nepalgunj" to "नेपालगन्ज",
        "dhangadhi" to "धनगढी",
        "surkhet" to "सुर्खेत",
        "janakpur" to "जनकपुर",
        "ilam" to "इलाम"
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("nepal_interactive_map_screen"),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_map_back")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (isNepali) "नेपालको बजार नक्सा" else "Nepal Live Market Map",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFDC2626), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "🔴 रातो पिन",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = "केन्द्रीय सम्पर्क: aazakobazar@gmail.com",
                            color = SecondaryAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                actions = {
                    // Quick email contact button
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:aazakobazar@gmail.com")
                                putExtra(Intent.EXTRA_SUBJECT, "Open Bazar Nepal Map Inquiry")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                Toast.makeText(context, "aazakobazar@gmail.com", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.testTag("btn_map_email_admin")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email Support",
                            tint = SecondaryAmber
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B1220))
            )
        },
        containerColor = Color(0xFF0B1220)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Leaflet & OpenStreetMap Canvas via WebView
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
                            setGeolocationEnabled(true)
                            cacheMode = WebSettings.LOAD_DEFAULT
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        }

                        setBackgroundColor(0xFF0B1220.toInt())

                        // Register Native Bridge to receive location updates from Leaflet map
                        addJavascriptInterface(object {
                            @JavascriptInterface
                            fun onLocationPreview(lat: Double, lng: Double, address: String) {
                                post {
                                    rawLat = lat.toString()
                                    rawLng = lng.toString()
                                    currentCoords = "${String.format("%.4f", lat)}° N, ${String.format("%.4f", lng)}° E"
                                    currentLocationName = address
                                }
                            }

                            @JavascriptInterface
                            fun onLocationConfirmed(lat: String, lng: String, address: String) {
                                post {
                                    rawLat = lat
                                    rawLng = lng
                                    currentCoords = "$lat° N, $lng° E"
                                    currentLocationName = address
                                    Toast.makeText(ctx, "✅ लोकेसन सुरक्षित भयो: $address", Toast.LENGTH_SHORT).show()
                                    onLocationSelected?.invoke(lat, lng, address)
                                }
                            }
                        }, "AndroidMapBridge")

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
                                    "nepal-map.html",
                                    "open-bazar/nepal-map.html"
                                )

                                for (cand in candidates) {
                                    try {
                                        val stream = ctx.assets.open(cand)
                                        val mime = when {
                                            cand.endsWith(".html") -> "text/html"
                                            cand.endsWith(".css") -> "text/css"
                                            cand.endsWith(".js") -> "application/javascript"
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

                        loadUrl("https://appassets.androidplatform.net/assets/nepal-map.html")
                        webViewInstance = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Top Quick City Bar Overlay
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .align(Alignment.TopCenter)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xEE121E34)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF1E2D4A)))
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(nepalCities) { (key, label) ->
                            val isSel = selectedCityKey == key
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) SecondaryAmber else Color(0xFF1E2D4A))
                                    .clickable {
                                        selectedCityKey = key
                                        webViewInstance?.evaluateJavascript("flyToCity('$key');", null)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "📍 $label",
                                    color = if (isSel) Color.Black else Color.White,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Floating GPS Locate Button
            FloatingActionButton(
                onClick = {
                    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    if (fine == PackageManager.PERMISSION_GRANTED) {
                        try {
                            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                            val loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                            if (loc != null) {
                                val lat = loc.latitude
                                val lng = loc.longitude
                                rawLat = lat.toString()
                                rawLng = lng.toString()
                                currentCoords = "${String.format("%.4f", lat)}° N, ${String.format("%.4f", lng)}° E"
                                currentLocationName = "मेरो प्रत्यक्ष GPS लोकेसन"
                                webViewInstance?.evaluateJavascript(
                                    "updateSelectedLocation($lat, $lng, 'मेरो प्रत्यक्ष GPS लोकेसन'); map.flyTo([$lat, $lng], 15);",
                                    null
                                )
                                Toast.makeText(context, "📍 तपाईंको प्रत्यक्ष GPS लोकेसन प्राप्त भयो!", Toast.LENGTH_SHORT).show()
                            } else {
                                webViewInstance?.evaluateJavascript("locateUserGps();", null)
                            }
                        } catch (_: Exception) {
                            webViewInstance?.evaluateJavascript("locateUserGps();", null)
                        }
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 120.dp)
                    .testTag("fab_map_gps_locate"),
                containerColor = Color(0xFF121E34),
                contentColor = SecondaryAmber
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "My GPS Location",
                    modifier = Modifier.size(24.dp)
                )
            }

            // Bottom Selected Location Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .align(Alignment.BottomCenter)
                    .testTag("card_map_selected_location"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xF5121E34)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFDC2626)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(10.dp).background(Color(0xFFDC2626), CircleShape))
                            Text(
                                text = currentLocationName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                        }
                        Text(
                            text = "🔴 $currentCoords",
                            color = SecondaryAmber,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "मुख्य हेन्डलिङ: aazakobazar@gmail.com",
                            color = TextTertiary,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            Toast.makeText(context, "✅ स्थान सुरक्षित भयो: $currentLocationName", Toast.LENGTH_SHORT).show()
                            onLocationSelected?.invoke(rawLat, rawLng, currentLocationName)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("btn_map_confirm_selection")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isNepali) "स्थान छान्नुहोस्" else "Confirm",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
