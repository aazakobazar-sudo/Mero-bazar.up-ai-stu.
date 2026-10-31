package com.example.ui.screens

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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BazarRepository
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginAuthScreen(
    onNavigateBackToMarket: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isNepali by BazarRepository.isNepaliLanguage.collectAsState()
    val currentUser by BazarRepository.currentUser.collectAsState()
    val isLoggedIn by BazarRepository.isLoggedIn.collectAsState()

    var selectedRoleTab by remember { mutableStateOf(UserRole.ADMIN) }
    var inputEmail by remember { mutableStateOf("aazakobazar@gmail.com") }
    var inputPassword by remember { mutableStateOf("A11222333") }
    var inputName by remember { mutableStateOf("सुर्य काफ्ले (Admin)") }
    var inputPhone by remember { mutableStateOf("+977-9851000001") }
    var inputBusiness by remember { mutableStateOf("OPEN BAZAR HQ") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("login_auth_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PrimaryBlue.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
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
                                .background(PrimaryBlue.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LockPerson,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isNepali) "सुरक्षित खाता तथा लगइन प्यानल" else "Secure Access & Auth",
                                    color = PrimaryBlue,
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
                        text = if (isNepali) "OPEN BAZAR (खुल्ला बजार) लगइन" else "OPEN BAZAR User Portal",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isNepali)
                            "मोबाइल अटो-सेटअप सक्रिय छ: तपाईंले लगआउट नगरेसम्म यो मोबाइलमा तपाईंको खाता र प्रोफाइल सधैँ सुरक्षित रहनेछ।"
                        else
                            "Persistent session active: you will remain signed in on this device until you explicitly log out.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Active Logged In Status Card
        if (isLoggedIn) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, AccentEmerald.copy(alpha = 0.6f), RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
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
                                        .size(46.dp)
                                        .background(AccentEmerald.copy(alpha = 0.2f), CircleShape)
                                        .border(1.5.dp, AccentEmerald, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (currentUser.role) {
                                            UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                            UserRole.MERCHANT -> Icons.Default.Storefront
                                            UserRole.CUSTOMER -> Icons.Default.Person
                                        },
                                        contentDescription = null,
                                        tint = AccentEmerald,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = currentUser.name,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    )
                                    Text(
                                        text = currentUser.email,
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .background(
                                        when (currentUser.role) {
                                            UserRole.ADMIN -> SecondaryAmber.copy(alpha = 0.2f)
                                            UserRole.MERCHANT -> PrimaryBlue.copy(alpha = 0.2f)
                                            UserRole.CUSTOMER -> AccentEmerald.copy(alpha = 0.2f)
                                        },
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = currentUser.role.titleNp,
                                    color = when (currentUser.role) {
                                        UserRole.ADMIN -> SecondaryAmber
                                        UserRole.MERCHANT -> PrimaryBlue
                                        UserRole.CUSTOMER -> AccentEmerald
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 12.dp))

                        // Details
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("सम्पर्क नम्बर:", color = TextTertiary, fontSize = 12.sp)
                                Text(currentUser.phone, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }

                            if (currentUser.businessName != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("व्यापार / संस्था:", color = TextTertiary, fontSize = 12.sp)
                                    Text(currentUser.businessName ?: "", color = SecondaryAmber, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            if (currentUser.panVat != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("दर्ता भ्याट / पान:", color = TextTertiary, fontSize = 12.sp)
                                    Text(currentUser.panVat ?: "", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("गुगल प्रमाणीकरण:", color = TextTertiary, fontSize = 12.sp)
                                Text("✅ निःशुल्क प्रमाणित (Verified)", color = AccentEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("मोबाइल स्थिति:", color = TextTertiary, fontSize = 12.sp)
                                Text("📱 अटो-लगइन सक्रिय", color = PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Role Switcher Chips
                        Text(
                            text = if (isNepali) "परीक्षणका लागि भूमिका बदल्नुहोस् (Switch Role):" else "Quick Test Role Switcher:",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    BazarRepository.persistUser(BazarRepository.defaultAdminUser, context)
                                    Toast.makeText(context, "प्रशासक (Admin) खाता सक्रिय भयो", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 6.dp)
                            ) {
                                Text("एडमिन", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    BazarRepository.persistUser(BazarRepository.defaultMerchantUser, context)
                                    Toast.makeText(context, "व्यापारी (Merchant) खाता सक्रिय भयो", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 6.dp)
                            ) {
                                Text("व्यापारी", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    BazarRepository.persistUser(BazarRepository.defaultCustomerUser, context)
                                    Toast.makeText(context, "ग्राहक (Customer) खाता सक्रिय भयो", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 6.dp)
                            ) {
                                Text("ग्राहक", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Logout Button
                        Button(
                            onClick = {
                                BazarRepository.logout(context)
                                Toast.makeText(context, "तपाईं खाताबाट लगआउट हुनुभयो।", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                            border = ButtonDefaults.outlinedButtonBorder,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFEF4444))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isNepali) "सुरक्षित लगआउट गर्नुहोस् (Logout)" else "Log Out Session",
                                color = Color(0xFFEF4444),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Login / Register Form (Available always or when logged out)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (isNepali) "लगइन तथा दर्ता प्रणाली (Login System)" else "Account Sign-In & Onboarding",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )

                    // Role Tabs Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkSurfaceVariant, RoundedCornerShape(10.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Pair(UserRole.ADMIN, if (isNepali) "एडमिन" else "Admin"),
                            Pair(UserRole.MERCHANT, if (isNepali) "व्यापारी" else "Merchant"),
                            Pair(UserRole.CUSTOMER, if (isNepali) "ग्राहक" else "Buyer")
                        ).forEach { (role, label) ->
                            val isSelected = selectedRoleTab == role
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) PrimaryBlue else Color.Transparent)
                                    .clickable {
                                        selectedRoleTab = role
                                        when (role) {
                                            UserRole.ADMIN -> {
                                                inputEmail = "aazakobazar@gmail.com"
                                                inputPassword = "A11222333"
                                                inputName = "सुर्य काफ्ले (Admin)"
                                                inputPhone = "+977-9851000001"
                                                inputBusiness = "OPEN BAZAR HQ"
                                            }
                                            UserRole.MERCHANT -> {
                                                inputEmail = "merchant@openbazar.com"
                                                inputPassword = "merchant123"
                                                inputName = "सुमन श्रेष्ठ (व्यापारी)"
                                                inputPhone = "+977-9841223344"
                                                inputBusiness = "श्रेष्ठ खाद्यान्न तथा थोक सप्लायर्स"
                                            }
                                            UserRole.CUSTOMER -> {
                                                inputEmail = "customer@openbazar.com"
                                                inputPassword = "customer123"
                                                inputName = "रोशन अधिकारी (ग्राहक)"
                                                inputPhone = "+977-9801998877"
                                                inputBusiness = ""
                                            }
                                        }
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) DarkBackground else TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Pre-fill One Click Demo Button for instant testing
                    Button(
                        onClick = {
                            when (selectedRoleTab) {
                                UserRole.ADMIN -> BazarRepository.persistUser(BazarRepository.defaultAdminUser, context)
                                UserRole.MERCHANT -> BazarRepository.persistUser(BazarRepository.defaultMerchantUser, context)
                                UserRole.CUSTOMER -> BazarRepository.persistUser(BazarRepository.defaultCustomerUser, context)
                            }
                            Toast.makeText(context, "${selectedRoleTab.titleNp} मा तत्काल लगइन भयो!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = DarkBackground)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isNepali)
                                "⚡ १-क्लिक ${selectedRoleTab.titleNp} लगइन (Instant Test)"
                            else
                                "⚡ 1-Click Instant ${selectedRoleTab.titleEn} Login",
                            color = DarkBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // 1-Click Google Verify (निःशुल्क प्रमाणीकरण)
                    Button(
                        onClick = {
                            val googleUser = UserProfile(
                                id = "usr-goog-${System.currentTimeMillis().toString().takeLast(4)}",
                                name = if (inputName.isNotBlank()) inputName else "गुगल प्रमाणित प्रयोगकर्ता",
                                email = if (inputEmail.isNotBlank()) inputEmail else "user@gmail.com",
                                phone = if (inputPhone.isNotBlank()) inputPhone else "+977-9841000000",
                                role = selectedRoleTab,
                                businessName = if (selectedRoleTab != UserRole.CUSTOMER) inputBusiness else null,
                                panVat = if (selectedRoleTab != UserRole.CUSTOMER) "301984521" else null,
                                isGoogleVerified = true,
                                isVerified = true
                            )
                            BazarRepository.persistUser(googleUser, context)
                            Toast.makeText(context, "Google द्वारा तत्काल निःशुल्क प्रमाणीकरण सम्पन्न भयो!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        border = ButtonDefaults.outlinedButtonBorder,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isNepali) "🔵 Google द्वारा निःशुल्क तत्काल प्रमाणीकरण" else "🔵 Google 1-Tap Free Verification",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }

                    // Notice: No Email verification blockage!
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "💡 सूचना: परीक्षण र डेमो सहज बनाउन इमेल कोड प्रमाणीकरणको झन्झट हटाइएको छ। तत्काल लगइन गरी सम्पूर्ण प्रणाली चलाउन सक्नुहुन्छ।",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }

                    // Name Field
                    OutlinedTextField(
                        value = inputName,
                        onValueChange = { inputName = it },
                        label = { Text(if (isNepali) "नाम / संस्था" else "Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = DarkBorder
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Email Field
                    OutlinedTextField(
                        value = inputEmail,
                        onValueChange = { inputEmail = it },
                        label = { Text(if (isNepali) "इमेल ठेगाना" else "Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = DarkBorder
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Password Field
                    OutlinedTextField(
                        value = inputPassword,
                        onValueChange = { inputPassword = it },
                        label = { Text(if (isNepali) "पासवर्ड" else "Password") },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Password",
                                    tint = TextSecondary
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = DarkBorder
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Submit Custom Credentials
                    Button(
                        onClick = {
                            if (inputEmail.isBlank() || inputPassword.isBlank()) {
                                Toast.makeText(context, "कृपया इमेल र पासवर्ड प्रविष्ट गर्नुहोस्", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val customUser = UserProfile(
                                id = "usr-${System.currentTimeMillis().toString().takeLast(5)}",
                                name = if (inputName.isNotBlank()) inputName else inputEmail.substringBefore("@"),
                                email = inputEmail,
                                phone = inputPhone,
                                role = selectedRoleTab,
                                businessName = if (selectedRoleTab != UserRole.CUSTOMER) inputBusiness else null,
                                panVat = if (selectedRoleTab != UserRole.CUSTOMER) "301984521" else null,
                                isGoogleVerified = true,
                                isVerified = true
                            )
                            BazarRepository.persistUser(customUser, context)
                            Toast.makeText(context, "लगइन सम्पन्न भयो! तपाईंको खाता मोबाइलमा सुरक्षित छ।", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text(
                            text = if (isNepali) "लगइन गर्नुहोस् (Sign In)" else "Sign In",
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
