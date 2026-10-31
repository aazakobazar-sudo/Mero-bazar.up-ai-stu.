package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import com.example.model.WorkExperience
import com.example.ui.theme.*

/**
 * WorkExperienceSection Composable
 * Displays a sleek vertical timeline of professional roles with company names,
 * dates, role titles, and key responsibilities/achievements.
 */
@Composable
fun WorkExperienceSection(
    modifier: Modifier = Modifier,
    experiences: List<WorkExperience> = PortfolioRepository.workExperiences,
    title: String = "Professional Experience Timeline",
    badge: String? = "Verified Track Record",
    subtitle: String? = "Proven leadership in scaling marketplace architectures and mobile client systems"
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("work_experience_section")
    ) {
        SectionHeader(
            title = title,
            badge = badge,
            subtitle = subtitle
        )

        Spacer(modifier = Modifier.height(18.dp))

        experiences.forEachIndexed { index, experience ->
            val isCurrentRole = index == 0
            val isLast = index == experiences.lastIndex

            TimelineExperienceItem(
                experience = experience,
                isCurrentRole = isCurrentRole,
                isLast = isLast,
                index = index
            )
        }
    }
}

@Composable
fun TimelineExperienceItem(
    experience: WorkExperience,
    isCurrentRole: Boolean,
    isLast: Boolean,
    index: Int,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(true) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("timeline_item_$index")
    ) {
        // Vertical Timeline Column (Node + Connecting Line)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(36.dp)
                .padding(top = 4.dp)
        ) {
            // Timeline Node Indicator
            Box(
                modifier = Modifier
                    .size(if (isCurrentRole) 22.dp else 18.dp)
                    .background(
                        color = if (isCurrentRole) PrimaryBlue.copy(alpha = 0.2f) else DarkSurfaceVariant,
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = if (isCurrentRole) PrimaryBlue else SecondaryAmber.copy(alpha = 0.7f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isCurrentRole) 8.dp else 6.dp)
                        .background(
                            color = if (isCurrentRole) PrimaryBlue else SecondaryAmber,
                            shape = CircleShape
                        )
                )
            }

            // Connecting Vertical Line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .heightIn(min = 160.dp)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    if (isCurrentRole) PrimaryBlue else SecondaryAmber.copy(alpha = 0.7f),
                                    DarkBorder,
                                    DarkBorder.copy(alpha = 0.3f)
                                )
                            )
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Experience Content Card
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 18.dp)
                .border(
                    width = 1.dp,
                    color = if (isCurrentRole) PrimaryBlue.copy(alpha = 0.4f) else DarkBorder,
                    shape = RoundedCornerShape(18.dp)
                )
                .testTag("experience_card_$index"),
            colors = CardDefaults.cardColors(
                containerColor = if (isCurrentRole) Color(0xFF0F1524) else DarkSurface
            ),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Top Meta Row: Company & Duration Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Organization / Company Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            tint = if (isCurrentRole) PrimaryBlue else SecondaryAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = experience.organization,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isCurrentRole) PrimaryBlue else SecondaryAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // Duration Date Badge
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isCurrentRole) PrimaryBlue.copy(alpha = 0.15f) else DarkSurfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isCurrentRole) PrimaryBlue.copy(alpha = 0.4f) else DarkBorder,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = if (isCurrentRole) PrimaryBlue else TextSecondary,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = experience.duration,
                                color = if (isCurrentRole) PrimaryBlue else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Role Title
                Text(
                    text = experience.role,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Location Pin
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = experience.location,
                        color = TextTertiary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Role Overview Description
                Text(
                    text = experience.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = 20.sp,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Key Responsibilities & Achievements Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { isExpanded = !isExpanded }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TaskAlt,
                            contentDescription = null,
                            tint = AccentEmerald,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "Key Responsibilities & Impact",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        experience.achievements.forEach { responsibility ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 6.dp)
                                        .size(6.dp)
                                        .background(AccentEmerald, CircleShape)
                                )
                                Text(
                                    text = responsibility,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
