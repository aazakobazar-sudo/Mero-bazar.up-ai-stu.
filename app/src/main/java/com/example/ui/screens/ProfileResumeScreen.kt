package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.PortfolioRepository
import com.example.model.WorkExperience
import com.example.ui.components.HeroSection
import com.example.ui.components.ProjectSection
import com.example.ui.components.SectionHeader
import com.example.ui.components.SkillsComponent
import com.example.ui.components.WorkExperienceSection
import com.example.ui.theme.*

@Composable
fun ProfileResumeScreen(
    onNavigateToContact: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedSkillCategoryIndex by remember { mutableStateOf(0) }
    var showResumeModal by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_resume_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Engineer Hero Section
        item {
            HeroSection(
                onContactClick = onNavigateToContact,
                onSecondaryActionClick = { showResumeModal = true },
                modifier = Modifier.padding(16.dp)
            )
        }

        // Featured Flagship Project
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                SectionHeader(
                    title = "Flagship Production Work",
                    badge = "Verified Scale",
                    subtitle = "High-throughput marketplace engineered by Surya Kafle"
                )
                Spacer(modifier = Modifier.height(10.dp))
                ProjectSection(
                    onExploreDemo = onNavigateToContact,
                    onViewCaseStudy = { showResumeModal = true }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Skills Component (Categorized FlowRow of Chips: Languages, Frameworks, Tools)
        item {
            SkillsComponent(modifier = Modifier.padding(horizontal = 16.dp))
        }

        // Work Experience Timeline
        item {
            Spacer(modifier = Modifier.height(24.dp))
            WorkExperienceSection(
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Testimonials / Recommendations
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                SectionHeader(
                    title = "Recommendations & Endorsements",
                    subtitle = "Feedback from engineering leaders & product stakeholders"
                )
                Spacer(modifier = Modifier.height(12.dp))

                PortfolioRepository.testimonials.forEach { t ->
                    TestimonialCard(testimonial = t)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    if (showResumeModal) {
        ResumeViewerDialog(onDismiss = { showResumeModal = false })
    }
}

@Composable
fun ExperienceTimelineCard(exp: WorkExperience) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exp.role,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = exp.organization,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .background(DarkSurfaceVariant, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = exp.duration, color = SecondaryAmber, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = exp.description,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            exp.achievements.forEach { ach ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AccentEmerald,
                        modifier = Modifier.size(13.dp).padding(top = 2.dp)
                    )
                    Text(text = ach, color = TextPrimary, fontSize = 12.sp, lineHeight = 17.sp)
                }
            }
        }
    }
}

