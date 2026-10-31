package com.example.ui.components

import android.content.Intent
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.BazarRepository
import com.example.model.DigitalInvoice
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

/**
 * Requirement 4: Automated System-Generated Bill / Invoice Dialog
 * View, Save, Download, and Share invoice directly to customer chat
 */
@Composable
fun DigitalInvoiceDialog(
    invoice: DigitalInvoice,
    isNepali: Boolean,
    onSendToChat: (DigitalInvoice) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val invoiceTextToShare = """
========================================
आजको बजार (AAJAKO BAZAR) - आधिकारिक बिल
========================================
बिल नम्बर: ${invoice.invoiceNumber}
अर्डर आईडी: ${invoice.orderId}
मिति: ${invoice.issueDate}
बिक्रेता: ${invoice.sellerName} (PAN/VAT: ${invoice.sellerPanVat})
ग्राहक: ${invoice.customerName} (${invoice.customerPhone})

सामान विवरण:
${invoice.items.joinToString("\n") { "• ${it.title} x ${it.quantity} = रु. ${it.quantity * it.unitPrice}" }}

कुल रकम (Subtotal): रु. ${invoice.subtotal}
१३% भ्याट (VAT 13%): रु. ${invoice.vatAmount}
----------------------------------------
अन्तिम रकम (Grand Total): रु. ${invoice.grandTotal}
भुक्तानी: ${invoice.paymentStatus}
प्रमाणीकरण: ${invoice.authorizedStampSignature}
QR टोकन: ${invoice.qrToken}
========================================
    """.trimIndent()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .border(1.dp, PrimaryBlue.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkBackground),
            shape = RoundedCornerShape(20.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                item {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(16.dp))
                                Text(
                                    text = if (isNepali) "आधिकारिक कर / भ्याट बिल" else "Tax Invoice (VAT Bill)",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Text(text = "बिल नं: ${invoice.invoiceNumber}", color = SecondaryAmber, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bill Paper Container (Light/Dark contrast style)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "बिक्रेता: ${invoice.sellerName}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(text = "PAN: ${invoice.sellerPanVat}", color = PrimaryBlue, fontSize = 11.sp)
                            }
                            Text(text = "ग्राहक: ${invoice.customerName} (${invoice.customerPhone})", color = TextSecondary, fontSize = 11.sp)
                            Text(text = "मिति: ${invoice.issueDate}", color = TextTertiary, fontSize = 10.sp)

                            HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))

                            Text(text = "सामान विवरण (Items Ordered):", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                            invoice.items.forEach { item ->
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "• ${item.title} (x${item.quantity})", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.weight(1f))
                                    Text(text = "रु. ${item.quantity * item.unitPrice}", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "उप-कुल (Subtotal):", color = TextSecondary, fontSize = 11.sp)
                                Text(text = "रु. ${NumberFormat.getNumberInstance(Locale.US).format(invoice.subtotal)}", color = TextPrimary, fontSize = 11.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "१३% भ्याट (VAT 13%):", color = TextSecondary, fontSize = 11.sp)
                                Text(text = "रु. ${NumberFormat.getNumberInstance(Locale.US).format(invoice.vatAmount)}", color = TextPrimary, fontSize = 11.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "कुल जम्मा (Grand Total):", color = AccentEmerald, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = "रु. ${NumberFormat.getNumberInstance(Locale.US).format(invoice.grandTotal)}", color = AccentEmerald, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(DarkBackground, RoundedCornerShape(6.dp))
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "★ ${invoice.authorizedStampSignature} • Verified QR Stamp", color = PrimaryBlue, fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons: Download, Share, Send to Customer Chat
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                onSendToChat(invoice)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isNepali) "च्याटमा ग्राहकलाई बिल पठाउनुहोस्" else "Send Bill to Customer via Chat",
                                color = DarkBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(invoiceTextToShare))
                                    Toast.makeText(context, "बिल प्रतिलिपि गरियो र सेभ भयो!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = if (isNepali) "डाउनलोड / सेभ" else "Save Bill", color = PrimaryBlue, fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, invoiceTextToShare)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "बिल सेयर गर्नुहोस्"))
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = if (isNepali) "सेयर गर्नुहोस्" else "Share", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Requirement 5: In-App Messenger / Live Chat between Merchant & Buyer
 */
@Composable
fun OrderMessengerDialog(
    orderId: String,
    isNepali: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val allMessages by BazarRepository.chatMessages.collectAsState()
    val orderMessages = remember(allMessages, orderId) {
        allMessages.filter { it.orderId == orderId }
    }

    var messageInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp)
                .border(1.dp, PrimaryBlue.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkBackground),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Chat Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(PrimaryBlue.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text(
                                text = if (isNepali) "मेसेन्जर: अर्डर #${orderId.takeLast(5)}" else "Order Chat: $orderId",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(text = "ग्राहक र व्यापारी प्रत्यक्ष सम्पर्क", color = AccentEmerald, fontSize = 10.sp)
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 8.dp))

                // Messages List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(orderMessages) { msg ->
                        val isMe = msg.isMerchantSender
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                        ) {
                            Text(
                                text = msg.senderName,
                                color = TextTertiary,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isMe) PrimaryBlue else DarkSurfaceVariant)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Column {
                                    Text(
                                        text = msg.text,
                                        color = if (isMe) DarkBackground else TextPrimary,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp
                                    )
                                    if (msg.attachedInvoiceNumber != null) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(SecondaryAmber, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "संलग्न बिल: ${msg.attachedInvoiceNumber}",
                                                color = DarkBackground,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                            Text(
                                text = msg.timestamp,
                                color = TextTertiary,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Quick Merchant Response Buttons
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("सामान प्याक भयो", "छिट्टै छुट्दैछ", "बिल पठाइयो").forEach { quickText ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkSurfaceVariant)
                                .clickable {
                                    BazarRepository.sendChatMessage(orderId, quickText, isMerchantSender = true)
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = quickText, color = TextSecondary, fontSize = 10.sp)
                        }
                    }
                }

                // Input Field and Send Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(if (isNepali) "सन्देश लेख्नुहोस्..." else "Type message...", color = TextTertiary, fontSize = 12.sp) },
                        shape = RoundedCornerShape(10.dp),
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
                            if (messageInput.isNotBlank()) {
                                BazarRepository.sendChatMessage(orderId, messageInput, isMerchantSender = true)
                                messageInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(10.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = DarkBackground, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
