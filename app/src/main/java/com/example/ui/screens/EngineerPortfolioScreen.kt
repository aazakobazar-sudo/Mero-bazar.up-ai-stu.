package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BazarRepository
import com.example.ui.components.MeroBazarD3DashboardSection
import com.example.ui.components.ProjectTimelineSection
import com.example.ui.components.SkillsComponent
import com.example.ui.theme.*

/**
 * Dedicated Navigation Sections for the Software Engineer & Skills System
 */
enum class EngineerNavSection(
    val titleNp: String,
    val titleEn: String,
    val iconFilled: ImageVector,
    val iconOutlined: ImageVector
) {
    SKILLS("सीप", "Skills", Icons.Filled.Code, Icons.Outlined.Code),
    DASHBOARD("ड्यासबोर्ड", "D3 Dashboard", Icons.Filled.Analytics, Icons.Outlined.Analytics),
    PROJECTS("प्रोजेक्ट", "Projects", Icons.Filled.Layers, Icons.Outlined.Layers),
    ROADMAP("मार्गचित्र", "Roadmap", Icons.Filled.Timeline, Icons.Outlined.Timeline),
    RESUME("बायोडाटा", "Resume", Icons.Filled.Badge, Icons.Outlined.Badge)
}

/**
 * Dedicated Engineer & Technical Proficiencies Screen
 * Features an isolated Navigation Bar (नेगा बार) for Skills, Projects, and Resume.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EngineerPortfolioScreen(
    onBackToBazar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isNepali by BazarRepository.isNepaliLanguage.collectAsState()
    var currentSection by remember { mutableStateOf(EngineerNavSection.SKILLS) }

    // Intercept back button to return smoothly to Open Bazar
    BackHandler {
        if (currentSection != EngineerNavSection.SKILLS) {
            currentSection = EngineerNavSection.SKILLS
        } else {
            onBackToBazar()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("engineer_portfolio_scaffold"),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBackToBazar,
                        modifier = Modifier.testTag("btn_engineer_back_to_bazar")
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back to Bazar",
                            tint = SecondaryAmber
                        )
                    }
                },
                title = {
                    Column {
                        Text(
                            text = if (isNepali) "सफ्टवेयर इन्जिनियर प्रोफाइल" else "Software Engineer Profile",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "सूर्य काफ्ले (Surya Kafle) · Full-Stack & Android",
                            color = PrimaryBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                actions = {
                    // Quick Switch to Bazar Button
                    Button(
                        onClick = onBackToBazar,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("btn_top_return_to_bazar")
                    ) {
                        Icon(
                            Icons.Default.Storefront,
                            contentDescription = null,
                            tint = DarkBackground,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isNepali) "बजार" else "Bazar",
                            color = DarkBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Language Toggle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurfaceVariant)
                            .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                            .clickable { BazarRepository.isNepaliLanguage.value = !isNepali }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                            .testTag("btn_engineer_lang_toggle")
                    ) {
                        Text(
                            text = if (isNepali) "नेपाली" else "EN",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        bottomBar = {
            // Dedicated Navigation Bar for Engineer system
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder)
                    .testTag("engineer_bottom_nav_bar")
            ) {
                EngineerNavSection.values().forEach { section ->
                    val isSelected = currentSection == section
                    val label = if (isNepali) section.titleNp else section.titleEn
                    val activeColor = when (section) {
                        EngineerNavSection.SKILLS -> PrimaryBlue
                        EngineerNavSection.DASHBOARD -> SecondaryAmber
                        EngineerNavSection.PROJECTS -> Color(0xFFF28C0F)
                        EngineerNavSection.ROADMAP -> Color(0xFF06B6D4)
                        EngineerNavSection.RESUME -> AccentEmerald
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentSection = section },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) section.iconFilled else section.iconOutlined,
                                contentDescription = label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = activeColor,
                            selectedTextColor = activeColor,
                            indicatorColor = activeColor.copy(alpha = 0.18f),
                            unselectedIconColor = TextTertiary,
                            unselectedTextColor = TextTertiary
                        ),
                        modifier = Modifier.testTag("engineer_nav_${section.name.lowercase()}")
                    )
                }
            }
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentSection) {
                EngineerNavSection.SKILLS -> {
                    // Flow layout & Chips Component categorized by Languages, Frameworks, and Tools
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            SkillsComponent(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
                EngineerNavSection.DASHBOARD -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            MeroBazarD3DashboardSection(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
                EngineerNavSection.PROJECTS -> {
                    ProjectsScreen(
                        onOpenFlagship = onBackToBazar,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                EngineerNavSection.ROADMAP -> {
                    ProjectTimelineSection(
                        modifier = Modifier.fillMaxSize()
                    )
                }
                EngineerNavSection.RESUME -> {
                    ProfileResumeScreen(
                        onNavigateToContact = onBackToBazar,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
