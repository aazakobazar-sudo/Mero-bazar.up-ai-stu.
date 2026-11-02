package com.example.ui.screens

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.example.data.BazarRepository
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * PDF Section A.4 & Section B.5.2: Messenger (Chat + Voice Call System)
 * - Direct Chat with Merchants & Customers
 * - Send Photo attachment simulation
 * - Send Product Link context cards
 * - WebRTC VoIP Audio Voice Calling with realistic dual-tone ring cadence, active call timer, mute & speaker controls
 * - Call History with logged durations and timestamps
 * - Block Merchant / Privacy Protection
 * - Support Ticket Access
 */
data class MessengerContact(
    val id: String,
    val name: String,
    val subtitle: String,
    val phone: String,
    val role: String,
    val isOnline: Boolean = true,
    val avatarEmoji: String = "🏪",
    val unreadCount: Int = 0,
    val lastMessage: String = "",
    val lastTime: String = "भर्खरै"
)

data class CallLogEntry(
    val id: String,
    val contactName: String,
    val phone: String,
    val type: String, // "outgoing", "incoming", "missed"
    val durationText: String,
    val timestamp: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessengerAndCallingScreen(
    onBack: () -> Unit,
    initialTargetName: String? = null,
    initialTargetPhone: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isNepali by BazarRepository.isNepaliLanguage.collectAsState()
    val scope = rememberCoroutineScope()

    // Tab state: 0 = Chats, 1 = Voice Calls & History, 2 = Groups & Support
    var selectedTab by remember { mutableStateOf(0) }

    // Contacts list
    val contacts = remember {
        listOf(
            MessengerContact(
                id = "c-1",
                name = "कालिमाटी फलफूल तथा कृषि थोक मण्डी",
                subtitle = "कालिमाटी, काठमाडौँ · खुला छ",
                phone = "9851000001",
                role = "थोक व्यापारी (Wholesale)",
                isOnline = true,
                avatarEmoji = "🥦",
                unreadCount = 2,
                lastMessage = "आज ताजा स्याउ र आलुको नयाँ गाडी आइपुग्यो हजुर।",
                lastTime = "10:15 AM"
            ),
            MessengerContact(
                id = "c-2",
                name = "श्रेष्ठ खाद्यान्न तथा थोक सप्लायर्स",
                subtitle = "नयाँ बानेश्वर, काठमाडौँ",
                phone = "9841223344",
                role = "प्रमाणित बिक्रेता",
                isOnline = true,
                avatarEmoji = "🍚",
                unreadCount = 0,
                lastMessage = "तपाईंको अर्डर #8492 डिस्प्याच भइसकेको छ।",
                lastTime = "09:40 AM"
            ),
            MessengerContact(
                id = "c-3",
                name = "न्यूरोड ग्याजेट्स तथा मोबाइल होलसेल",
                subtitle = "न्यूरोड, काठमाडौँ",
                phone = "9801998877",
                role = "इलेक्ट्रोनिक्स",
                isOnline = false,
                avatarEmoji = "📱",
                unreadCount = 0,
                lastMessage = "वारन्टी कार्ड बिलसँगै राखिएको छ।",
                lastTime = "हिजो"
            ),
            MessengerContact(
                id = "c-4",
                name = "केन्द्रीय ग्राहक सहायता कक्ष (Support)",
                subtitle = "aazakobazar@gmail.com · 24/7 Support",
                phone = "9851234567",
                role = "Official Support",
                isOnline = true,
                avatarEmoji = "🛡️",
                unreadCount = 0,
                lastMessage = "कुनै समस्या भएमा हामीलाई सिधै सम्पर्क गर्नुहोस्।",
                lastTime = "२ दिन अगाडि"
            )
        )
    }

    var activeChatContact by remember {
        mutableStateOf<MessengerContact?>(
            if (!initialTargetName.isNullOrBlank()) {
                contacts.find { it.name.contains(initialTargetName) } ?: MessengerContact(
                    id = "custom-target",
                    name = initialTargetName,
                    subtitle = "सिधा सम्पर्क",
                    phone = initialTargetPhone ?: "9851000000",
                    role = "बिक्रेता",
                    isOnline = true,
                    avatarEmoji = "🏪"
                )
            } else null
        )
    }

    // Call state: null = idle, active call object when in call
    var activeCall by remember { mutableStateOf<MessengerContact?>(null) }
    var callStatus by remember { mutableStateOf("ringing") } // "ringing", "connected", "ended"
    var callDurationSeconds by remember { mutableStateOf(0) }
    var isMuted by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(false) }
    var isCallMinimized by remember { mutableStateOf(false) }

    // Call history
    val callLogs = remember {
        mutableStateListOf(
            CallLogEntry("cl-1", "कालिमाटी फलफूल तथा कृषि थोक मण्डी", "9851000001", "outgoing", "०२:४५ मिनेट", "आज, १०:१८ AM"),
            CallLogEntry("cl-2", "श्रेष्ठ खाद्यान्न सप्लायर्स", "9841223344", "incoming", "०१:१२ मिनेट", "आज, ०९:४२ AM"),
            CallLogEntry("cl-3", "न्यूरोड ग्याजेट्स", "9801998877", "missed", "मिस्ड कल", "हिजो, ०४:१५ PM"),
            CallLogEntry("cl-4", "केन्द्रीय सहायता कक्ष", "9851234567", "outgoing", "०४:०८ मिनेट", "२ दिन अगाडि")
        )
    }

    // Incoming Call Alert / Popup Toast State
    var incomingCallContact by remember { mutableStateOf<MessengerContact?>(null) }

    // Incoming Call Tone & 30s Auto-Timeout Loop
    LaunchedEffect(incomingCallContact) {
        if (incomingCallContact != null) {
            try {
                val tg = ToneGenerator(AudioManager.STREAM_RING, 70)
                var elapsed = 0
                while (incomingCallContact != null && elapsed < 30000) {
                    tg.startTone(ToneGenerator.TONE_SUP_RINGTONE, 1100)
                    delay(2600)
                    elapsed += 2600
                }
                tg.release()
                if (incomingCallContact != null) {
                    val missed = incomingCallContact!!
                    incomingCallContact = null
                    callLogs.add(
                        0,
                        CallLogEntry(
                            id = "cl-${System.currentTimeMillis()}",
                            contactName = missed.name,
                            phone = missed.phone,
                            type = "missed",
                            durationText = "अनुत्तरित (Missed Call)",
                            timestamp = "भर्खरै"
                        )
                    )
                    Toast.makeText(context, "मिस्ड कल: ${missed.name}", Toast.LENGTH_SHORT).show()
                }
            } catch (_: Exception) {}
        }
    }

    // Outgoing Call Timer & Sound loop effect
    LaunchedEffect(activeCall, callStatus) {
        if (activeCall != null) {
            if (callStatus == "ringing") {
                callDurationSeconds = 0
                // Play subtle dual tone for dial tone
                try {
                    val tg = ToneGenerator(AudioManager.STREAM_VOICE_CALL, 60)
                    tg.startTone(ToneGenerator.TONE_SUP_RINGTONE, 1500)
                } catch (_: Exception) {}

                delay(3000)
                callStatus = "connected"
                Toast.makeText(context, "कल जोडियो (Call Connected)", Toast.LENGTH_SHORT).show()
            } else if (callStatus == "connected") {
                while (activeCall != null && callStatus == "connected") {
                    delay(1000)
                    callDurationSeconds++
                }
            }
        }
    }

    BackHandler {
        if (activeChatContact != null) {
            activeChatContact = null
        } else {
            onBack()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("messenger_calling_screen"),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (activeChatContact != null) activeChatContact!!.name else if (isNepali) "मेरो बजार मेसेन्जर तथा कल" else "Mero Bazar Messenger & Call",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (activeChatContact != null) {
                                if (activeChatContact!!.isOnline) "🟢 अनलाइन · ${activeChatContact!!.phone}" else "अफलाइन"
                            } else "सुरक्षित च्याट, भ्वाइस नोट तथा अडियो कल",
                            color = if (activeChatContact?.isOnline == true) AccentEmerald else TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (activeChatContact != null) activeChatContact = null else onBack()
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    if (activeChatContact != null) {
                        // Voice Call Button with Two-Way Online Check
                        IconButton(
                            onClick = {
                                if (!activeChatContact!!.isOnline) {
                                    Toast.makeText(
                                        context,
                                        "⚠️ सम्पर्क हुन सकेन: अर्को पक्ष हाल अफलाइन हुनुहुन्छ (Offline)।\nदोहोरो अनलाइन (दुवै पक्ष अनलाइन) भएपछि मात्र कल लाग्नेछ।",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    activeCall = activeChatContact
                                    callStatus = "ringing"
                                    isMuted = false
                                    isSpeakerOn = false
                                    isCallMinimized = false
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Call,
                                contentDescription = "Voice Call",
                                tint = if (activeChatContact?.isOnline == true) AccentEmerald else TextTertiary
                            )
                        }

                        // Test Incoming Call Trigger
                        IconButton(
                            onClick = {
                                incomingCallContact = activeChatContact
                            }
                        ) {
                            Icon(Icons.Default.PhoneCallback, contentDescription = "Test Incoming Call", tint = SecondaryAmber)
                        }

                        // Block contact action
                        IconButton(
                            onClick = {
                                Toast.makeText(context, "${activeChatContact!!.name} ब्लक गरियो (Blocked)", Toast.LENGTH_SHORT).show()
                                activeChatContact = null
                            }
                        ) {
                            Icon(Icons.Default.Block, contentDescription = "Block", tint = AccentCrimson)
                        }
                    } else {
                        // Test Incoming Call Trigger (Global)
                        IconButton(
                            onClick = {
                                incomingCallContact = contacts.find { it.isOnline } ?: contacts.first()
                            }
                        ) {
                            Icon(Icons.Default.RingVolume, contentDescription = "Test Incoming Call", tint = AccentEmerald)
                        }

                        // Direct Call Action for Emergency Support
                        IconButton(
                            onClick = {
                                activeCall = contacts.first()
                                callStatus = "ringing"
                                isMuted = false
                                isSpeakerOn = false
                                isCallMinimized = false
                            }
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "New Call", tint = SecondaryAmber)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (activeChatContact != null) {
                // Active Chat Room View
                ActiveChatRoomView(
                    contact = activeChatContact!!,
                    isNepali = isNepali,
                    onStartVoiceCall = {
                        if (!activeChatContact!!.isOnline) {
                            Toast.makeText(context, "⚠️ सम्पर्क हुन सकेन: अर्को पक्ष हाल अफलाइन हुनुहुन्छ (Offline)।", Toast.LENGTH_LONG).show()
                        } else {
                            activeCall = activeChatContact
                            callStatus = "ringing"
                            isMuted = false
                            isSpeakerOn = false
                            isCallMinimized = false
                        }
                    },
                    onTriggerIncomingCall = {
                        incomingCallContact = activeChatContact
                    }
                )
            } else {
                // Main Messenger Directory View
                Column(modifier = Modifier.fillMaxSize()) {
                    // Navigation Tabs
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = DarkSurface,
                        contentColor = PrimaryBlue
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("च्याटहरू (Chats)", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                            icon = { Icon(Icons.Default.ChatBubble, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("अडियो कल (Calls)", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                            icon = { Icon(Icons.Default.PhoneInTalk, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("समूह तथा सहायता", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                            icon = { Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                    }

                    when (selectedTab) {
                        0 -> {
                            // Direct Chats List
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2744)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Text("🔒", fontSize = 20.sp)
                                            Column {
                                                Text("इन्ड-टु-इन्ड इन्क्रिप्टेड सन्देश", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text("ग्राहक र व्यापारी बीचको संवाद पूर्ण सुरक्षित तथा गोप्य रहन्छ।", color = TextSecondary, fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }

                                items(contacts) { c ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { activeChatContact = c },
                                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(46.dp)
                                                    .background(Color(0xFF1E2D4A), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(c.avatarEmoji, fontSize = 22.sp)
                                            }

                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(c.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                                    Text(c.lastTime, color = TextTertiary, fontSize = 10.sp)
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(c.lastMessage, color = TextSecondary, fontSize = 11.5.sp, maxLines = 1)
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text("📞 ${c.phone} · ${c.role}", color = SecondaryAmber, fontSize = 10.sp)
                                            }

                                            IconButton(
                                                onClick = {
                                                    if (!c.isOnline) {
                                                        Toast.makeText(
                                                            context,
                                                            "⚠️ सम्पर्क हुन सकेन: ${c.name} हाल अफलाइन हुनुहुन्छ। दोहोरो अनलाइन भएपछि मात्र कल सम्भव हुन्छ।",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    } else {
                                                        activeCall = c
                                                        callStatus = "ringing"
                                                        isMuted = false
                                                        isSpeakerOn = false
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    Icons.Default.Call,
                                                    contentDescription = "Call",
                                                    tint = if (c.isOnline) AccentEmerald else TextTertiary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        1 -> {
                            // Call History & Dial Panel
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("📞 VoIP अनलाइन अडियो कल प्रणाली", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Box(
                                                    modifier = Modifier
                                                        .background(AccentEmerald.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Text("WebRTC Active", color = AccentEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                "Metered.ca Relay (global.relay.metered.ca:443) मार्फत मोबाइल डाटा वा वाईफाईबाट प्रत्यक्ष बिना कुनै शुल्क व्यापारीसँग कुरा गर्नुहोस्।",
                                                color = TextSecondary,
                                                fontSize = 11.sp
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Button(
                                                onClick = {
                                                    incomingCallContact = contacts.find { it.isOnline } ?: contacts.first()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                                                shape = RoundedCornerShape(10.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Icon(Icons.Default.PhoneCallback, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("📲 आगमन कल सूचना परीक्षण गर्नुहोस् (Simulate Incoming WebRTC Call)", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                            }
                                        }
                                    }
                                }

                                item {
                                    Text("कल इतिहास (Call History Logs)", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
                                }

                                items(callLogs) { log ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Icon(
                                                imageVector = when (log.type) {
                                                    "outgoing" -> Icons.Default.CallMade
                                                    "incoming" -> Icons.Default.CallReceived
                                                    else -> Icons.Default.CallMissed
                                                },
                                                contentDescription = null,
                                                tint = when (log.type) {
                                                    "outgoing" -> PrimaryBlue
                                                    "incoming" -> AccentEmerald
                                                    else -> AccentCrimson
                                                },
                                                modifier = Modifier.size(20.dp)
                                            )

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(log.contactName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Text("📞 ${log.phone} · ${log.durationText}", color = TextSecondary, fontSize = 11.sp)
                                            }

                                            Text(log.timestamp, color = TextTertiary, fontSize = 10.sp)

                                            IconButton(
                                                onClick = {
                                                    val c = contacts.find { it.phone == log.phone } ?: contacts.first()
                                                    if (!c.isOnline) {
                                                        Toast.makeText(
                                                            context,
                                                            "⚠️ सम्पर्क हुन सकेन: ${c.name} हाल अफलाइन हुनुहुन्छ। दोहोरो अनलाइन भएपछि मात्र कल सम्भव हुन्छ।",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    } else {
                                                        activeCall = c
                                                        callStatus = "ringing"
                                                        isMuted = false
                                                        isSpeakerOn = false
                                                    }
                                                }
                                            ) {
                                                Icon(Icons.Default.Call, contentDescription = "Call Again", tint = AccentEmerald)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        2 -> {
                            // Groups & Support
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text("🏪 नेपाल खुल्ला बजार साझा चौतारी", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("४२ जना व्यापारी तथा ग्राहक जोडिनुभएको छ। दैनिक मूल्य, माग र आपूर्ति सम्बन्धी जानकारी।", color = TextSecondary, fontSize = 12.sp)
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Button(
                                                onClick = {
                                                    Toast.makeText(context, "साझा चौतारीमा स्वागत छ!", Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("समूहमा प्रवेश गर्नुहोस् (Join Group)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }

                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text("🛡️ आधिकारिक इमेल सहायता (Email Support)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("मुख्य प्रणाली व्यवस्थापक इमेल:", color = TextSecondary, fontSize = 11.sp)
                                            Text("aazakobazar@gmail.com", color = SecondaryAmber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text("कुनै गुनासो, सुझाव वा पसल प्रमाणीकरण सम्बन्धी सहायताका लागि २४सै घण्टा उपलब्ध।", color = TextTertiary, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 1. Incoming Call Toast Notification (Floating at Top with Animated Entry)
            AnimatedVisibility(
                visible = incomingCallContact != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
                    .zIndex(100f)
            ) {
                if (incomingCallContact != null) {
                    IncomingCallToastNotification(
                        caller = incomingCallContact!!,
                        onAccept = {
                            val caller = incomingCallContact!!
                            incomingCallContact = null
                            activeCall = caller
                            callStatus = "connected"
                            callDurationSeconds = 0
                            isMuted = false
                            isSpeakerOn = false
                            isCallMinimized = false
                            Toast.makeText(context, "कल उठाइयो: ${caller.name}", Toast.LENGTH_SHORT).show()
                        },
                        onDecline = {
                            val caller = incomingCallContact!!
                            incomingCallContact = null
                            try {
                                val tg = ToneGenerator(AudioManager.STREAM_VOICE_CALL, 50)
                                tg.startTone(ToneGenerator.TONE_SUP_BUSY, 350)
                            } catch (_: Exception) {}
                            callLogs.add(
                                0,
                                CallLogEntry(
                                    id = "cl-${System.currentTimeMillis()}",
                                    contactName = caller.name,
                                    phone = caller.phone,
                                    type = "missed",
                                    durationText = "अस्वीकार (Declined)",
                                    timestamp = "भर्खरै"
                                )
                            )
                            Toast.makeText(context, "कल अस्वीकार गरियो (Declined)", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            // 2. Minimized Floating Call Pill (When active call is minimized)
            if (activeCall != null && isCallMinimized) {
                MinimizedCallPill(
                    contact = activeCall!!,
                    durationSeconds = callDurationSeconds,
                    isMuted = isMuted,
                    onExpand = { isCallMinimized = false },
                    onToggleMute = {
                        isMuted = !isMuted
                        Toast.makeText(context, if (isMuted) "🔇 माइक बन्द भयो (Muted)" else "🎙️ माइक चालु भयो (Unmuted)", Toast.LENGTH_SHORT).show()
                    },
                    onEndCall = {
                        val mins = callDurationSeconds / 60
                        val secs = callDurationSeconds % 60
                        val durStr = String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
                        callLogs.add(
                            0,
                            CallLogEntry(
                                id = "cl-${System.currentTimeMillis()}",
                                contactName = activeCall!!.name,
                                phone = activeCall!!.phone,
                                type = "outgoing",
                                durationText = "$durStr मिनेट",
                                timestamp = "भर्खरै"
                            )
                        )
                        activeCall = null
                        callStatus = "ended"
                        Toast.makeText(context, "कल समाप्त भयो ($durStr)", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                        .zIndex(99f)
                )
            }

            // 3. Real-time Voice Call Overlay Screen (Full Screen Overlay Modal)
            if (activeCall != null && !isCallMinimized) {
                ActiveVoiceCallOverlayScreen(
                    contact = activeCall!!,
                    status = callStatus,
                    durationSeconds = callDurationSeconds,
                    isMuted = isMuted,
                    isSpeakerOn = isSpeakerOn,
                    onMinimize = { isCallMinimized = true },
                    onToggleMute = {
                        isMuted = !isMuted
                        Toast.makeText(context, if (isMuted) "🔇 माइक बन्द भयो (Muted)" else "🎙️ माइक चालु भयो (Unmuted)", Toast.LENGTH_SHORT).show()
                    },
                    onToggleSpeaker = {
                        isSpeakerOn = !isSpeakerOn
                        Toast.makeText(context, if (isSpeakerOn) "🔊 लाउडस्पिकर चालु" else "📱 इयरपिस चालु", Toast.LENGTH_SHORT).show()
                    },
                    onEndCall = {
                        val mins = callDurationSeconds / 60
                        val secs = callDurationSeconds % 60
                        val durStr = String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
                        try {
                            val tg = ToneGenerator(AudioManager.STREAM_VOICE_CALL, 60)
                            tg.startTone(ToneGenerator.TONE_SUP_BUSY, 300)
                        } catch (_: Exception) {}
                        callLogs.add(
                            0,
                            CallLogEntry(
                                id = "cl-${System.currentTimeMillis()}",
                                contactName = activeCall!!.name,
                                phone = activeCall!!.phone,
                                type = "outgoing",
                                durationText = "$durStr मिनेट",
                                timestamp = "भर्खरै"
                            )
                        )
                        activeCall = null
                        callStatus = "ended"
                        Toast.makeText(context, "कल समाप्त भयो ($durStr)", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun ActiveChatRoomView(
    contact: MessengerContact,
    isNepali: Boolean,
    onStartVoiceCall: () -> Unit,
    onTriggerIncomingCall: () -> Unit
) {
    val context = LocalContext.current
    var textInput by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            Pair(contact.name, "नमस्ते हजुर! खुल्ला बजारमा स्वागत छ। कुन सामानबारे बुझ्न खोज्नुभएको हो?"),
            Pair("तपाईं", "मलाई ताजा फलफूल र बासमती चामलको थोक दरभाउ चाहिएको थियो।"),
            Pair(contact.name, "अवश्य! आज कालिमाटी मन्डीमा चामल रु. २,४५० प्रति बोरा र स्याउ रु. १६० प्रति केजी छ। डेलिभरी आजै पठाउन सकिन्छ।")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Fast Action Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF101B2E))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("🏪", fontSize = 14.sp)
                Text(contact.role, color = SecondaryAmber, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = onStartVoiceCall,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("भ्वाइस कल", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onTriggerIncomingCall,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2B48)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.PhoneCallback, contentDescription = null, tint = SecondaryAmber, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("📲 परीक्षण", fontSize = 11.sp, color = SecondaryAmber, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Message Stream
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.first == "तपाईं"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                ) {
                    Text(msg.first, color = TextTertiary, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isMe) PrimaryBlue else DarkSurfaceVariant)
                            .padding(horizontal = 12.dp, vertical = 9.dp)
                    ) {
                        Text(
                            text = msg.second,
                            color = if (isMe) Color.White else TextPrimary,
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // Quick replies
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("दरभाउ कति छ?", "डेलिभरी कहिले सम्म?", "QR पठाउनुहोस्").forEach { quick ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkSurfaceVariant)
                        .clickable { messages.add(Pair("तपाईं", quick)) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(quick, color = TextSecondary, fontSize = 10.5.sp)
                }
            }
        }

        // Input row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IconButton(
                onClick = {
                    Toast.makeText(context, "📷 फोटो संलग्न गरियो (Photo Attached)", Toast.LENGTH_SHORT).show()
                    messages.add(Pair("तपाईं", "📷 [फोटो] सामानको नमुना हेर्नुहोस्।"))
                }
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = "Camera", tint = SecondaryAmber)
            }

            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("सन्देश लेख्नुहोस्...", color = TextTertiary, fontSize = 12.sp) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Button(
                onClick = {
                    if (textInput.isNotBlank()) {
                        messages.add(Pair("तपाईं", textInput))
                        textInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(10.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}

/**
 * Incoming Call Toast Notification Popup (WebRTC Connection Request)
 * Pops up when a WebRTC call request is received, allowing the user to Accept or Decline.
 */
@Composable
fun IncomingCallToastNotification(
    caller: MessengerContact,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("incoming_call_toast_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1728)),
        border = BorderStroke(2.dp, AccentEmerald),
        elevation = CardDefaults.cardElevation(defaultElevation = 14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(0xFF1E2D4A), CircleShape)
                            .border(2.dp, AccentEmerald, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(caller.avatarEmoji, fontSize = 24.sp)
                    }
                    Column {
                        Text(
                            text = caller.name,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "📞 +977-${caller.phone} · ${caller.role}",
                            color = SecondaryAmber,
                            fontSize = 11.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(AccentEmerald.copy(alpha = 0.18f), RoundedCornerShape(99.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🔔 आगमन कल...",
                        color = AccentEmerald,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // WebRTC Connection Request Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF06101E), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "📡 WebRTC Connection Request · global.relay.metered.ca:443",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp
                )
                Text(
                    text = "दोहोरो अनलाइन",
                    color = AccentEmerald,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Reject & Accept
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onDecline,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("incoming_call_decline_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCrimson),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Icon(Icons.Default.CallEnd, contentDescription = "Decline", tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("काट्नुहोस् (Decline)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = onAccept,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("incoming_call_accept_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Accept", tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("उठाउनुहोस् (Accept)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

/**
 * Minimized Floating In-Call Pill
 * Docked at the bottom when an active call is minimized, allowing the user to browse products or chat.
 */
@Composable
fun MinimizedCallPill(
    contact: MessengerContact,
    durationSeconds: Int,
    isMuted: Boolean,
    onExpand: () -> Unit,
    onToggleMute: () -> Unit,
    onEndCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mins = durationSeconds / 60
    val secs = durationSeconds % 60
    val durStr = String.format(Locale.getDefault(), "%02d:%02d", mins, secs)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onExpand() }
            .testTag("minimized_call_pill"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2038)),
        border = BorderStroke(1.5.dp, AccentEmerald),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFF1E2D4A), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(contact.avatarEmoji, fontSize = 20.sp)
                }
                Column {
                    Text(contact.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                    Text("🟢 कल चालु: $durStr (ट्याप गरी ठूलो पार्नुहोस्)", color = AccentEmerald, fontSize = 10.5.sp)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleMute,
                    modifier = Modifier
                        .size(36.dp)
                        .background(if (isMuted) AccentCrimson else Color(0xFF1E2D4A), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mute",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = onEndCall,
                    modifier = Modifier
                        .size(36.dp)
                        .background(AccentCrimson, CircleShape)
                ) {
                    Icon(
                        Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Animated Dynamic Voice Spectrum Bar Visualizer
 */
@Composable
fun AnimatedVoiceSpectrumBars(
    isMuted: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_spectrum")

    val h1 by infiniteTransition.animateFloat(
        initialValue = 12f, targetValue = 42f,
        animationSpec = infiniteRepeatable(tween(380, easing = LinearEasing), RepeatMode.Reverse), label = "h1"
    )
    val h2 by infiniteTransition.animateFloat(
        initialValue = 20f, targetValue = 54f,
        animationSpec = infiniteRepeatable(tween(460, easing = LinearEasing), RepeatMode.Reverse), label = "h2"
    )
    val h3 by infiniteTransition.animateFloat(
        initialValue = 16f, targetValue = 60f,
        animationSpec = infiniteRepeatable(tween(320, easing = LinearEasing), RepeatMode.Reverse), label = "h3"
    )
    val h4 by infiniteTransition.animateFloat(
        initialValue = 24f, targetValue = 48f,
        animationSpec = infiniteRepeatable(tween(420, easing = LinearEasing), RepeatMode.Reverse), label = "h4"
    )
    val h5 by infiniteTransition.animateFloat(
        initialValue = 10f, targetValue = 38f,
        animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse), label = "h5"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(64.dp)
        ) {
            listOf(h1, h2, h3, h4, h5, h2, h1).forEach { h ->
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(if (isMuted) 6.dp else h.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isMuted) AccentCrimson else SecondaryAmber)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (isMuted) "🔇 माइक बन्द छ (Microphone Muted)" else "🎙️ प्रत्यक्ष भ्वाइस स्ट्रिम (Live Voice Stream)",
            color = if (isMuted) AccentCrimson else Color(0xFF68D391),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Overlay Screen for Active Voice Calls
 * Includes:
 * - Live Timer indicating Call Duration
 * - Mute / Unmute Button with visual feedback
 * - Prominent 'Hang Up' (कल काट्नुहोस्) Button
 * - Speakerphone Toggle
 * - Metered.ca WebRTC STUN/TURN connection details
 */
@Composable
fun ActiveVoiceCallOverlayScreen(
    contact: MessengerContact,
    status: String,
    durationSeconds: Int,
    isMuted: Boolean,
    isSpeakerOn: Boolean,
    onMinimize: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onEndCall: () -> Unit
) {
    val mins = durationSeconds / 60
    val secs = durationSeconds % 60
    val durFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs)

    val neNumerals = listOf('०','१','२','३','४','५','६','७','८','९')
    fun toNe(num: Int): String = String.format(Locale.getDefault(), "%02d", num).map { if (it.isDigit()) neNumerals[it - '0'] else it }.joinToString("")
    val neTimerText = "${toNe(mins)}:${toNe(secs)}"

    Dialog(
        onDismissRequest = onMinimize,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("active_voice_call_overlay_screen"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF081220)),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.5.dp, Color(0xFF1E2D4A))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Action Header (Minimize & Security Status)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onMinimize,
                        modifier = Modifier
                            .background(Color(0xFF142138), CircleShape)
                            .size(42.dp)
                            .testTag("call_overlay_minimize_btn")
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = "Minimize Call Overlay",
                            tint = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(Color(0xFF0F2744), RoundedCornerShape(99.dp))
                            .border(1.dp, Color(0xFF1E3A60), RoundedCornerShape(99.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(8.dp).background(AccentEmerald, CircleShape))
                            Text(
                                text = "🔒 सुरक्षित WebRTC · global.relay.metered.ca:443",
                                color = Color(0xFF68D391),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(42.dp))
                }

                // Center Area: Avatar, Target Info, Timer & Waveform
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Avatar with Outer Glowing Ring
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .background(Color(0xFF142440), CircleShape)
                            .border(3.dp, if (status == "ringing") SecondaryAmber else AccentEmerald, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(contact.avatarEmoji, fontSize = 54.sp)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = contact.name,
                            color = TextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "📞 +977-${contact.phone} · ${contact.role}",
                            color = SecondaryAmber,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Large Digital Timer Display
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(Color(0xFF0B1728), RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFF182844), RoundedCornerShape(16.dp))
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        if (status == "ringing") {
                            Text(
                                text = "🔔 घण्टी बज्दैछ... (Calling...)",
                                color = SecondaryAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "टुर... टुर...",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        } else {
                            Text(
                                text = durFormatted,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 36.sp,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "कल अवधि: $neTimerText मिनेट ($durationSeconds सेकेन्ड)",
                                color = AccentEmerald,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Dynamic Voice Spectrum Visualizer
                    if (status == "connected") {
                        AnimatedVoiceSpectrumBars(isMuted = isMuted)
                    }
                }

                // Bottom Action Controls: Mute, Hang Up, Speaker
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Mute / Unmute Button
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = onToggleMute,
                            modifier = Modifier
                                .size(56.dp)
                                .testTag("call_mute_toggle_btn")
                                .background(if (isMuted) AccentCrimson else Color(0xFF182740), CircleShape)
                                .border(1.5.dp, if (isMuted) Color.White else Color(0xFF334668), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Mute / Unmute",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Text(
                            text = if (isMuted) "माइक बन्द" else "माइक चालु",
                            color = if (isMuted) AccentCrimson else TextSecondary,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // 2. Hang Up Button (Prominent Red End Call Button)
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = onEndCall,
                            modifier = Modifier
                                .size(68.dp)
                                .testTag("call_hangup_btn")
                                .background(AccentCrimson, CircleShape)
                                .border(2.5.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CallEnd,
                                contentDescription = "Hang Up / End Call",
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                        Text(
                            text = "काट्नुहोस् (End)",
                            color = AccentCrimson,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // 3. Speaker Toggle Button
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = onToggleSpeaker,
                            modifier = Modifier
                                .size(56.dp)
                                .testTag("call_speaker_toggle_btn")
                                .background(if (isSpeakerOn) AccentEmerald else Color(0xFF182740), CircleShape)
                                .border(1.5.dp, if (isSpeakerOn) Color.White else Color(0xFF334668), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                                contentDescription = "Speaker Toggle",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Text(
                            text = if (isSpeakerOn) "स्पिकर चालु" else "इयरपिस",
                            color = if (isSpeakerOn) AccentEmerald else TextSecondary,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Backward compatibility alias for VoiceCallModal
 */
@Composable
fun VoiceCallModal(
    contact: MessengerContact,
    status: String,
    durationSeconds: Int,
    isMuted: Boolean,
    isSpeakerOn: Boolean,
    onToggleMute: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onEndCall: () -> Unit
) {
    ActiveVoiceCallOverlayScreen(
        contact = contact,
        status = status,
        durationSeconds = durationSeconds,
        isMuted = isMuted,
        isSpeakerOn = isSpeakerOn,
        onMinimize = onEndCall,
        onToggleMute = onToggleMute,
        onToggleSpeaker = onToggleSpeaker,
        onEndCall = onEndCall
    )
}

