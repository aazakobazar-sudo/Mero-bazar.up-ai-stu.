package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.BazarRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch

data class AiMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val timestamp: String = "अहिले"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantDialog(
    onDismissRequest: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val isNepali by BazarRepository.isNepaliLanguage.collectAsState()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val messages = remember {
        mutableStateListOf(
            AiMessage(
                id = "ai-init",
                isUser = false,
                text = "🙏 नमस्ते! म Open Bazar को आधिकारिक नि:शुल्क AI सहायक हुँ। म तपाईंलाई आजको बजार भाउ, सस्तो पसल खोजी, स्टक व्यवस्थापन, विज्ञापन क्याप्सन तथा प्रणाली सहयोगमा मद्दत गर्न सक्छु। के जान्न चाहनुहुन्छ?"
            )
        )
    }

    val quickQuestions = listOf(
        "📊 आजको बजार भाउ के छ?",
        "🛍️ सस्तो सामान र पसल खोजिदिनुहोस्",
        "🚀 व्यापार र बिक्री कसरी बढाउने?",
        "🎥 भिडियो र लिङ्क कसरी सेयर गर्ने?",
        "📍 नक्सामा स्थान किन अनिवार्य छ?"
    )

    fun handleSend(promptText: String) {
        val q = promptText.trim()
        if (q.isBlank()) return
        messages.add(AiMessage(id = "usr-${System.currentTimeMillis()}", isUser = true, text = q))
        inputText = ""

        val response = BazarRepository.queryAiAssistant(q)
        messages.add(AiMessage(id = "bot-${System.currentTimeMillis()}", isUser = false, text = response))

        scope.launch {
            if (messages.size > 0) {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(22.dp))
                .border(1.5.dp, PrimaryBlue.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
                .testTag("ai_assistant_dialog"),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryBlue.copy(alpha = 0.15f))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(PrimaryBlue, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SmartToy, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(
                                text = if (isNepali) "OPEN BAZAR AI सहायक" else "Open Bazar AI Agent",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = if (isNepali) "नि:शुल्क बजार विश्लेषण तथा व्यापार सल्लाहकार" else "Free Market & Business Intelligence",
                                color = AccentEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                // Quick Question Chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quickQuestions) { qq ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(DarkSurfaceVariant)
                                .border(1.dp, PrimaryBlue.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                                .clickable { handleSend(qq) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(qq, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                HorizontalDivider(color = DarkBorder)

                // Message Thread
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages) { msg ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 14.dp,
                                            topEnd = 14.dp,
                                            bottomStart = if (msg.isUser) 14.dp else 2.dp,
                                            bottomEnd = if (msg.isUser) 2.dp else 14.dp
                                        )
                                    )
                                    .background(
                                        if (msg.isUser) PrimaryBlue.copy(alpha = 0.25f) else DarkSurfaceVariant
                                    )
                                    .border(
                                        1.dp,
                                        if (msg.isUser) PrimaryBlue.copy(alpha = 0.6f) else DarkBorder,
                                        RoundedCornerShape(14.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = DarkBorder)

                // Input Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text(if (isNepali) "यहाँ प्रश्न सोध्नुहोस् (उदा: बजार भाउ, सस्तो पसल...)" else "Ask anything...", fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_input_field"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = DarkBorder
                        ),
                        singleLine = true
                    )

                    Button(
                        onClick = { handleSend(inputText) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("ai_send_button"),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = DarkBackground, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}
