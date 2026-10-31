package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BazarRepository
import com.example.model.PolicyRuleItem
import com.example.model.PolicyScope
import com.example.ui.theme.*

@Composable
fun OpenBazarTermsScreen(
    onNavigateBackToMarket: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isNepali by BazarRepository.isNepaliLanguage.collectAsState()
    val allPolicies by BazarRepository.policies.collectAsState()

    var selectedScope by remember { mutableStateOf(PolicyScope.CUSTOMER) }
    var expandedPolicyId by remember { mutableStateOf<String?>("pol-c1") }

    val filteredPolicies = remember(selectedScope, allPolicies) {
        allPolicies.filter { it.scope == selectedScope }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("open_bazar_terms_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SecondaryAmber.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(SecondaryAmber.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Gavel,
                                    contentDescription = null,
                                    tint = SecondaryAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isNepali) "आधिकारिक नीति तथा सम्झौता" else "Official Policies & Agreements",
                                    color = SecondaryAmber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        IconButton(onClick = onNavigateBackToMarket) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isNepali) "OPEN BAZAR (खुल्ला बजार) शर्त तथा नियमहरू" else "OPEN BAZAR Terms & Conditions",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isNepali)
                            "ग्राहक सुरक्षा, व्यापारीहरूको व्यापारिक अनुशासन, र एडमिन सञ्चालनका विस्तृत कानुनी तथा प्राविधिक शर्तहरू। पूर्ण पारदर्शिता र विश्वासको ग्यारेन्टी।"
                        else
                            "Comprehensive marketplace terms governing customer rights, merchant duties, and administrative arbitration.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Scope Switcher Tabs (ग्राहक, व्यापारी, एडमिन)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Triple(PolicyScope.CUSTOMER, if (isNepali) "🛍️ ग्राहकका शर्त" else "🛍️ Customer", AccentEmerald),
                    Triple(PolicyScope.MERCHANT, if (isNepali) "🏬 व्यापारीका नियम" else "🏬 Merchant", PrimaryBlue),
                    Triple(PolicyScope.ADMIN, if (isNepali) "⚖️ एडमिन अधिकार" else "⚖️ Admin", SecondaryAmber)
                ).forEach { (scope, title, activeColor) ->
                    val isSelected = selectedScope == scope
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) activeColor else Color.Transparent)
                            .clickable { selectedScope = scope }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) DarkBackground else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (selectedScope) {
                        PolicyScope.CUSTOMER -> if (isNepali) "ग्राहक तथा उपभोक्ता सुरक्षा नियमहरू" else "Consumer Protection Provisions"
                        PolicyScope.MERCHANT -> if (isNepali) "व्यापारी तथा बिक्रेताका व्यावसायिक दायित्वहरू" else "Seller Obligations & Standards"
                        PolicyScope.ADMIN -> if (isNepali) "एडमिन तथा प्लेटफर्म सञ्चालकका कानुनी अधिकार" else "Platform Governance & Arbitration"
                    },
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = "${filteredPolicies.size} शर्तहरू",
                    color = TextTertiary,
                    fontSize = 12.sp
                )
            }
        }

        // Policy Items
        items(filteredPolicies) { policy ->
            val isExpanded = expandedPolicyId == policy.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isExpanded) {
                            when (policy.scope) {
                                PolicyScope.CUSTOMER -> AccentEmerald.copy(alpha = 0.6f)
                                PolicyScope.MERCHANT -> PrimaryBlue.copy(alpha = 0.6f)
                                PolicyScope.ADMIN -> SecondaryAmber.copy(alpha = 0.6f)
                            }
                        } else DarkBorder,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        expandedPolicyId = if (isExpanded) null else policy.id
                    },
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
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
                                    when (policy.scope) {
                                        PolicyScope.CUSTOMER -> AccentEmerald.copy(alpha = 0.15f)
                                        PolicyScope.MERCHANT -> PrimaryBlue.copy(alpha = 0.15f)
                                        PolicyScope.ADMIN -> SecondaryAmber.copy(alpha = 0.15f)
                                    },
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = policy.badgeNp,
                                color = when (policy.scope) {
                                    PolicyScope.CUSTOMER -> AccentEmerald
                                    PolicyScope.MERCHANT -> PrimaryBlue
                                    PolicyScope.ADMIN -> SecondaryAmber
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle Details",
                            tint = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = policy.titleNp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = policy.shortSummaryNp,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    AnimatedVisibility(visible = isExpanded) {
                        Column {
                            HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 10.dp))
                            Text(
                                text = "पूर्ण नीति विवरण तथा कानुनी प्रावधान:",
                                color = SecondaryAmber,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = policy.detailedTermsNp,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
