package com.example.ui.components

import androidx.annotation.DrawableRes
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.PortfolioRepository
import com.example.ui.theme.*

/**
 * HeroSection Composable
 * Displays a professional profile image, a brief 'About Me' bio,
 * key credentials, and a clear call-to-action button for contact inquiries.
 */
@Composable
fun HeroSection(
    modifier: Modifier = Modifier,
    @DrawableRes profileImageRes: Int = R.drawable.avatar_engineer,
    name: String = PortfolioRepository.ENGINEER_NAME,
    title: String = PortfolioRepository.ENGINEER_TITLE,
    location: String = PortfolioRepository.ENGINEER_LOCATION,
    bio: String = PortfolioRepository.ENGINEER_BIO,
    availabilityBadge: String = "Available for Big Market Projects",
    ctaButtonText: String = "Send Contact Inquiry",
    onContactClick: () -> Unit,
    onSecondaryActionClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        PrimaryBlue.copy(alpha = 0.5f),
                        DarkBorder,
                        SecondaryAmber.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .testTag("hero_section"),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live Status Availability Pill
            Box(
                modifier = Modifier
                    .background(AccentEmerald.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                    .border(1.dp, AccentEmerald.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(AccentEmerald, CircleShape)
                    )
                    Text(
                        text = availabilityBadge,
                        color = AccentEmerald,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Professional Profile Image with Glowing Halo
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        brush = Brush.sweepGradient(
                            listOf(
                                PrimaryBlue,
                                SecondaryAmber,
                                AccentEmerald,
                                PrimaryBlue
                            )
                        ),
                        shape = CircleShape
                    )
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(DarkBackground)
                ) {
                    Image(
                        painter = painterResource(id = profileImageRes),
                        contentDescription = "Professional profile photo of $name",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .testTag("hero_profile_image"),
                        contentScale = ContentScale.Crop
                    )
                }

                // Verified Badge Overlaid
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-4).dp, y = (-4).dp)
                        .size(24.dp)
                        .background(PrimaryBlue, CircleShape)
                        .border(2.dp, DarkBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Verified Architect",
                        tint = DarkBackground,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Full Name
            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("hero_name")
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Professional Title
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = PrimaryBlue,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("hero_title")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Location with Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = SecondaryAmber,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = location,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Brief "About Me" Bio Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0B101D), RoundedCornerShape(14.dp))
                    .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "About Me",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = bio,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.testTag("hero_bio")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Highlight Tags (Specialties)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TechChip(text = "Aajako Bazar Lead", highlight = true)
                    TechChip(text = "Mobile Systems")
                    TechChip(text = "High Concurrency")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Clear Call-to-Action (CTA) for Contact Inquiries
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onContactClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = DarkBackground
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("hero_cta_contact_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mail,
                        contentDescription = null,
                        tint = DarkBackground,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = ctaButtonText,
                        color = DarkBackground,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                }

                if (onSecondaryActionClick != null) {
                    OutlinedButton(
                        onClick = onSecondaryActionClick,
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(
                                listOf(SecondaryAmber, PrimaryBlue)
                            )
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("hero_secondary_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = SecondaryAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Download Resume (PDF) & Verified CV",
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
