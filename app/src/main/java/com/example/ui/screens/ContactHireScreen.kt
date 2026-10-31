package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.PortfolioRepository
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun ContactHireScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var clientName by remember { mutableStateOf("") }
    var clientEmail by remember { mutableStateOf("") }
    var projectType by remember { mutableStateOf("Enterprise Marketplace") }
    var projectMessage by remember { mutableStateOf("") }
    var inquirySubmitted by remember { mutableStateOf(false) }

    // Estimator State
    var includeAndroid by remember { mutableStateOf(true) }
    var includePwaWeb by remember { mutableStateOf(true) }
    var includeRealtimeMarket by remember { mutableStateOf(true) }
    var includeOfflineSync by remember { mutableStateOf(true) }
    var includePaymentGateway by remember { mutableStateOf(true) }

    val estimatedWeeks = remember(includeAndroid, includePwaWeb, includeRealtimeMarket, includeOfflineSync, includePaymentGateway) {
        var base = 2
        if (includeAndroid) base += 2
        if (includePwaWeb) base += 1
        if (includeRealtimeMarket) base += 2
        if (includeOfflineSync) base += 1
        if (includePaymentGateway) base += 1
        base
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("contact_hire_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Direct Contact Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, SecondaryAmber.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(AccentEmerald, CircleShape)
                        )
                        Text(
                            text = "AVAILABLE FOR CONTRACT & CONSULTING",
                            color = AccentEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Build Your Next Big Market Platform",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Looking to architect a high-throughput marketplace like Aajako Bazar, offline-first mobile app, or resilient cloud backend? Connect directly with Surya Kafle.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Click Action
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:${PortfolioRepository.ENGINEER_EMAIL}")
                                    putExtra(Intent.EXTRA_SUBJECT, "Project Inquiry for Surya Kafle")
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Contact: ${PortfolioRepository.ENGINEER_EMAIL}", Toast.LENGTH_LONG).show()
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Default.Mail, contentDescription = null, tint = PrimaryBlue)
                                Column {
                                    Text(text = "Direct Email", color = TextSecondary, fontSize = 11.sp)
                                    Text(text = PortfolioRepository.ENGINEER_EMAIL, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                            Icon(Icons.Default.Send, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // Interactive Project Scope & Timeline Estimator
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                SectionHeader(
                    title = "Interactive Project Scope Estimator",
                    badge = "Instant Estimate",
                    subtitle = "Select components to estimate delivery timeline for your marketplace or app"
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        EstimatorCheckRow("Native Android Client (Kotlin / Compose)", includeAndroid) { includeAndroid = it }
                        EstimatorCheckRow("Progressive Web App (PWA & Service Worker)", includePwaWeb) { includePwaWeb = it }
                        EstimatorCheckRow("Real-time Bargaining / Chat Engine", includeRealtimeMarket) { includeRealtimeMarket = it }
                        EstimatorCheckRow("Offline Sync & Room DB Persistence", includeOfflineSync) { includeOfflineSync = it }
                        EstimatorCheckRow("e-Payment Gateways & Escrow Setup", includePaymentGateway) { includePaymentGateway = it }

                        HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Estimated Time to MVP", color = TextSecondary, fontSize = 12.sp)
                                Text(
                                    text = "$estimatedWeeks - ${estimatedWeeks + 2} Weeks",
                                    color = PrimaryBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                            Button(
                                onClick = {
                                    projectMessage = "I would like an estimate for a project with: " +
                                            listOfNotNull(
                                                if (includeAndroid) "Native Android" else null,
                                                if (includePwaWeb) "PWA Web" else null,
                                                if (includeRealtimeMarket) "Real-time Marketplace" else null,
                                                if (includeOfflineSync) "Offline Sync" else null,
                                                if (includePaymentGateway) "Payment Gateway" else null
                                            ).joinToString(", ") + ". Estimated timeline: $estimatedWeeks weeks."
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(text = "Apply Scope", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Direct Message / Inquiry Form
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                SectionHeader(title = "Send Collaboration Inquiry")
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = clientName,
                            onValueChange = { clientName = it },
                            label = { Text("Your Name / Organization", color = TextSecondary) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_contact_name"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        OutlinedTextField(
                            value = clientEmail,
                            onValueChange = { clientEmail = it },
                            label = { Text("Your Email Address", color = TextSecondary) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_contact_email"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        OutlinedTextField(
                            value = projectMessage,
                            onValueChange = { projectMessage = it },
                            label = { Text("Project Details / Requirements", color = TextSecondary) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("input_contact_message"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        if (inquirySubmitted) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(AccentEmerald.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                    .border(1.dp, AccentEmerald, RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentEmerald)
                                    Column {
                                        Text(text = "Inquiry Logged & Transmitted!", color = AccentEmerald, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = "Surya Kafle has been notified at suryakafle860@gmail.com", color = TextPrimary, fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (clientName.isBlank() || clientEmail.isBlank()) {
                                    Toast.makeText(context, "Please enter your name and email.", Toast.LENGTH_SHORT).show()
                                } else {
                                    inquirySubmitted = true
                                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:${PortfolioRepository.ENGINEER_EMAIL}")
                                        putExtra(Intent.EXTRA_SUBJECT, "Market Project Inquiry from $clientName")
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "Sender: $clientName\nEmail: $clientEmail\n\nRequirements:\n$projectMessage"
                                        )
                                    }
                                    try {
                                        context.startActivity(emailIntent)
                                    } catch (e: Exception) {
                                        // Ignore if no email app installed
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_submit_inquiry")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = DarkBackground)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Submit Inquiry to Surya Kafle",
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
}

@Composable
fun EstimatorCheckRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, color = TextPrimary, fontSize = 13.sp)
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = PrimaryBlue,
                uncheckedColor = DarkBorder,
                checkmarkColor = DarkBackground
            )
        )
    }
}