@Composable
fun TestimonialCard(testimonial: com.example.model.Testimonial) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                repeat(testimonial.rating) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = SecondaryAmber, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "\"${testimonial.quote}\"",
                color = TextSecondary,
                fontSize = 12.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = testimonial.clientName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(text = "• ${testimonial.role}, ${testimonial.company}", color = PrimaryBlue, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun ResumeViewerDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val resumeSummary = """
SURYA KAFLE
Senior Full-Stack & Mobile Systems Architect
Email: suryakafle860@gmail.com
Location: Kathmandu, Nepal (Open to Worldwide Remote)
Flagship Project: Aajako Bazar (High-Throughput Commerce Ecosystem)

CORE SKILLS:
- Mobile: Kotlin, Jetpack Compose, Coroutines/Flow, Room DB, Offline Sync, MVI/Clean Architecture
- Cloud & Serverless: Firebase Firestore, Cloud Functions, Algolia Search, Node.js, TypeScript, PostgreSQL
- Architecture: Progressive Web Apps (PWA), High-Concurrency Microservices, Zero-Trust IAM Rules

KEY PROJECTS:
1. Aajako Bazar ( आजको बजार ): 52k+ listings, 148k monthly visitors, sub-120ms latency.
2. Himalaya Cloud POS: Deployed to 420+ retail vendors with offline Bluetooth thermal printing.
3. Nepal AgriConnect: B2B direct auction platform reducing food waste by 24%.
    """.trimIndent()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .border(1.dp, PrimaryBlue.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkBackground),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Curriculum Vitae",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .background(Color(0xFF070B14), RoundedCornerShape(12.dp))
                        .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = resumeSummary,
                        color = TextSecondary,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Download Resume (PDF) & Action Buttons
                Button(
                    onClick = {
                        triggerPrintResume(context)
                        Toast.makeText(context, "Opening PDF print spooler for Surya Kafle Resume...", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = DarkBackground
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("download_resume_pdf_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Download,
                        contentDescription = null,
                        tint = DarkBackground,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Download Resume (PDF)",
                        color = DarkBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(resumeSummary))
                            Toast.makeText(context, "Full CV copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy CV", fontSize = 12.sp, color = TextPrimary)
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Done", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

private fun triggerPrintResume(context: android.content.Context) {
    try {
        val printManager = context.getSystemService(android.content.Context.PRINT_SERVICE) as? android.print.PrintManager
        val webView = android.webkit.WebView(context)
        val resumeHtml = """
            <!DOCTYPE html>
            <html>
            <head>
            <meta charset="utf-8">
            <title>Surya Kafle - Resume</title>
            <style>
              body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif; padding: 24px; color: #111; line-height: 1.5; }
              h1 { color: #0F4C9A; margin-bottom: 2px; }
              .sub { color: #555; font-size: 13px; margin-bottom: 16px; }
              h2 { color: #0F4C9A; border-bottom: 1.5px solid #CBD5E1; padding-bottom: 4px; margin-top: 18px; font-size: 15px; text-transform: uppercase; }
              .skill-item { margin-bottom: 6px; font-size: 12.5px; }
              .exp-head { display: flex; justify-content: space-between; font-weight: bold; font-size: 13.5px; margin-top: 8px; }
              ul { margin: 4px 0 10px 18px; font-size: 12.5px; }
            </style>
            </head>
            <body>
              <h1>Surya Kafle</h1>
              <div class="sub">Lead Full-Stack Architect & Mobile Systems Engineer | Email: suryakafle860@gmail.com | Location: Kathmandu, Nepal</div>
              <h2>Executive Summary</h2>
              <p>Senior Full-Stack Engineer and System Architect with comprehensive expertise in native Android (Kotlin), scalable web architectures (ES6+, React, PWA, D3.js), and resilient cloud backends (Node.js, Firebase). Architect of the Open Bazar commerce ecosystem featuring real-time WebRTC audio/video communications with Metered.ca TURN relays.</p>
              <h2>Core Technical Proficiencies</h2>
              <div class="skill-item"><strong>Frontend Engineering (96%):</strong> Jetpack Compose WebView, React, Modern ES6+, D3.js v7 Data Visualizations, HTML5 Canvas, Offline PWA Service Workers</div>
              <div class="skill-item"><strong>Backend & Cloud Services (95%):</strong> Node.js, Express, Firebase Firestore & Cloud Auth, RESTful API Design, WebCrypto PBKDF2 Encryption</div>
              <div class="skill-item"><strong>UI/UX Design (94%):</strong> Material Design 3, Devanagari Typography, High-Contrast Theming, WCAG Accessibility, Touch Ergonomics</div>
              <div class="skill-item"><strong>System Architecture (93%):</strong> Clean Architecture, MVVM, Offline-First Synchronization, Native Android Bridge, Modular Services</div>
              <div class="skill-item"><strong>Mobile & Hardware (92%):</strong> Android Kotlin, MediaStore Storage, FileProvider Native Sharing, Camera/Audio Permissions</div>
              <div class="skill-item"><strong>Real-Time VoIP (91%):</strong> Metered.ca WebRTC Audio/Video Calling, TURN/STUN Relays, Audio Spectrum Analysers</div>
              <h2>Professional Experience</h2>
              <div class="exp-head"><span>Lead Full-Stack Architect & Mobile Systems Engineer — Open Bazar</span><span>2023 – Present</span></div>
              <ul>
                <li>Architected hyperlocal marketplace serving merchants and customers across all 7 provinces of Nepal.</li>
                <li>Integrated real-time WebRTC audio and video calling powered by Metered.ca enterprise TURN relays (relay.metered.ca:443).</li>
                <li>Engineered offline-first PWA caching and zero-trust local database security with automated invoice generation.</li>
              </ul>
              <div class="exp-head"><span>Senior Software Engineer (Web & Mobile) — Cloud Solutions</span><span>2020 – 2023</span></div>
              <ul>
                <li>Developed high-throughput RESTful services and responsive single-page applications.</li>
                <li>Optimized front-end rendering performance, achieving 98+ Google Lighthouse scores.</li>
              </ul>
              <h2>Education & Certifications</h2>
              <p><strong>Bachelor of Engineering in Computer Engineering / Information Technology</strong> — Tribhuvan University (IOE)</p>
              <p>Android Certified Application Developer, Google Cloud & Firebase Certified, WebRTC Specialist</p>
            </body>
            </html>
        """.trimIndent()

        webView.webViewClient = object : android.webkit.WebViewClient() {
            override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                val printAdapter = webView.createPrintDocumentAdapter("Surya_Kafle_Resume")
                printManager?.print("Surya Kafle Resume", printAdapter, android.print.PrintAttributes.Builder().build())
            }
        }
        webView.loadDataWithBaseURL(null, resumeHtml, "text/html", "utf-8", null)
    } catch (e: Exception) {
        android.widget.Toast.makeText(context, "Printing error: ${e.localizedMessage}", android.widget.Toast.LENGTH_SHORT).show()
    }
}
