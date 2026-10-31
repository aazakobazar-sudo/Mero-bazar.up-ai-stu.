package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.shape.CircleShape
import com.example.data.PortfolioRepository
import com.example.model.PortfolioProject
import com.example.ui.components.ProjectSection
import com.example.ui.components.ProjectTimelineSection
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatCard
import com.example.ui.components.TechChip
import com.example.ui.theme.*

@Composable
fun ProjectsScreen(
    onOpenFlagship: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedProjectForModal by remember { mutableStateOf<PortfolioProject?>(null) }

    val filterOptions = listOf("All", "Flagship Market", "Fintech & Cloud", "AgriTech", "Mobile & POS")
    val allProjects = listOf(PortfolioRepository.flagshipProject) + PortfolioRepository.otherProjects

    val displayedProjects = remember(selectedFilter) {
        when (selectedFilter) {
            "Flagship Market" -> allProjects.filter { it.isFlagship }
            "Fintech & Cloud" -> allProjects.filter { it.category.contains("Fintech", ignoreCase = true) || it.category.contains("Cloud", ignoreCase = true) }
            "AgriTech" -> allProjects.filter { it.category.contains("Agri", ignoreCase = true) }
            "Mobile & POS" -> allProjects.filter { it.techStack.any { tech -> tech.contains("Compose") || tech.contains("Android") } }
            else -> allProjects
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("projects_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                SectionHeader(
                    title = "Featured Engineering Projects",
                    badge = "${allProjects.size} Systems",
                    subtitle = "Enterprise architectures, distributed marketplaces, and mobile apps built by Surya Kafle"
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Filter row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filterOptions) { filter ->
                        val isSelected = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PrimaryBlue else DarkSurfaceVariant)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) PrimaryBlue else DarkBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = filter,
                                color = if (isSelected) DarkBackground else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Aajako Bazar D3.js Timeline Showcase Card
        item {
            var showTimelineView by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .border(1.dp, PrimaryBlue.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .clickable { showTimelineView = !showTimelineView }
                    .testTag("card_projects_timeline_toggle"),
                colors = CardDefaults.cardColors(
                    containerColor = DarkSurface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
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
                                    .clip(CircleShape)
                                    .background(PrimaryBlue.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timeline,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Aajako Bazar Developmental Roadmap",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp
                                )
                                Text(
                                    text = "Interactive D3.js · 2023 Genesis to 2027+ Milestones",
                                    color = SecondaryAmber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        FilledTonalButton(
                            onClick = { showTimelineView = !showTimelineView },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (showTimelineView) SecondaryAmber else PrimaryBlue.copy(alpha = 0.25f)
                            )
                        ) {
                            Text(
                                text = if (showTimelineView) "Hide" else "Explore",
                                color = if (showTimelineView) DarkBackground else PrimaryBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = showTimelineView,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(modifier = Modifier.padding(top = 14.dp)) {
                            ProjectTimelineSection()
                        }
                    }
                }
            }
        }

        items(displayedProjects) { project ->
            if (project.isFlagship) {
                ProjectSection(
                    project = project,
                    onExploreDemo = onOpenFlagship,
                    onViewCaseStudy = onOpenFlagship,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            } else {
                ProjectCardItem(
                    project = project,
                    onCardClick = {
                        selectedProjectForModal = project
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }

    selectedProjectForModal?.let { project ->
        ProjectDetailDialog(
            project = project,
            onDismiss = { selectedProjectForModal = null }
        )
    }
}

@Composable
fun ProjectCardItem(
    project: PortfolioProject,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (project.isFlagship) SecondaryAmber.copy(alpha = 0.5f) else DarkBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (project.isFlagship) DarkSurfaceVariant.copy(alpha = 0.9f) else DarkSurface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            if (project.isFlagship) SecondaryAmber.copy(alpha = 0.15f) else PrimaryBlue.copy(alpha = 0.12f),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (project.isFlagship) "★ FLAGSHIP MARKET" else project.category,
                        color = if (project.isFlagship) SecondaryAmber else PrimaryBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = project.title,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = project.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryBlue,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = project.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 19.sp,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tech Stack preview
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                project.techStack.take(3).forEach { tech ->
                    TechChip(text = tech)
                }
                if (project.techStack.size > 3) {
                    TechChip(text = "+${project.techStack.size - 3}")
                }
            }

            // Key metric highlight
            if (project.metrics.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                val topMetric = project.metrics.first()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkBackground.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = topMetric.label, color = TextSecondary, fontSize = 12.sp)
                    Text(text = "${topMetric.value} (${topMetric.change})", color = AccentEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ProjectDetailDialog(
    project: PortfolioProject,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .border(1.dp, PrimaryBlue.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkBackground),
            shape = RoundedCornerShape(20.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(PrimaryBlue.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(text = project.category, color = PrimaryBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = project.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = project.subtitle,
                        color = SecondaryAmber,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = project.fullOverview,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Metrics
                    Text(text = "Key Metrics", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    project.metrics.forEach { metric ->
                        StatCard(metric = metric, modifier = Modifier.padding(vertical = 4.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Challenges & Solution
                    Text(text = "Engineering Challenge", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = project.challenges, color = TextSecondary, fontSize = 13.sp, lineHeight = 19.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Architectural Solution", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = project.solution, color = TextSecondary, fontSize = 13.sp, lineHeight = 19.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Key System Capabilities", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    project.keyFeatures.forEach { feature ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(14.dp))
                            Text(text = feature, color = TextSecondary, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Close Case Study", color = DarkBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
