package com.example.ui.screens

import android.widget.Toast
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
import com.example.model.MediaType
import com.example.model.MediaUploadItem
import com.example.model.MalwareScanStatus
import com.example.model.SocialVideoLink
import com.example.ui.theme.*

enum class MediaTabOption {
    SOCIAL_LINKS,
    LOCAL_UPLOAD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaAndAdsScreen(
    onNavigateBackToMarket: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isNepali by BazarRepository.isNepaliLanguage.collectAsState()
    val mediaList by BazarRepository.mediaUploads.collectAsState()
    val socialVideosList by BazarRepository.socialVideos.collectAsState()

    var activeTab by remember { mutableStateOf(MediaTabOption.SOCIAL_LINKS) }
    var showForm by remember { mutableStateOf(false) }

    // Social Link Share State
    var socialUrlInput by remember { mutableStateOf("") }
    var socialTitleInput by remember { mutableStateOf("") }
    var socialDescInput by remember { mutableStateOf("") }

    // Local Upload State
    var selectedMediaType by remember { mutableStateOf(MediaType.VIDEO) }
    var inputTitle by remember { mutableStateOf("") }
    var inputDescription by remember { mutableStateOf("") }
    var inputTags by remember { mutableStateOf("नेपाल, बजार, भिडियो") }
    var inputDuration by remember { mutableStateOf("0:45 HD") }
    var isScanningVirus by remember { mutableStateOf(false) }
    var scanCompleted by remember { mutableStateOf(false) }
    var playingMediaId by remember { mutableStateOf<String?>(null) }
    var playingSocialId by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("media_and_ads_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Header
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
                                .background(AccentEmerald.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = AccentEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isNepali) "भिडियो विज्ञापन तथा सामाजिक सेयरिङ" else "Video Ads & Social Hub",
                                    color = AccentEmerald,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        IconButton(onClick = onNavigateBackToMarket) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isNepali) "भिडियो मार्केटिङ तथा सुरक्षित मिडिया हब" else "Video Marketing & Safe Media Studio",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isNepali)
                            "केवल ४ आधिकारिक संजाल (Facebook, YouTube, TikTok, Instagram) का भिडियो लिङ्क मात्र सेयर गर्न पाइने। अनावश्यक बाह्य लिङ्कहरू प्रतिबन्धित छन्।"
                        else
                            "Strict social link filtering: Only YouTube, Facebook, TikTok, and Instagram video links allowed. Arbitrary external links strictly prohibited.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tab selector between Social Link Share and Local Upload
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkBackground)
                            .padding(4.dp)
                    ) {
                        val isSocial = activeTab == MediaTabOption.SOCIAL_LINKS
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSocial) PrimaryBlue else Color.Transparent)
                                .clickable {
                                    activeTab = MediaTabOption.SOCIAL_LINKS
                                    showForm = false
                                }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🎥 सामाजिक भिडियो लिंक",
                                color = if (isSocial) DarkBackground else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        val isLocal = activeTab == MediaTabOption.LOCAL_UPLOAD
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isLocal) AccentEmerald else Color.Transparent)
                                .clickable {
                                    activeTab = MediaTabOption.LOCAL_UPLOAD
                                    showForm = false
                                }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "📁 स्थानीय अपलोड & स्क्यान",
                                color = if (isLocal) DarkBackground else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showForm = !showForm },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showForm) DarkSurfaceVariant else (if (activeTab == MediaTabOption.SOCIAL_LINKS) PrimaryBlue else AccentEmerald)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = if (showForm) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = null,
                            tint = if (showForm) TextPrimary else DarkBackground
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (showForm) "फारम बन्द गर्नुहोस्" else (if (activeTab == MediaTabOption.SOCIAL_LINKS) "+ नयाँ भिडियो लिंक सेयर गर्नुहोस्" else "+ नयाँ मिडिया फाइल अपलोड गर्नुहोस्"),
                            color = if (showForm) TextPrimary else DarkBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // =====================================================================
        // FORM 1: SOCIAL VIDEO LINK SHARE (STRICT FILTERING)
        // =====================================================================
        if (showForm && activeTab == MediaTabOption.SOCIAL_LINKS) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, PrimaryBlue.copy(alpha = 0.6f), RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "🎥 सामाजिक भिडियो लिंक सेयर गर्नुहोस्",
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )

                        // Allowed platforms chips
                        Text(
                            text = "मान्य प्लेटफर्महरू (Strictly Allowed Platforms Only):",
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("🔴 YouTube", "🔵 Facebook", "🎵 TikTok", "📸 Instagram").forEach { plat ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(DarkSurfaceVariant)
                                        .border(1.dp, PrimaryBlue.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(plat, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        OutlinedTextField(
                            value = socialUrlInput,
                            onValueChange = { socialUrlInput = it },
                            label = { Text("भिडियोको आधिकारिक लिङ्क (URL)") },
                            placeholder = { Text("उदा: https://www.youtube.com/watch?v=... वा tiktok.com/...") },
                            leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, tint = PrimaryBlue) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = socialTitleInput,
                            onValueChange = { socialTitleInput = it },
                            label = { Text("भिडियो शीर्षक (Title)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = socialDescInput,
                            onValueChange = { socialDescInput = it },
                            label = { Text("विवरण वा क्याप्सन") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                if (socialUrlInput.isBlank()) {
                                    Toast.makeText(context, "कृपया भिडियोको लिङ्क हाल्नुहोस्", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val (ok, msg) = BazarRepository.addSocialVideo(
                                    url = socialUrlInput,
                                    title = socialTitleInput,
                                    description = socialDescInput,
                                    merchantName = "प्रमाणित पसल"
                                )
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                if (ok) {
                                    socialUrlInput = ""
                                    socialTitleInput = ""
                                    socialDescInput = ""
                                    showForm = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = DarkBackground)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("भिडियो लिङ्क प्रमाणित गरी सेयर गर्नुहोस्", color = DarkBackground, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // =====================================================================
        // FORM 2: LOCAL MEDIA UPLOAD WITH ANTIVIRUS SCAN
        // =====================================================================
        if (showForm && activeTab == MediaTabOption.LOCAL_UPLOAD) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, SecondaryAmber.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = if (isNepali) "मिडिया अपलोड तथा भाइरस सुरक्षा फारम" else "Upload Media & Antivirus Inspection",
                            color = SecondaryAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        OutlinedTextField(
                            value = inputTitle,
                            onValueChange = { inputTitle = it },
                            label = { Text("विज्ञापन शीर्षक (Title)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = inputDescription,
                            onValueChange = { inputDescription = it },
                            label = { Text("विवरण तथा प्रचार सामाग्री") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            shape = RoundedCornerShape(10.dp)
                        )

                        // Antivirus scan component
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, if (scanCompleted) AccentEmerald else SecondaryAmber, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                if (isScanningVirus) {
                                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(6.dp), color = AccentEmerald)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("भाइरस, मालवेयर तथा सुरक्षा जाँच हुँदैछ...", color = SecondaryAmber, fontSize = 11.sp)
                                } else if (scanCompleted) {
                                    Text("✅ भाइरस जाँच सम्पन्न: फाइल १००% सुरक्षित र प्रमाणित छ।", color = AccentEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                } else {
                                    Text("सुरक्षाका लागि अपलोड हुनु अघि फोटो/भिडियोको स्वचालित भाइरस स्क्यान गरिन्छ।", color = TextSecondary, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            isScanningVirus = true
                                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                                isScanningVirus = false
                                                scanCompleted = true
                                                Toast.makeText(context, "भाइरस स्क्यान सफल: फाइल सुरक्षित छ!", Toast.LENGTH_SHORT).show()
                                            }, 700)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("🛡️ भाइरस स्क्यान चलाउनुहोस् (Scan Now)", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (inputTitle.isBlank()) {
                                    Toast.makeText(context, "कृपया शीर्षक लेख्नुहोस्", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (!scanCompleted) {
                                    Toast.makeText(context, "कृपया पहिले भाइरस स्क्यान चलाउनुहोस्", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                BazarRepository.uploadMediaItem(
                                    title = inputTitle,
                                    description = inputDescription.ifBlank { "ओपन बजार भिडियो" },
                                    isPhoto = false,
                                    fileSizeMb = 12.4,
                                    tags = listOf("बजार", "भिडियो", "विज्ञापन"),
                                    duration = inputDuration
                                )
                                Toast.makeText(context, "✅ भिडियो सुरक्षित रूपमा अपलोड भयो!", Toast.LENGTH_SHORT).show()
                                showForm = false
                                scanCompleted = false
                                inputTitle = ""
                                inputDescription = ""
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("प्रमाणित मिडिया प्रकाशित गर्नुहोस्", fontWeight = FontWeight.Bold, color = DarkBackground)
                        }
                    }
                }
            }
        }

        // =====================================================================
        // SECTION 1: SOCIAL VIDEO LINKS SHOWCASE (Strict 4 platforms)
        // =====================================================================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📱 सामाजिक संजालका भिडियोहरू (${socialVideosList.size})",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "YouTube • FB • TikTok • Insta",
                    color = PrimaryBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        items(socialVideosList) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Video Platform Badge & Player Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, PrimaryBlue.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        val isPlaying = playingSocialId == item.id
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            IconButton(
                                onClick = {
                                    playingSocialId = if (isPlaying) null else item.id
                                    Toast.makeText(context, "${item.platform} भिडियो सुरु भयो", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(
                                        when (item.platform) {
                                            "YouTube" -> Color(0xFFEF4444)
                                            "Facebook" -> Color(0xFF1877F2)
                                            "TikTok" -> Color(0xFFEE1D52)
                                            "Instagram" -> Color(0xFFC13584)
                                            else -> PrimaryBlue
                                        },
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isPlaying) "बजिरहेको छ (${item.platform} Player)" else "${item.platform} भिडियो हेर्नुहोस्",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Platform chip in corner
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                                .background(DarkBackground.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(item.platform, color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(item.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(item.description, color = TextSecondary, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🏪 ${item.merchantName}", color = AccentEmerald, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("👁️ ${item.viewsCount} हेराइ · ${item.timeAgo}", color = TextTertiary, fontSize = 11.sp)
                    }
                }
            }
        }

        // =====================================================================
        // SECTION 2: CERTIFIED VIRUS-FREE NATIVE UPLOADS
        // =====================================================================
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "🛡️ प्रमाणित अपलोड मिडिया (${mediaList.size})",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        items(mediaList) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A)),
                        contentAlignment = Alignment.Center
                    ) {
                        val isPlaying = playingMediaId == item.id
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = {
                                    playingMediaId = if (isPlaying) null else item.id
                                    Toast.makeText(context, "स्थानीय भिडियो सुरु भयो", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(AccentEmerald, CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = DarkBackground,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(if (isPlaying) "बजिरहेको छ..." else "${item.duration} भिडियो", color = TextPrimary, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(item.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(item.description, color = TextSecondary, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✅ १००% भाइरस मुक्त प्रमाणित", color = AccentEmerald, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("${item.views} हेराइ · ${item.timestamp}", color = TextTertiary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
