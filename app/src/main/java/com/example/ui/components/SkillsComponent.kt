package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PortfolioRepository
import com.example.model.SkillGroup
import com.example.model.SkillItem
import com.example.ui.theme.*

/**
 * 'Skills' Component using Compose FlowLayout (FlowRow) and interactive Chips.
 * Categorized by:
 * 1. Languages (प्रोग्रामिङ भाषाहरू)
 * 2. Frameworks & Libraries (फ्रेमवर्क तथा लाइब्रेरीहरू)
 * 3. Tools & Infrastructure (टुल्स तथा क्लाउड पूर्वाधार)
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SkillsComponent(
    modifier: Modifier = Modifier
) {
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var selectedSkillDetail by remember { mutableStateOf<SkillItem?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf("All", "Languages", "Frameworks", "Tools")

    // Filter skill groups based on category filter & search query
    val displayedSkillGroups = remember(selectedCategoryFilter, searchQuery) {
        val groups = PortfolioRepository.skillsOrganizedByCategory
        val filteredByCategory = when (selectedCategoryFilter) {
            "Languages" -> groups.filter { it.category.contains("Languages", ignoreCase = true) }
            "Frameworks" -> groups.filter { it.category.contains("Frameworks", ignoreCase = true) }
            "Tools" -> groups.filter { it.category.contains("Tools", ignoreCase = true) }
            else -> groups
        }

        if (searchQuery.isBlank()) {
            filteredByCategory
        } else {
            filteredByCategory.mapNotNull { group ->
                val matchingSkills = group.skills.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.tag.contains(searchQuery, ignoreCase = true)
                }
                if (matchingSkills.isNotEmpty()) {
                    group.copy(skills = matchingSkills)
                } else null
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("skills_component_card"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(PrimaryBlue.copy(alpha = 0.18f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Code,
                            contentDescription = "Skills",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Technical Proficiencies & Skills",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "प्राविधिक सीप तथा इन्जिनियरिङ दक्षता",
                            color = SecondaryAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(AccentEmerald.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .border(1.dp, AccentEmerald.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Production Ready",
                        color = AccentEmerald,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "Specialized in native Android (Kotlin & Jetpack Compose), full-stack Node.js, offline-first sync architectures, and cloud serverless systems.",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            // Category Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategoryFilter == cat
                    val labelNp = when (cat) {
                        "Languages" -> " भाषाहरू"
                        "Frameworks" -> " फ्रेमवर्क"
                        "Tools" -> " टुल्स"
                        else -> " सबै"
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategoryFilter = cat },
                        label = {
                            Text(
                                text = cat + labelNp,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryBlue,
                            selectedLabelColor = DarkBackground,
                            containerColor = DarkSurfaceVariant,
                            labelColor = TextSecondary
                        ),
                        border = BorderStroke(1.dp, if (isSelected) PrimaryBlue else DarkBorder),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // Quick Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("सीप खोज्नुहोस् (उदा: Kotlin, Compose, Firebase...)", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextTertiary, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("skills_search_input"),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = DarkBorder
                )
            )

            // Detailed Selected Skill Inspection Card (if any chip clicked)
            AnimatedVisibility(
                visible = selectedSkillDetail != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                selectedSkillDetail?.let { skill ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SecondaryAmber, RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(text = "⭐ ${skill.name}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Box(
                                        modifier = Modifier
                                            .background(SecondaryAmber.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = skill.tag, color = SecondaryAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                IconButton(
                                    onClick = { selectedSkillDetail = null },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextTertiary, modifier = Modifier.size(16.dp))
                                }
                            }

                            // Progress Level Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("दक्षता स्तर (Proficiency):", color = TextSecondary, fontSize = 11.sp)
                                Text("${skill.level}% · ${skill.yearsExp}", color = AccentEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .background(DarkBackground, RoundedCornerShape(3.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(skill.level / 100f)
                                        .height(6.dp)
                                        .background(Brush.horizontalGradient(listOf(PrimaryBlue, AccentEmerald)), RoundedCornerShape(3.dp))
                                )
                            }
                        }
                    }
                }
            }

            // Categorized FlowRow Layout Sections
            displayedSkillGroups.forEach { group ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Category Sub-heading with Icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val icon = when {
                            group.category.contains("Languages", ignoreCase = true) -> Icons.Default.Terminal
                            group.category.contains("Frameworks", ignoreCase = true) -> Icons.Default.Widgets
                            else -> Icons.Default.Build
                        }
                        Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                        Text(
                            text = group.category,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "(${group.skills.size})",
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                    }

                    // THE FLOW ROW OF CHIPS
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("skills_flow_row_${group.category.lowercase().replace(" ", "_")}"),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        group.skills.forEach { skill ->
                            val isSelected = selectedSkillDetail?.name == skill.name
                            SkillChipItem(
                                skill = skill,
                                isSelected = isSelected,
                                onClick = {
                                    selectedSkillDetail = if (isSelected) null else skill
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

/**
 * Individual Interactive Skill Chip Item
 */
@Composable
fun SkillChipItem(
    skill: SkillItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) SecondaryAmber else DarkBorder
    val bgColor = if (isSelected) SecondaryAmber.copy(alpha = 0.15f) else DarkSurfaceVariant

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("skill_chip_${skill.name.lowercase().replace(" ", "_")}"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Icon indicator
            val symbol = when {
                skill.name.contains("Kotlin") -> "🟣"
                skill.name.contains("Compose") -> "📱"
                skill.name.contains("TypeScript") || skill.name.contains("JavaScript") -> "🟨"
                skill.name.contains("SQL") -> "🗄️"
                skill.name.contains("Node") -> "🟢"
                skill.name.contains("Firebase") -> "🔥"
                skill.name.contains("Git") -> "🐙"
                skill.name.contains("Docker") -> "🐳"
                skill.name.contains("PWA") -> "⚡"
                else -> "💻"
            }
            Text(text = symbol, fontSize = 12.sp)

            // Skill Name
            Text(
                text = skill.name,
                color = if (isSelected) SecondaryAmber else TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )

            // Level / Years Tag Badge
            Box(
                modifier = Modifier
                    .background(PrimaryBlue.copy(alpha = 0.18f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${skill.level}%",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }
    }
}
