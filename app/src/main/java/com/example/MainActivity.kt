package com.example

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.webkit.WebViewAssetLoader
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import androidx.core.content.FileProvider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.components.AiAssistantDialog
import com.example.ui.screens.DatabaseStatementInspectorScreen
import com.example.ui.screens.EngineerPortfolioScreen
import com.example.ui.screens.MessengerAndCallingScreen
import com.example.ui.screens.NepalInteractiveMapScreen
import com.example.ui.theme.MyApplicationTheme
import java.io.File
import java.io.FileOutputStream

/**
 * Open Bazar (खुल्ला बजार) Native Android Shell
 * Runs the exact Open Bazar application from user's archive (open-bazar)
 * with full modern HTML5, CSS, JS, PBKDF2/AES-GCM WebCrypto, Leaflet maps,
 * Firebase integration, camera/photo uploads, and GPS permissions.
 */
class MainActivity : ComponentActivity() {

    private var fileUploadCallback: ValueCallback<Array<Uri>>? = null
    private var pendingGeoOrigin: String? = null
    private var pendingGeoCallback: GeolocationPermissions.Callback? = null

    // Native file chooser launcher for image/document uploads (Step 4, bills, QR, etc.)
    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val results = WebChromeClient.FileChooserParams.parseResult(result.resultCode, data)
            fileUploadCallback?.onReceiveValue(results)
        } else {
            fileUploadCallback?.onReceiveValue(null)
        }
        fileUploadCallback = null
    }

    // Runtime permission launcher for GPS location (OpenStreetMap / Leaflet map picker in Nepal)
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        pendingGeoCallback?.let { callback ->
            callback.invoke(pendingGeoOrigin, fineGranted || coarseGranted, false)
            pendingGeoCallback = null
            pendingGeoOrigin = null
        }
    }

    // Runtime permission launcher for Camera and Audio recording (Audio/Video calls & voice notes)
    private var pendingMediaRequest: PermissionRequest? = null
    private val mediaPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        pendingMediaRequest?.let { req ->
            val granted = mutableListOf<String>()
            val hasCam = permissions[Manifest.permission.CAMERA] == true ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            val hasMic = permissions[Manifest.permission.RECORD_AUDIO] == true ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

            for (res in req.resources) {
                if (res == PermissionRequest.RESOURCE_VIDEO_CAPTURE && hasCam) granted.add(res)
                else if (res == PermissionRequest.RESOURCE_AUDIO_CAPTURE && hasMic) granted.add(res)
            }
            if (granted.isNotEmpty()) {
                req.grant(granted.toTypedArray())
            } else {
                req.deny()
            }
            pendingMediaRequest = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                var showNativePortfolio by remember { mutableStateOf(false) }
                var showNepalMap by remember { mutableStateOf(false) }
                var showMessengerAndCalls by remember { mutableStateOf(false) }
                var messengerTargetName by remember { mutableStateOf<String?>(null) }
                var messengerTargetPhone by remember { mutableStateOf<String?>(null) }
                var showDatabaseInspector by remember { mutableStateOf(false) }
                var showAiAssistant by remember { mutableStateOf(false) }

                if (showNepalMap) {
                    NepalInteractiveMapScreen(
                        onBack = { showNepalMap = false }
                    )
                } else if (showMessengerAndCalls) {
                    MessengerAndCallingScreen(
                        onBack = { showMessengerAndCalls = false },
                        initialTargetName = messengerTargetName,
                        initialTargetPhone = messengerTargetPhone
                    )
                } else if (showDatabaseInspector) {
                    DatabaseStatementInspectorScreen(
                        onBack = { showDatabaseInspector = false }
                    )
                } else if (showNativePortfolio) {
                    EngineerPortfolioScreen(
                        onBackToBazar = { showNativePortfolio = false }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize()) {
                        OpenBazarApp(
                            onLaunchFileChooser = { callback, params ->
                                fileUploadCallback?.onReceiveValue(null)
                                fileUploadCallback = callback
                                val intent = params?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                                    type = "image/*"
                                    addCategory(Intent.CATEGORY_OPENABLE)
                                }
                                try {
                                    fileChooserLauncher.launch(intent)
                                } catch (e: Exception) {
                                    fileUploadCallback?.onReceiveValue(null)
                                    fileUploadCallback = null
                                    Toast.makeText(this@MainActivity, "Could not open file picker", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onRequestLocationPermission = { origin, callback ->
                                val fineGranted = ContextCompat.checkSelfPermission(
                                    this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                                val coarseGranted = ContextCompat.checkSelfPermission(
                                    this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED

                                if (fineGranted || coarseGranted) {
                                    callback.invoke(origin, true, false)
                                } else {
                                    pendingGeoOrigin = origin
                                    pendingGeoCallback = callback
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            },
                            onRequestMediaPermission = { request ->
                                val hasCam = ContextCompat.checkSelfPermission(
                                    this@MainActivity, Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                                val hasMic = ContextCompat.checkSelfPermission(
                                    this@MainActivity, Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasCam && hasMic) {
                                    request.grant(request.resources)
                                } else {
                                    pendingMediaRequest = request
                                    mediaPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.RECORD_AUDIO
                                        )
                                    )
                                }
                            },
                            onOpenPortfolio = { showNativePortfolio = true },
                            onOpenNepalMap = { showNepalMap = true },
                            onOpenMessenger = { name, phone ->
                                messengerTargetName = name
                                messengerTargetPhone = phone
                                showMessengerAndCalls = true
                            },
                            onOpenDatabaseInspector = { showDatabaseInspector = true },
                            onOpenAiAssistant = { showAiAssistant = true }
                        )
                        // Floating action buttons removed: Home screen is clean, map and features open from dedicated sections.
                    }
                }

                if (showAiAssistant) {
                    AiAssistantDialog(
                        onDismissRequest = { showAiAssistant = false }
                    )
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun OpenBazarApp(
    onLaunchFileChooser: (ValueCallback<Array<Uri>>, WebChromeClient.FileChooserParams?) -> Unit,
    onRequestLocationPermission: (String, GeolocationPermissions.Callback) -> Unit,
    onRequestMediaPermission: (PermissionRequest) -> Unit,
    onOpenPortfolio: () -> Unit = {},
    onOpenNepalMap: () -> Unit = {},
    onOpenMessenger: (String?, String?) -> Unit = { _, _ -> },
    onOpenDatabaseInspector: () -> Unit = {},
    onOpenAiAssistant: () -> Unit = {}
) {
    val context = LocalContext.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var canGoBack by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    // Safety timeout: ensure loading screen dismisses promptly within 1.2s
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1200)
        isLoading = false
    }

    // Intercept back button to navigate backwards in web history if possible
    BackHandler(enabled = canGoBack) {
        webViewInstance?.let { webView ->
            if (webView.canGoBack()) {
                webView.goBack()
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("open_bazar_main_scaffold"),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = Color(0xFF0B1220) // Open Bazar dark theme base
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF6F9FC))
        ) {
            AndroidView(
                factory = { ctx ->
                    // Set up WebViewAssetLoader to serve local files from secure HTTPS origin
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
                            databaseEnabled = true
                            setGeolocationEnabled(true)
                            mediaPlaybackRequiresUserGesture = false
                            allowFileAccess = true
                            allowContentAccess = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            cacheMode = WebSettings.LOAD_DEFAULT
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            userAgentString = "${settings.userAgentString} OpenBazarApp/1.0"
                        }

                        isVerticalScrollBarEnabled = false
                        isHorizontalScrollBarEnabled = false

                        webViewClient = object : WebViewClient() {
                            override fun shouldInterceptRequest(
                                view: WebView,
                                request: WebResourceRequest
                            ): WebResourceResponse? {
                                val url = request.url

                                // 1. Try standard WebViewAssetLoader
                                val loaderResp = assetLoader.shouldInterceptRequest(url)
                                if (loaderResp != null) return loaderResp

                                // 2. Custom fallback for local asset paths
                                val host = url.host ?: ""
                                val path = url.path ?: ""
                                if (host == "appassets.androidplatform.net" || host == "localhost" || host == "127.0.0.1") {
                                    val cleanPath = path.removePrefix("/")
                                        .removePrefix("assets/")
                                        .let { if (it.isEmpty() || it == "/") "index.html" else it }

                                    val candidatePaths = listOf(
                                        cleanPath,
                                        "open-bazar/$cleanPath",
                                        cleanPath.removePrefix("open-bazar/"),
                                        "open-bazar/" + cleanPath.removePrefix("open-bazar/")
                                    ).distinct()

                                    for (cand in candidatePaths) {
                                        try {
                                            val stream = ctx.assets.open(cand)
                                            val mimeType = when {
                                                cand.endsWith(".html") -> "text/html"
                                                cand.endsWith(".css") -> "text/css"
                                                cand.endsWith(".js") -> "application/javascript"
                                                cand.endsWith(".json") -> "application/json"
                                                cand.endsWith(".png") -> "image/png"
                                                cand.endsWith(".jpg") || cand.endsWith(".jpeg") -> "image/jpeg"
                                                cand.endsWith(".svg") -> "image/svg+xml"
                                                cand.endsWith(".apk") -> "application/vnd.android.package-archive"
                                                cand.endsWith(".wav") -> "audio/wav"
                                                cand.endsWith(".mp3") -> "audio/mpeg"
                                                cand.endsWith(".ogg") -> "audio/ogg"
                                                else -> "application/octet-stream"
                                            }
                                            val headers = mapOf(
                                                "Access-Control-Allow-Origin" to "*",
                                                "Cache-Control" to "no-cache"
                                            )
                                            return WebResourceResponse(mimeType, "UTF-8", 200, "OK", headers, stream)
                                        } catch (_: Exception) {
                                            // Continue checking next candidate
                                        }
                                    }
                                }

                                return null
                            }

                            override fun onReceivedError(
                                view: WebView?,
                                request: WebResourceRequest?,
                                error: WebResourceError?
                            ) {
                                super.onReceivedError(view, request, error)
                                if (request?.isForMainFrame == true) {
                                    // Fallback to load direct local asset
                                    view?.loadUrl("https://appassets.androidplatform.net/assets/index.html")
                                }
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                val url = request?.url ?: return false
                                val urlStr = url.toString()

                                // External protocols (Phone calls, SMS, email)
                                if (urlStr.startsWith("tel:") || urlStr.startsWith("mailto:") || urlStr.startsWith("sms:")) {
                                    try {
                                        ctx.startActivity(Intent(Intent.ACTION_VIEW, url))
                                    } catch (e: Exception) {
                                        Toast.makeText(ctx, "No app available for this action", Toast.LENGTH_SHORT).show()
                                    }
                                    return true
                                }

                                // Open external links (like IRD tax portal or OSM directions) in system browser
                                if (!urlStr.contains("appassets.androidplatform.net") &&
                                    !urlStr.contains("firebaseapp.com") &&
                                    !urlStr.contains("googleapis.com") &&
                                    !urlStr.contains("gstatic.com") &&
                                    !urlStr.contains("cdnjs.cloudflare.com") &&
                                    !urlStr.contains("openstreetmap.org") &&
                                    !urlStr.contains("youtube") &&
                                    !urlStr.contains("tiktok") &&
                                    !urlStr.contains("facebook") &&
                                    !urlStr.contains("instagram")
                                ) {
                                    try {
                                        ctx.startActivity(Intent(Intent.ACTION_VIEW, url))
                                    } catch (e: Exception) {
                                        return false
                                    }
                                    return true
                                }

                                return false
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                                canGoBack = view?.canGoBack() == true
                                if (BuildConfig.GEMINI_API_KEY.isNotEmpty()) {
                                    view?.evaluateJavascript("window.GEMINI_API_KEY = '${BuildConfig.GEMINI_API_KEY}';", null)
                                }
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onGeolocationPermissionsShowPrompt(
                                origin: String,
                                callback: GeolocationPermissions.Callback
                            ) {
                                onRequestLocationPermission(origin, callback)
                            }

                            override fun onShowFileChooser(
                                webView: WebView?,
                                filePathCallback: ValueCallback<Array<Uri>>?,
                                fileChooserParams: FileChooserParams?
                            ): Boolean {
                                filePathCallback?.let { callback ->
                                    onLaunchFileChooser(callback, fileChooserParams)
                                    return true
                                }
                                return false
                            }

                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                if (newProgress >= 90) {
                                    isLoading = false
                                }
                                canGoBack = view?.canGoBack() == true
                            }

                            override fun onPermissionRequest(request: PermissionRequest?) {
                                request?.let { onRequestMediaPermission(it) }
                            }

                            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                                android.util.Log.d("OpenBazarJS", "${consoleMessage?.message()} -- From line ${consoleMessage?.lineNumber()} of ${consoleMessage?.sourceId()}")
                                return true
                            }
                        }

                        // Register native Android Bridge for real downloading and real sharing
                        val hostActivity = (ctx as? Activity) ?: (context as Activity)
                        addJavascriptInterface(
                            AndroidBridge(
                                activity = hostActivity,
                                onOpenPortfolio = onOpenPortfolio,
                                onOpenNepalMap = onOpenNepalMap,
                                onOpenMessenger = onOpenMessenger,
                                onOpenDatabaseInspector = onOpenDatabaseInspector,
                                onOpenAiAssistant = onOpenAiAssistant
                            ),
                            "AndroidBridge"
                        )

                        // Real download listener for normal HTTP/HTTPS download URLs
                        setDownloadListener { url, userAgent, contentDisposition, mimetype, _ ->
                            try {
                                val request = DownloadManager.Request(Uri.parse(url)).apply {
                                    setMimeType(mimetype)
                                    val cookies = CookieManager.getInstance().getCookie(url)
                                    addRequestHeader("cookie", cookies)
                                    addRequestHeader("User-Agent", userAgent)
                                    setDescription("Open Bazar बाट डाउनलोड हुँदैछ...")
                                    val filename = URLUtil.guessFileName(url, contentDisposition, mimetype)
                                    setTitle(filename)
                                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                    setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
                                }
                                val dm = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                dm.enqueue(request)
                                Toast.makeText(ctx, "डाउनलोड सुरु भयो...", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                try {
                                    ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                } catch (_: Exception) {}
                            }
                        }

                        // Load index.html directly from local assets with multi-path resolution
                        loadUrl("https://appassets.androidplatform.net/assets/index.html")
                        webViewInstance = this
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("open_bazar_webview")
            )

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .height(3.dp),
                    color = Color(0xFFE8820C),
                    trackColor = Color.Transparent
                )
            }
        }
    }
}

/**
 * Native bridge to handle real downloads and real sharing directly through Android OS
 * Handles:
 * 1. Base64/blob files -> saved to MediaStore / Environment.DIRECTORY_DOWNLOADS
 * 2. File sharing -> Android FileProvider -> Intent.ACTION_SEND -> Native system share sheet
 * 3. Text sharing -> Intent.ACTION_SEND
 */
class AndroidBridge(
    private val activity: Activity,
    private val onOpenPortfolio: (() -> Unit)? = null,
    private val onOpenNepalMap: (() -> Unit)? = null,
    private val onOpenMessenger: ((String?, String?) -> Unit)? = null,
    private val onOpenDatabaseInspector: (() -> Unit)? = null,
    private val onOpenAiAssistant: (() -> Unit)? = null
) {

    @JavascriptInterface
    fun isNativeApp(): Boolean = true

    @JavascriptInterface
    fun getGeminiApiKey(): String = BuildConfig.GEMINI_API_KEY

    @JavascriptInterface
    fun openNativePortfolio() {
        activity.runOnUiThread {
            onOpenPortfolio?.invoke()
        }
    }

    @JavascriptInterface
    fun openRoadmap() {
        activity.runOnUiThread {
            onOpenPortfolio?.invoke()
        }
    }

    @JavascriptInterface
    fun openNepalMap() {
        activity.runOnUiThread {
            onOpenNepalMap?.invoke()
        }
    }

    @JavascriptInterface
    fun openMessenger(targetName: String? = null, targetPhone: String? = null) {
        activity.runOnUiThread {
            onOpenMessenger?.invoke(targetName, targetPhone)
        }
    }

    @JavascriptInterface
    fun openVoiceCall(targetName: String? = null, targetPhone: String? = null) {
        activity.runOnUiThread {
            onOpenMessenger?.invoke(targetName, targetPhone)
        }
    }

    @JavascriptInterface
    fun openDatabaseInspector() {
        activity.runOnUiThread {
            onOpenDatabaseInspector?.invoke()
        }
    }

    @JavascriptInterface
    fun openAiAssistant() {
        activity.runOnUiThread {
            onOpenAiAssistant?.invoke()
        }
    }

    @JavascriptInterface
    fun downloadApk(apkName: String?) {
        activity.runOnUiThread {
            try {
                val safeName = if (apkName.isNullOrBlank()) "aajako-bazar.apk" else apkName
                val candidates = listOf(
                    "open-bazar/mero-bazar.apk",
                    "mero-bazar.apk",
                    "aajako-bazar.apk"
                )
                var bytes: ByteArray? = null
                for (cand in candidates) {
                    try {
                        val inputStream = activity.assets.open(cand)
                        bytes = inputStream.readBytes()
                        inputStream.close()
                        if (bytes != null && bytes.isNotEmpty()) break
                    } catch (_: Exception) {}
                }

                if (bytes == null || bytes.isEmpty()) {
                    Toast.makeText(activity, "APK स्रोत फेला परेन", Toast.LENGTH_SHORT).show()
                    return@runOnUiThread
                }

                val mimeType = "application/vnd.android.package-archive"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Downloads.DISPLAY_NAME, safeName)
                        put(MediaStore.Downloads.MIME_TYPE, mimeType)
                        put(MediaStore.Downloads.IS_PENDING, 1)
                    }
                    val resolver = activity.contentResolver
                    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    if (uri != null) {
                        resolver.openOutputStream(uri)?.use { os ->
                            os.write(bytes)
                        }
                        contentValues.clear()
                        contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                        resolver.update(uri, contentValues, null, null)
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val downloadsDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS
                    )
                    if (!downloadsDir.exists()) downloadsDir.mkdirs()
                    val targetFile = File(downloadsDir, safeName)
                    FileOutputStream(targetFile).use { it.write(bytes) }
                    MediaScannerConnection.scanFile(
                        activity, arrayOf(targetFile.absolutePath), arrayOf(mimeType), null
                    )
                }

                Toast.makeText(
                    activity,
                    "✅ $safeName Downloads फोल्डरमा सुरक्षित भयो!\nNotifications वा File Manager बाट खोलेर इन्स्टल गर्नुहोस्।",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(activity, "डाउनलोड असफल भयो: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @JavascriptInterface
    fun downloadFile(base64Data: String, fileName: String, mimeType: String) {
        activity.runOnUiThread {
            try {
                val cleanBase64 = if (base64Data.contains(",")) {
                    base64Data.substringAfter(",")
                } else {
                    base64Data
                }
                val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                val resolvedMime = if (mimeType.isBlank()) "application/octet-stream" else mimeType
                val safeName = if (fileName.isBlank()) "open_bazar_${System.currentTimeMillis()}" else fileName

                var success = false

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Downloads.DISPLAY_NAME, safeName)
                        put(MediaStore.Downloads.MIME_TYPE, resolvedMime)
                        put(MediaStore.Downloads.IS_PENDING, 1)
                    }
                    val resolver = activity.contentResolver
                    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    if (uri != null) {
                        resolver.openOutputStream(uri)?.use { os ->
                            os.write(bytes)
                        }
                        contentValues.clear()
                        contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                        resolver.update(uri, contentValues, null, null)
                        success = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val downloadsDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS
                    )
                    if (!downloadsDir.exists()) downloadsDir.mkdirs()
                    val targetFile = File(downloadsDir, safeName)
                    FileOutputStream(targetFile).use { fos ->
                        fos.write(bytes)
                    }
                    MediaScannerConnection.scanFile(
                        activity, arrayOf(targetFile.absolutePath), arrayOf(resolvedMime), null
                    )
                    success = true
                }

                if (success) {
                    Toast.makeText(
                        activity,
                        "✅ डाउनलोड सम्पन्न भयो!\n$safeName (Downloads फोल्डरमा सुरक्षित)",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(activity, "फाइल सेभ गर्न सकिएन", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(activity, "डाउनलोडमा समस्या: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @JavascriptInterface
    fun shareFile(base64Data: String, fileName: String, mimeType: String, title: String) {
        activity.runOnUiThread {
            try {
                val cleanBase64 = if (base64Data.contains(",")) {
                    base64Data.substringAfter(",")
                } else {
                    base64Data
                }
                val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                val resolvedMime = if (mimeType.isBlank()) "application/octet-stream" else mimeType
                val safeName = if (fileName.isBlank()) "open_bazar_share_${System.currentTimeMillis()}" else fileName

                val shareDir = File(activity.cacheDir, "shared_files").apply { mkdirs() }
                val shareFile = File(shareDir, safeName)
                FileOutputStream(shareFile).use { fos ->
                    fos.write(bytes)
                }

                val contentUri = FileProvider.getUriForFile(
                    activity,
                    "${activity.packageName}.fileprovider",
                    shareFile
                )

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = resolvedMime
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    putExtra(Intent.EXTRA_SUBJECT, title)
                    putExtra(Intent.EXTRA_TEXT, title)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                activity.startActivity(
                    Intent.createChooser(shareIntent, title.ifEmpty { "Open Bazar — सेयर गर्नुहोस्" })
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(activity, "सेयर गर्न सकिएन: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @JavascriptInterface
    fun shareText(text: String, title: String) {
        activity.runOnUiThread {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, title)
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                activity.startActivity(
                    Intent.createChooser(shareIntent, title.ifEmpty { "Open Bazar — सेयर गर्नुहोस्" })
                )
            } catch (e: Exception) {
                Toast.makeText(activity, "सेयर गर्न सकिएन", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

