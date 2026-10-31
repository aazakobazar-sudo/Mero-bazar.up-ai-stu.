package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.PortfolioRepository
import com.example.model.PortfolioProject
import com.example.ui.theme.*

/**
 * ProjectSection Composable
 * A card-based showcase layout for the 'Aajako Bazar' market project,
 * displaying hero imagery, title, short description, production metrics,
 * and key technical stacks used.
 */
@Composable
fun ProjectSection(
    modifier: Modifier = Modifier,
    project: PortfolioProject = PortfolioRepository.flagshipProject,
    onExploreDemo: (() -> Unit)? = null,
    onViewCaseStudy: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        SecondaryAmber.copy(alpha = 0.6f),
                        DarkBorder,
                        PrimaryBlue.copy(alpha = 0.5f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .testTag("project_section_card"),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Visual Banner with Badge Overlays
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_aajako_bazar),
                    contentDescription = "${project.title} Banner",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .testTag("project_section_banner"),
                    contentScale = ContentScale.Crop
                )

                // Gradient Overlay for smooth transition to dark card surface
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    DarkSurface.copy(alpha = 0.7f),
                                    DarkSurface
                                )
                            )
                        )
                )

                // Top Left Flagship Badge
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(SecondaryAmber, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = DarkBackground,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "FLAGSHIP PROJECT",
                                color = DarkBackground,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(DarkBackground.copy(alpha = 0.75f), RoundedCornerShape(8.dp))
                            .border(1.dp, AccentEmerald.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "HIGH CONCURRENCY",
                            color = AccentEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Card Body Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                // Category tag
                Text(
                    text = project.category.uppercase(),
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Title
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    modifier = Modifier.testTag("project_section_title")
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Subtitle
                Text(
                    text = project.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryAmber,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Short Description
                Text(
                    text = project.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = 20.sp,
                    fontSize = 13.sp,
                    modifier = Modifier.testTag("project_section_description")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Metric Highlights Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF090E1A), RoundedCornerShape(12.dp))
                        .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "52,400+", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Listings", color = TextSecondary, fontSize = 10.sp)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(DarkBorder))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "148,000+", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "MAU", color = TextSecondary, fontSize = 10.sp)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(DarkBorder))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "118 ms", color = AccentEmerald, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Edge Latency", color = TextSecondary, fontSize = 10.sp)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(DarkBorder))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "$1.85M+", color = SecondaryAmber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "GMV", color = TextSecondary, fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Technical Stack Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Technical Stack & Architecture",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // List of Key Technical Stacks
                FlowRowTechChips(
                    techStack = project.techStack,
                    modifier = Modifier.testTag("project_section_tech_stack")
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Interactive CTA Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (onExploreDemo != null) {
                        Button(
                            onClick = onExploreDemo,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                                contentColor = DarkBackground
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_project_section_demo")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = DarkBackground,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Live Demo",
                                color = DarkBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    if (onViewCaseStudy != null) {
                        OutlinedButton(
                            onClick = onViewCaseStudy,
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(
                                    listOf(SecondaryAmber, PrimaryBlue)
                                )
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_project_section_details")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Architecture,
                                contentDescription = null,
                                tint = SecondaryAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Case Study",
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FlowRowTechChips(
    techStack: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        techStack.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                rowItems.forEach { tech ->
                    val isCore = tech.contains("Kotlin") ||
                            tech.contains("Firestore") ||
                            tech.contains("PWA") ||
                            tech.contains("Algolia")
                    TechChip(
                        text = tech,
                        modifier = Modifier.weight(1f),
                        highlight = isCore
                    )
                }
            }
        }
    }
}
