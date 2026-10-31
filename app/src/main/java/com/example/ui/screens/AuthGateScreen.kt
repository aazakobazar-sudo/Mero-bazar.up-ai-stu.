package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.BazarRepository
import com.example.model.BAZAR_EIGHT_CATEGORIES
import com.example.model.MainCategoryTier
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.components.AiAssistantDialog
import com.example.ui.theme.*

/**
 * 3 Distinct, Logical Auth Navigation Sections:
 * 1. Login (लगइन)
 * 2. Registration (नयाँ दर्ता - ग्राहक / व्यापारी)
 * 3. Password Recovery (पासवर्ड रिकभरी)
 */
enum class AuthNavSection(
    val titleNp: String,
    val titleEn: String,
    val iconFilled: ImageVector,
    val iconOutlined: ImageVector
) {
    LOGIN("लगइन", "Login", Icons.Filled.Login, Icons.Outlined.Login),
    REGISTER("नयाँ दर्ता", "Register", Icons.Filled.PersonAdd, Icons.Outlined.PersonAdd),
    RECOVERY("पासवर्ड रिकभरी", "Recovery", Icons.Filled.LockReset, Icons.Outlined.LockReset)
}

data class CountryItem(
    val code: String,
    val flag: String,
    val nameNp: String,
    val nameEn: String
)

val supportedCountries = listOf(
    CountryItem("+977", "🇳🇵", "नेपाल", "Nepal"),
    CountryItem("+91", "🇮🇳", "भारत", "India"),
    CountryItem("+1", "🇺🇸", "संयुक्त राज्य अमेरिका", "USA"),
    CountryItem("+44", "🇬🇧", "संयुक्त अधिराज्य", "UK"),
    CountryItem("+61", "🇦🇺", "अस्ट्रेलिया", "Australia"),
    CountryItem("+971", "🇦🇪", "संयुक्त अरब इमिरेट्स", "UAE"),
    CountryItem("+974", "🇶🇦", "कतार", "Qatar"),
    CountryItem("+60", "🇲🇾", "मलेसिया", "Malaysia"),
    CountryItem("+81", "🇯🇵", "जापान", "Japan")
)

data class NepalProvince(
    val name: String,
    val districts: List<String>
)

val nepalProvincesList = listOf(
    NepalProvince("बागमती (Bagmati)", listOf("काठमाडौँ", "ललितपुर", "भक्तपुर", "चितवन", "काभ्रेपलाञ्चोक", "नुवाकोट", "धादिङ", "मकवानपुर", "सिन्धुपाल्चोक", "दोलखा", "रामेछाप", "सिन्धुली", "रसुवा")),
    NepalProvince("कोशी (Koshi)", listOf("मोरङ", "सुनसरी", "झापा", "इलाम", "उदयपुर", "धनकुटा", "भोजपुर", "पाँचथर", "संखुवासभा", "ताप्लेजुङ", "तेह्रथुम", "ओखलढुङ्गा", "खोटाङ", "सोलुखुम्बु")),
    NepalProvince("मधेश (Madhesh)", listOf("धनुषा", "पर्सा", "बारा", "रौतहट", "सर्लाही", "महोत्तरी", "सिरहा", "सप्तरी")),
    NepalProvince("गण्डकी (Gandaki)", listOf("कास्की", "तनहुँ", "गोरखा", "स्याङ्जा", "नवलपुर", "लमजुङ", "बाग्लुङ", "पर्वत", "म्याग्दी", "मनाङ", "मुस्ताङ")),
    NepalProvince("लुम्बिनी (Lumbini)", listOf("रुपन्देही", "कपिलवस्तु", "दाङ", "बाँके", "बर्दिया", "पाल्पा", "गुल्मी", "अर्घाखाँची", "नवलपरासी", "प्युठान", "रोल्पा", "रुकुम पूर्व")),
    NepalProvince("कर्णाली (Karnali)", listOf("सुर्खेत", "दैलेख", "सल्यान", "जुम्ला", "कालीकोट", "जाजरकोट", "रुकुम पश्चिम", "मुगु", "हुम्ला", "डोल्पा")),
    NepalProvince("सुदूरपश्चिम (Sudurpashchim)", listOf("कैलाली", "कञ्चनपुर", "डोटी", "डडेलधुरा", "बैतडी", "दार्चुला", "अछाम", "बझाङ", "बाजुरा"))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthGateScreen(
    onAuthenticationSuccess: (UserProfile) -> Unit,
    onNavigateToEngineer: () -> Unit = {}
) {
    val context = LocalContext.current
    val isNepali by BazarRepository.isNepaliLanguage.collectAsState()

    // Active Navigation Section: Login, Register, or Recovery
    var currentSection by remember { mutableStateOf(AuthNavSection.LOGIN) }
    var showAiDialog by remember { mutableStateOf(false) }

    // --- 1. Login State ---
    var loginEmail by remember { mutableStateOf("aazakodazar@gmail.com") }
    var loginPassword by remember { mutableStateOf("A11222333") }
    var isLoginPasswordVisible by remember { mutableStateOf(false) }
    var isLoginOtpPending by remember { mutableStateOf(false) }
    var loginOtpInput by remember { mutableStateOf("849201") }
    var pendingLoginUser by remember { mutableStateOf<UserProfile?>(null) }
    var loginErrorMessage by remember { mutableStateOf<String?>(null) }
    val demoLoginOtp = "849201"

    // --- 2. Registration State ---
    var registrationTargetRole by remember { mutableStateOf(UserRole.CUSTOMER) }

    // Customer Registration
    var custName by remember { mutableStateOf("") }
    var custEmail by remember { mutableStateOf("") }
    var custSelectedCountry by remember { mutableStateOf(supportedCountries[0]) }
    var custPhone by remember { mutableStateOf("") }
    var custAddress by remember { mutableStateOf("") }
    var custPassword by remember { mutableStateOf("") }
    var custConfirmPassword by remember { mutableStateOf("") }
    var custSelectedAvatar by remember { mutableStateOf("👤") }
    var custMapCoordinates by remember { mutableStateOf<String?>(null) }
    var custMapLocationName by remember { mutableStateOf<String?>(null) }
    var isCustOtpPending by remember { mutableStateOf(false) }
    var custOtpInput by remember { mutableStateOf("629145") }
    val demoCustOtp = "629145"

    // Merchant Registration
    var merStep by remember { mutableStateOf(1) }
    var merName by remember { mutableStateOf("") }
    var merBusinessName by remember { mutableStateOf("") }
    var merSelectedCountry by remember { mutableStateOf(supportedCountries[0]) }
    var merPhone by remember { mutableStateOf("") }
    var merSelectedMainCategory by remember { mutableStateOf(BAZAR_EIGHT_CATEGORIES[0]) }
    var merSelectedSubcategory by remember { mutableStateOf(BAZAR_EIGHT_CATEGORIES[0].subcategoriesNp[0]) }
    var showMainCategoryModal by remember { mutableStateOf(false) }
    var showSubcategoryModal by remember { mutableStateOf(false) }
    var merPanVat by remember { mutableStateOf("") }
    var merAddress by remember { mutableStateOf("") }
    var merMapCoordinates by remember { mutableStateOf<String?>(null) }
    var merMapLocationName by remember { mutableStateOf<String?>(null) }
    var merPassword by remember { mutableStateOf("") }
    var merConfirmPassword by remember { mutableStateOf("") }
    var merAgreedTerms by remember { mutableStateOf(true) }
    var isMerOtpPending by remember { mutableStateOf(false) }
    var merOtpInput by remember { mutableStateOf("738192") }
    val demoMerOtp = "738192"

    // --- 3. Forgot Password State ---
    var forgotEmail by remember { mutableStateOf("aazakodazar@gmail.com") }
    var isForgotOtpSent by remember { mutableStateOf(false) }
    var forgotOtpInput by remember { mutableStateOf("518924") }
    var isForgotOtpVerified by remember { mutableStateOf(false) }
    var forgotNewPassword by remember { mutableStateOf("") }
    var forgotConfirmPassword by remember { mutableStateOf("") }
    var forgotErrorMessage by remember { mutableStateOf<String?>(null) }
    val demoForgotOtp = "518924"

    // Dialogs
    var showCountryDialogFor by remember { mutableStateOf<String?>(null) }
    var showNepalMapPickerFor by remember { mutableStateOf<String?>(null) }

    // Intercept back button to return to Login
    if (currentSection != AuthNavSection.LOGIN) {
        BackHandler {
            currentSection = AuthNavSection.LOGIN
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("auth_gate_scaffold"),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.app_logo),
                                contentDescription = "Open Bazar Logo",
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, PrimaryBlue, CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Column {
                                Text(
                                    text = "OPEN BAZAR",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = if (isNepali) "खुल्ला बजार" else "Digital Open Market",
                                    color = AccentEmerald,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    },
                    actions = {
                        // Engineer Skills & Profile button
                        Button(
                            onClick = onNavigateToEngineer,
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber.copy(alpha = 0.18f)),
                            border = BorderStroke(1.dp, SecondaryAmber),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_auth_engineer_skills")
                        ) {
                            Icon(
                                Icons.Default.Code,
                                contentDescription = null,
                                tint = SecondaryAmber,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isNepali) "सीप" else "Skills",
                                color = SecondaryAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // AI Assistant button
                        Button(
                            onClick = { showAiDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue.copy(alpha = 0.2f)),
                            border = BorderStroke(1.dp, PrimaryBlue),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("🤖 AI", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Language Switcher
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceVariant)
                                .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    BazarRepository.isNepaliLanguage.value = !isNepali
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("btn_auth_lang_toggle"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isNepali) "नेपाली" else "English",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_bottom_nav_bar")
            ) {
                AuthNavSection.values().forEach { section ->
                    val isSelected = currentSection == section
                    val activeColor = when (section) {
                        AuthNavSection.LOGIN -> PrimaryBlue
                        AuthNavSection.REGISTER -> AccentEmerald
                        AuthNavSection.RECOVERY -> SecondaryAmber
                    }
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            currentSection = section
                            loginErrorMessage = null
                            forgotErrorMessage = null
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) section.iconFilled else section.iconOutlined,
                                contentDescription = section.titleEn,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = if (isNepali) section.titleNp else section.titleEn,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = activeColor,
                            selectedTextColor = activeColor,
                            indicatorColor = activeColor.copy(alpha = 0.18f),
                            unselectedIconColor = TextTertiary,
                            unselectedTextColor = TextTertiary
                        ),
                        modifier = Modifier.testTag("auth_nav_item_${section.name.lowercase()}")
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
                // =============================================================
                // 1. LOGIN SECTION (सफा र व्यवस्थित लगइन)
                // =============================================================
                AuthNavSection.LOGIN -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(18.dp),
                                border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.4f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .background(PrimaryBlue.copy(alpha = 0.15f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Login,
                                                contentDescription = null,
                                                tint = PrimaryBlue,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = if (isNepali) "खातामा प्रवेश गर्नुहोस् (Login)" else "Account Login",
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 17.sp
                                            )
                                            Text(
                                                text = if (isNepali) "पहिले दर्ता भएको इमेल र पासवर्ड राख्नुहोस्" else "Enter registered email and password",
                                                color = TextSecondary,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }

                                    HorizontalDivider(color = DarkBorder)

                                    // Email / Phone Field
                                    OutlinedTextField(
                                        value = loginEmail,
                                        onValueChange = {
                                            loginEmail = it
                                            loginErrorMessage = null
                                        },
                                        label = { Text(if (isNepali) "इमेल ठेगाना" else "Email Address") },
                                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = PrimaryBlue) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_login_email"),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    // Password Field
                                    OutlinedTextField(
                                        value = loginPassword,
                                        onValueChange = {
                                            loginPassword = it
                                            loginErrorMessage = null
                                        },
                                        label = { Text(if (isNepali) "पासवर्ड" else "Password") },
                                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryBlue) },
                                        trailingIcon = {
                                            IconButton(onClick = { isLoginPasswordVisible = !isLoginPasswordVisible }) {
                                                Icon(
                                                    if (isLoginPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                    contentDescription = "Toggle password visibility"
                                                )
                                            }
                                        },
                                        visualTransformation = if (isLoginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_login_password"),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    loginErrorMessage?.let { err ->
                                        Text(err, color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }

                                    // Primary Log In Button
                                    if (!isLoginOtpPending) {
                                        Button(
                                            onClick = {
                                                val cleanEmail = loginEmail.trim().lowercase()
                                                // Direct Admin check
                                                if ((cleanEmail == "aazakodazar@gmail.com" || cleanEmail == "aazakobazar@gmail.com") && loginPassword.trim() == "A11222333") {
                                                    pendingLoginUser = BazarRepository.defaultAdminUser
                                                    isLoginOtpPending = true
                                                    loginOtpInput = demoLoginOtp
                                                    loginErrorMessage = null
                                                    return@Button
                                                }
                                                val (isValid, user) = BazarRepository.validateLoginCredentials(loginEmail, loginPassword)
                                                if (isValid && user != null) {
                                                    pendingLoginUser = user
                                                    isLoginOtpPending = true
                                                    loginOtpInput = demoLoginOtp
                                                    loginErrorMessage = null
                                                } else {
                                                    loginErrorMessage = if (isNepali)
                                                        "इमेल वा पासवर्ड मिलेन। कृपया दर्ता भएको इमेल/पासवर्ड जाँच्नुहोस्।"
                                                    else
                                                        "Invalid credentials. Please verify your registered email and password."
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp)
                                                .testTag("btn_login_submit"),
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(
                                                text = if (isNepali) "लगइन गर्नुहोस् (Login)" else "Log In",
                                                fontWeight = FontWeight.Bold,
                                                color = DarkBackground,
                                                fontSize = 15.sp
                                            )
                                        }
                                    }

                                    // 1.1 OTP SECTION (Step 1.1)
                                    if (isLoginOtpPending) {
                                        HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))

                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.dp, SecondaryAmber.copy(alpha = 0.5f))
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(14.dp),
                                                verticalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SecondaryAmber)
                                                    Column {
                                                        Text(
                                                            text = if (isNepali) "सुरक्षा ओटिपी (OTP) प्रमाणित गर्नुहोस्" else "Verify Security OTP",
                                                            color = SecondaryAmber,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 14.sp
                                                        )
                                                        Text(
                                                            text = if (isNepali) "डेमो कोड: ८४९२०१ (स्वतः भरिएको)" else "Demo Code: 849201",
                                                            color = TextSecondary,
                                                            fontSize = 11.sp
                                                        )
                                                    }
                                                }

                                                OutlinedTextField(
                                                    value = loginOtpInput,
                                                    onValueChange = { loginOtpInput = it },
                                                    label = { Text("६ अङ्कको ओटिपी (OTP)") },
                                                    leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, tint = SecondaryAmber) },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .testTag("input_login_otp"),
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                    singleLine = true,
                                                    shape = RoundedCornerShape(10.dp)
                                                )

                                                Button(
                                                    onClick = {
                                                        val user = pendingLoginUser ?: BazarRepository.defaultAdminUser
                                                        BazarRepository.persistUser(user, context)
                                                        onAuthenticationSuccess(user)
                                                    },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(48.dp)
                                                        .testTag("btn_verify_login_otp"),
                                                    colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                                                    shape = RoundedCornerShape(10.dp)
                                                ) {
                                                    Text(
                                                        text = if (isNepali) "प्रमाणित गरी पोर्टल खोल्नुहोस्" else "Verify & Open Portal",
                                                        fontWeight = FontWeight.Bold,
                                                        color = DarkBackground,
                                                        fontSize = 14.sp
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Forgot Password Link
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(
                                            onClick = { currentSection = AuthNavSection.RECOVERY },
                                            modifier = Modifier.testTag("link_to_recovery")
                                        ) {
                                            Text(
                                                text = if (isNepali) "🔐 पासवर्ड बिर्सनुभयो?" else "🔐 Forgot Password?",
                                                color = SecondaryAmber,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Dedicated Isolated Sign-Up Switcher (Requirement 1: login माथि, sign up तल)
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, SecondaryAmber.copy(alpha = 0.4f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = if (isNepali) "नयाँ हुनुहुन्छ? अहिलेसम्म खाता खोल्नुभएको छैन?" else "New to Open Bazar?",
                                        color = TextSecondary,
                                        fontSize = 13.sp
                                    )
                                    Button(
                                        onClick = { currentSection = AuthNavSection.REGISTER },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(46.dp)
                                            .testTag("btn_switch_to_register"),
                                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = if (isNepali) "📝 नयाँ खाता खोल्नुहोस् (Sign Up)" else "Sign Up for Account",
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

                // =============================================================
                // 2. REGISTRATION SECTION (२. दर्ता: ग्राहक वा व्यापारी)
                // =============================================================
                AuthNavSection.REGISTER -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        item {
                            // Role Switcher: Customer vs Merchant
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkSurface)
                                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                                    .padding(4.dp)
                            ) {
                                val isCust = registrationTargetRole == UserRole.CUSTOMER
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isCust) AccentEmerald else Color.Transparent)
                                        .clickable { registrationTargetRole = UserRole.CUSTOMER }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = if (isCust) DarkBackground else TextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = if (isNepali) "ग्राहक दर्ता" else "Customer",
                                            color = if (isCust) DarkBackground else TextSecondary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                val isMer = registrationTargetRole == UserRole.MERCHANT
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isMer) PrimaryBlue else Color.Transparent)
                                        .clickable { registrationTargetRole = UserRole.MERCHANT }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(
                                            Icons.Default.Storefront,
                                            contentDescription = null,
                                            tint = if (isMer) DarkBackground else TextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = if (isNepali) "व्यापारी दर्ता" else "Merchant",
                                            color = if (isMer) DarkBackground else TextSecondary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }

                        // -----------------------------------------------------
                        // 2A. CUSTOMER REGISTRATION (ग्राहक दर्ता + अनिवार्य नक्सा)
                        // -----------------------------------------------------
                        if (registrationTargetRole == UserRole.CUSTOMER) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, AccentEmerald.copy(alpha = 0.4f))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(18.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            text = if (isNepali) "ग्राहक विवरण तथा स्थान छनोट" else "Customer Details & Location",
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )

                                        // Profile Avatar
                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(
                                                text = if (isNepali) "प्रोफाइल अवतार रोज्नुहोस्:" else "Select Avatar:",
                                                color = TextTertiary,
                                                fontSize = 12.sp
                                            )
                                            val avatars = listOf("👤", "👩", "👨", "🧑", "👵", "🧓")
                                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                avatars.forEach { av ->
                                                    val isSel = custSelectedAvatar == av
                                                    Box(
                                                        modifier = Modifier
                                                            .size(42.dp)
                                                            .clip(CircleShape)
                                                            .background(if (isSel) AccentEmerald.copy(alpha = 0.25f) else DarkSurfaceVariant)
                                                            .border(
                                                                if (isSel) 2.dp else 1.dp,
                                                                if (isSel) AccentEmerald else DarkBorder,
                                                                CircleShape
                                                            )
                                                            .clickable { custSelectedAvatar = av },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(av, fontSize = 20.sp)
                                                    }
                                                }
                                            }
                                        }

                                        // Full Name
                                        OutlinedTextField(
                                            value = custName,
                                            onValueChange = { custName = it },
                                            label = { Text(if (isNepali) "पूरा नाम" else "Full Name") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp)
                                        )

                                        // Email
                                        OutlinedTextField(
                                            value = custEmail,
                                            onValueChange = { custEmail = it },
                                            label = { Text(if (isNepali) "इमेल ठेगाना" else "Email") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp)
                                        )

                                        // Country Selector + Phone
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(DarkSurfaceVariant)
                                                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                                                    .clickable { showCountryDialogFor = "CUSTOMER" }
                                                    .padding(horizontal = 10.dp, vertical = 14.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Text(custSelectedCountry.flag, fontSize = 16.sp)
                                                    Text(custSelectedCountry.code, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = SecondaryAmber)
                                                }
                                            }

                                            OutlinedTextField(
                                                value = custPhone,
                                                onValueChange = { custPhone = it },
                                                label = { Text(if (isNepali) "१० अङ्कको मोबाइल नम्बर" else "10-digit Phone") },
                                                modifier = Modifier.weight(1f),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                                singleLine = true,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                        }

                                        // Address
                                        OutlinedTextField(
                                            value = custAddress,
                                            onValueChange = { custAddress = it },
                                            label = { Text(if (isNepali) "स्थानीय ठेगाना (टोल/सडक)" else "Local Address") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp)
                                        )

                                        // MANDATORY NEPAL MAP PICKER (Requirement 2: नेपालको नक्सामा अनिवार्य रातो पिन)
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(1.dp, if (custMapCoordinates != null) AccentEmerald else Color(0xFFDC2626), RoundedCornerShape(12.dp)),
                                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                                                        Text(
                                                            text = if (isNepali) "नेपालको नक्सामा स्थान छनोट" else "Nepal Map Location",
                                                            color = TextPrimary,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 13.sp
                                                        )
                                                    }
                                                    Box(
                                                        modifier = Modifier
                                                            .background(Color(0xFFDC2626).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text("अनिवार्य", color = Color(0xFFEF4444), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }

                                                Text(
                                                    text = if (isNepali) "नक्सा खोलेर आफू रहेको एरियामा ट्याप गरी रातो पिन लगाउनुहोस्।" else "Select your area in Nepal map with red pin marker.",
                                                    color = TextSecondary,
                                                    fontSize = 11.sp
                                                )

                                                Button(
                                                    onClick = { showNepalMapPickerFor = "CUSTOMER" },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Icon(Icons.Default.Map, contentDescription = null, tint = Color.White)
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = if (isNepali) "📍 नेपालको नक्सा खोल्नुहोस् (रातो पिन)" else "Open Nepal Map (Red Pin)",
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp
                                                    )
                                                }

                                                if (custMapCoordinates != null) {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(AccentEmerald.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                                            .border(1.dp, AccentEmerald.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                                            .padding(8.dp)
                                                    ) {
                                                        Text(
                                                            text = "✅ नक्सामा स्थान चिन्हित: ${custMapLocationName ?: "नेपाल"} [${custMapCoordinates}] (रातो पिन लाग्यो)",
                                                            color = AccentEmerald,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                    }
                                                } else {
                                                    Text("⚠️ नक्सामा स्थान छनोट गर्न बाँकी छ।", color = Color(0xFFEF4444), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                                }
                                            }
                                        }

                                        // Password
                                        OutlinedTextField(
                                            value = custPassword,
                                            onValueChange = { custPassword = it },
                                            label = { Text(if (isNepali) "पासवर्ड बनाउनुहोस्" else "Create Password") },
                                            modifier = Modifier.fillMaxWidth(),
                                            visualTransformation = PasswordVisualTransformation(),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp)
                                        )

                                        // Customer OTP
                                        if (isCustOtpPending) {
                                            HorizontalDivider(color = DarkBorder)
                                            Text(
                                                text = if (isNepali) "मोबाइल ओटिपी (डेमो कोड: ६२९१४५)" else "SMS OTP (Demo: 629145)",
                                                color = AccentEmerald,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            OutlinedTextField(
                                                value = custOtpInput,
                                                onValueChange = { custOtpInput = it },
                                                label = { Text("६ अङ्कको ओटिपी") },
                                                modifier = Modifier.fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                singleLine = true,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                if (custName.isBlank()) {
                                                    Toast.makeText(context, "कृपया आफ्नो नाम हाल्नुहोस्", Toast.LENGTH_SHORT).show()
                                                    return@Button
                                                }
                                                if (custPhone.length < 8) {
                                                    Toast.makeText(context, "कृपया १० अङ्कको मोबाइल नम्बर हाल्नुहोस्", Toast.LENGTH_SHORT).show()
                                                    return@Button
                                                }
                                                // Check mandatory map selection
                                                if (custMapCoordinates == null) {
                                                    Toast.makeText(context, "📍 नेपालको नक्सामा आफ्नो एरिया छानेर रातो पिन अनिवार्य लगाउनुहोस्।", Toast.LENGTH_LONG).show()
                                                    return@Button
                                                }
                                                if (!isCustOtpPending) {
                                                    isCustOtpPending = true
                                                    custOtpInput = demoCustOtp
                                                    Toast.makeText(context, "ओटिपी पठाइयो (कोड: ६२९१४५)", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    val newCust = UserProfile(
                                                        id = "usr-cus-${System.currentTimeMillis() % 10000}",
                                                        name = custName.trim(),
                                                        email = custEmail.trim().ifBlank { "${custPhone.trim()}@customer.com" },
                                                        phone = "${custSelectedCountry.code}-${custPhone.trim()}",
                                                        role = UserRole.CUSTOMER,
                                                        countryCode = custSelectedCountry.code,
                                                        countryName = custSelectedCountry.nameNp,
                                                        address = custAddress.trim().ifBlank { custMapLocationName ?: "काठमाडौँ, नेपाल" },
                                                        mapCoordinates = custMapCoordinates,
                                                        mapLocationName = custMapLocationName,
                                                        avatarUrl = custSelectedAvatar,
                                                        isVerified = true
                                                    )
                                                    BazarRepository.persistUser(newCust, context)
                                                    onAuthenticationSuccess(newCust)
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(
                                                text = if (!isCustOtpPending) (if (isNepali) "ओटिपी पठाउनुहोस् ->" else "Send OTP ->") else (if (isNepali) "दर्ता पूरा गर्नुहोस्" else "Complete Registration"),
                                                fontWeight = FontWeight.Bold,
                                                color = DarkBackground,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // -----------------------------------------------------
                        // 2B. MERCHANT REGISTRATION (८ मुख्य विधा र अनिवार्य नक्सा)
                        // -----------------------------------------------------
                        if (registrationTargetRole == UserRole.MERCHANT) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.4f))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(18.dp),
                                        verticalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        Text(
                                            text = if (isNepali) "व्यापारी दर्ता फारम (चरण ${merStep}/२)" else "Merchant Registration (Step ${merStep}/2)",
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )

                                        if (merStep == 1) {
                                            OutlinedTextField(
                                                value = merName,
                                                onValueChange = { merName = it },
                                                label = { Text("व्यापारी / सञ्चालकको नाम") },
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true,
                                                shape = RoundedCornerShape(12.dp)
                                            )

                                            OutlinedTextField(
                                                value = merBusinessName,
                                                onValueChange = { merBusinessName = it },
                                                label = { Text("पसल वा व्यवसायको नाम") },
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true,
                                                shape = RoundedCornerShape(12.dp)
                                            )

                                            // TWO-TIER CATEGORY SELECTOR (Requirement 4: ८ मुख्य वर्ग र उप-वर्ग)
                                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(
                                                    text = "१. मुख्य वर्ग छान्नुहोस् (८ वटा मध्ये):",
                                                    color = PrimaryBlue,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(DarkSurfaceVariant)
                                                        .border(1.dp, PrimaryBlue.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                                        .clickable { showMainCategoryModal = true }
                                                        .padding(horizontal = 14.dp, vertical = 13.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                            Text(merSelectedMainCategory.emoji, fontSize = 20.sp)
                                                            Text(
                                                                text = "${merSelectedMainCategory.id}. ${merSelectedMainCategory.nameNp}",
                                                                color = TextPrimary,
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 13.sp
                                                            )
                                                        }
                                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = PrimaryBlue)
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(4.dp))

                                                Text(
                                                    text = "२. उप-वर्ग छान्नुहोस् (Subcategory):",
                                                    color = AccentEmerald,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(DarkSurfaceVariant)
                                                        .border(1.dp, AccentEmerald.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                                        .clickable { showSubcategoryModal = true }
                                                        .padding(horizontal = 14.dp, vertical = 13.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            text = "• ${merSelectedSubcategory}",
                                                            color = AccentEmerald,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 13.sp
                                                        )
                                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = AccentEmerald)
                                                    }
                                                }
                                            }

                                            // Country Code + Phone
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(DarkSurfaceVariant)
                                                        .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                                                        .clickable { showCountryDialogFor = "MERCHANT" }
                                                        .padding(horizontal = 10.dp, vertical = 14.dp)
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                        Text(merSelectedCountry.flag, fontSize = 16.sp)
                                                        Text(merSelectedCountry.code, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = SecondaryAmber)
                                                    }
                                                }

                                                OutlinedTextField(
                                                    value = merPhone,
                                                    onValueChange = { merPhone = it },
                                                    label = { Text("सम्पर्क फोन / मोबाइल") },
                                                    modifier = Modifier.weight(1f),
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                                    singleLine = true,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                            }

                                            OutlinedTextField(
                                                value = merPanVat,
                                                onValueChange = { merPanVat = it },
                                                label = { Text("PAN / VAT नम्बर (ऐच्छिक)") },
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true,
                                                shape = RoundedCornerShape(12.dp)
                                            )

                                            Button(
                                                onClick = {
                                                    if (merName.isBlank() || merBusinessName.isBlank()) {
                                                        Toast.makeText(context, "कृपया व्यापारी र पसलको नाम लेख्नुहोस्", Toast.LENGTH_SHORT).show()
                                                        return@Button
                                                    }
                                                    merStep = 2
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text("अर्को: नेपालको नक्सा र सुरक्षा ->", fontWeight = FontWeight.Bold, color = DarkBackground)
                                            }
                                        }

                                        if (merStep == 2) {
                                            // MANDATORY NEPAL MAP PICKER
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .border(1.dp, if (merMapCoordinates != null) AccentEmerald else Color(0xFFDC2626), RoundedCornerShape(12.dp)),
                                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                                                            Text("नेपालको नक्सामा पसलको स्थान (अनिवार्य)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                        }
                                                        Box(
                                                            modifier = Modifier
                                                                .background(Color(0xFFDC2626).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Text("अनिवार्य", color = Color(0xFFEF4444), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }

                                                    Text(
                                                        text = "पसल रहेको एरिया वा बजार नक्सामा खोलेर रातो पिन लगाउनुहोस्।",
                                                        color = TextSecondary,
                                                        fontSize = 11.sp
                                                    )

                                                    Button(
                                                        onClick = { showNepalMapPickerFor = "MERCHANT" },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Icon(Icons.Default.Map, contentDescription = null, tint = Color.White)
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text("📍 नेपालको नक्सा खोल्नुहोस् (रातो पिन)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    }

                                                    if (merMapCoordinates != null) {
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .background(AccentEmerald.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                                                .border(1.dp, AccentEmerald.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                                                .padding(8.dp)
                                                        ) {
                                                            Text(
                                                                text = "✅ नक्सामा पसल चिन्हित: ${merMapLocationName} [${merMapCoordinates}] (रातो पिन लाग्यो)",
                                                                color = AccentEmerald,
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.SemiBold
                                                            )
                                                        }
                                                    } else {
                                                        Text("⚠️ पसलको नक्सा लोकेसन छनोट गर्न बाँकी छ।", color = Color(0xFFEF4444), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                                    }
                                                }
                                            }

                                            OutlinedTextField(
                                                value = merAddress,
                                                onValueChange = { merAddress = it },
                                                label = { Text("पसलको पूरा ठेगाना (टोल/सडक/चोक)") },
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true,
                                                shape = RoundedCornerShape(12.dp)
                                            )

                                            OutlinedTextField(
                                                value = merPassword,
                                                onValueChange = { merPassword = it },
                                                label = { Text("पासवर्ड बनाउनुहोस्") },
                                                modifier = Modifier.fillMaxWidth(),
                                                visualTransformation = PasswordVisualTransformation(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                                singleLine = true,
                                                shape = RoundedCornerShape(12.dp)
                                            )

                                            if (isMerOtpPending) {
                                                HorizontalDivider(color = DarkBorder)
                                                Text("ओटिपी (डेमो कोड: ७३८१९२)", color = SecondaryAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                OutlinedTextField(
                                                    value = merOtpInput,
                                                    onValueChange = { merOtpInput = it },
                                                    label = { Text("६ अङ्कको ओटिपी") },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                    singleLine = true,
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                            }

                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                OutlinedButton(
                                                    onClick = { merStep = 1 },
                                                    modifier = Modifier.weight(1f),
                                                    shape = RoundedCornerShape(12.dp)
                                                ) {
                                                    Text("← पछाडि")
                                                }

                                                Button(
                                                    onClick = {
                                                        if (merMapCoordinates == null) {
                                                            Toast.makeText(context, "📍 नेपालको नक्सामा पसलको एरिया छानी रातो पिन अनिवार्य लगाउनुहोस्।", Toast.LENGTH_LONG).show()
                                                            return@Button
                                                        }
                                                        if (!isMerOtpPending) {
                                                            isMerOtpPending = true
                                                            merOtpInput = demoMerOtp
                                                            Toast.makeText(context, "ओटिपी पठाइयो (कोड: ७३८१९२)", Toast.LENGTH_SHORT).show()
                                                        } else {
                                                            val newMer = UserProfile(
                                                                id = "usr-mer-${System.currentTimeMillis() % 10000}",
                                                                name = merName.trim(),
                                                                email = "${merPhone.trim()}@merchant.openbazar.com",
                                                                phone = "${merSelectedCountry.code}-${merPhone.trim()}",
                                                                role = UserRole.MERCHANT,
                                                                businessName = merBusinessName.trim(),
                                                                businessCategory = "${merSelectedMainCategory.nameNp} (${merSelectedSubcategory})",
                                                                panVat = merPanVat.trim().ifBlank { null },
                                                                address = merAddress.trim().ifBlank { merMapLocationName ?: "काठमाडौँ" },
                                                                mapCoordinates = merMapCoordinates,
                                                                mapLocationName = merMapLocationName,
                                                                countryCode = merSelectedCountry.code,
                                                                countryName = merSelectedCountry.nameNp,
                                                                isVerified = true
                                                            )
                                                            BazarRepository.persistUser(newMer, context)
                                                            onAuthenticationSuccess(newMer)
                                                        }
                                                    },
                                                    modifier = Modifier.weight(1.5f),
                                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                                    shape = RoundedCornerShape(12.dp)
                                                ) {
                                                    Text(
                                                        text = if (!isMerOtpPending) "ओटिपी ->" else "दर्ता पूरा गर्नुहोस्",
                                                        fontWeight = FontWeight.Bold,
                                                        color = DarkBackground
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // =============================================================
                // 3. PASSWORD RECOVERY (३. पासवर्ड रिकभरी)
                // =============================================================
                AuthNavSection.RECOVERY -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(18.dp),
                                border = BorderStroke(1.dp, SecondaryAmber.copy(alpha = 0.4f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(SecondaryAmber.copy(alpha = 0.15f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.LockReset, contentDescription = null, tint = SecondaryAmber)
                                        }
                                        Column {
                                            Text(
                                                text = if (isNepali) "पासवर्ड रिसेट गर्नुहोस्" else "Password Recovery",
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 17.sp
                                            )
                                            Text(
                                                text = if (isNepali) "पहिले दर्ता भएको इमेल अनिवार्य चाहिन्छ" else "Registered email is required",
                                                color = TextSecondary,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }

                                    HorizontalDivider(color = DarkBorder)

                                    OutlinedTextField(
                                        value = forgotEmail,
                                        onValueChange = { forgotEmail = it },
                                        label = { Text("दर्ता भएको इमेल") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    if (!isForgotOtpSent) {
                                        Button(
                                            onClick = {
                                                isForgotOtpSent = true
                                                forgotOtpInput = demoForgotOtp
                                                Toast.makeText(context, "रिसेट ओटिपी पठाइयो (कोड: ५१८९२४)", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("इमेलमा ओटिपी पठाउनुहोस्", fontWeight = FontWeight.Bold, color = DarkBackground)
                                        }
                                    }

                                    if (isForgotOtpSent && !isForgotOtpVerified) {
                                        OutlinedTextField(
                                            value = forgotOtpInput,
                                            onValueChange = { forgotOtpInput = it },
                                            label = { Text("६ अङ्कको रिसेट ओटिपी (डेमो: ५१८९२४)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp)
                                        )

                                        Button(
                                            onClick = {
                                                isForgotOtpVerified = true
                                                Toast.makeText(context, "ओटिपी प्रमाणित भयो!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("ओटिपी प्रमाणित गर्नुहोस्", fontWeight = FontWeight.Bold, color = DarkBackground)
                                        }
                                    }

                                    if (isForgotOtpVerified) {
                                        OutlinedTextField(
                                            value = forgotNewPassword,
                                            onValueChange = { forgotNewPassword = it },
                                            label = { Text("नयाँ पासवर्ड बनाउनुहोस्") },
                                            modifier = Modifier.fillMaxWidth(),
                                            visualTransformation = PasswordVisualTransformation(),
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp)
                                        )

                                        Button(
                                            onClick = {
                                                if (forgotNewPassword.length < 6) {
                                                    Toast.makeText(context, "पासवर्ड कम्तिमा ६ अक्षरको हुनुपर्छ", Toast.LENGTH_SHORT).show()
                                                    return@Button
                                                }
                                                BazarRepository.resetPassword(forgotEmail, forgotNewPassword)
                                                loginEmail = forgotEmail
                                                loginPassword = forgotNewPassword
                                                Toast.makeText(context, "नयाँ पासवर्ड सेट भयो! लगइन गर्नुहोस्।", Toast.LENGTH_SHORT).show()
                                                currentSection = AuthNavSection.LOGIN
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("नयाँ पासवर्ड सेभ गरी लगइनमा जानुहोस्", fontWeight = FontWeight.Bold, color = DarkBackground)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // =========================================================================
    // MODAL 1: 8 MAIN CATEGORIES PICKER (८ मुख्य वर्ग)
    // =========================================================================
    if (showMainCategoryModal) {
        AlertDialog(
            onDismissRequest = { showMainCategoryModal = false },
            title = {
                Text(
                    text = "८ मुख्य व्यवसाय विधा छान्नुहोस्",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(BAZAR_EIGHT_CATEGORIES) { cat ->
                        val isSelected = merSelectedMainCategory.id == cat.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    merSelectedMainCategory = cat
                                    merSelectedSubcategory = cat.subcategoriesNp[0]
                                    showMainCategoryModal = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) PrimaryBlue.copy(alpha = 0.2f) else DarkSurfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) PrimaryBlue else DarkBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(cat.emoji, fontSize = 22.sp)
                                    Column {
                                        Text(
                                            text = "${cat.id}. ${cat.nameNp}",
                                            color = if (isSelected) PrimaryBlue else TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(cat.nameEn, color = TextTertiary, fontSize = 11.sp)
                                    }
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMainCategoryModal = false }) {
                    Text("बन्द गर्नुहोस्", color = TextSecondary)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }

    // =========================================================================
    // MODAL 2: SUBCATEGORY PICKER (उप-वर्ग छनोट)
    // =========================================================================
    if (showSubcategoryModal) {
        AlertDialog(
            onDismissRequest = { showSubcategoryModal = false },
            title = {
                Text(
                    text = "${merSelectedMainCategory.nameNp} - उप-वर्ग छान्नुहोस्",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(merSelectedMainCategory.subcategoriesNp) { sub ->
                        val isSelected = merSelectedSubcategory == sub
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    merSelectedSubcategory = sub
                                    showSubcategoryModal = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) AccentEmerald.copy(alpha = 0.2f) else DarkSurfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) AccentEmerald else DarkBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = sub,
                                    color = if (isSelected) AccentEmerald else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSubcategoryModal = false }) {
                    Text("बन्द गर्नुहोस्", color = TextSecondary)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }

    // =========================================================================
    // MODAL 3: MANDATORY REAL NEPAL MAP LOCATION PICKER WITH RED PIN
    // =========================================================================
    if (showNepalMapPickerFor != null) {
        Dialog(
            onDismissRequest = { showNepalMapPickerFor = null },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0B1220))
            ) {
                NepalInteractiveMapScreen(
                    onBack = { showNepalMapPickerFor = null },
                    onLocationSelected = { lat, lng, address ->
                        val coords = "$lat° N, $lng° E"
                        if (showNepalMapPickerFor == "CUSTOMER") {
                            custMapCoordinates = coords
                            custMapLocationName = address
                        } else {
                            merMapCoordinates = coords
                            merMapLocationName = address
                        }
                        showNepalMapPickerFor = null
                        Toast.makeText(context, "✅ नेपालको नक्सामा स्थान सुरक्षित भयो!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    // =========================================================================
    // MODAL 4: COUNTRY SELECTOR
    // =========================================================================
    if (showCountryDialogFor != null) {
        AlertDialog(
            onDismissRequest = { showCountryDialogFor = null },
            title = {
                Text("देश तथा कन्ट्री कोड छान्नुहोस्", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(supportedCountries) { c ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    if (showCountryDialogFor == "CUSTOMER") custSelectedCountry = c
                                    else merSelectedCountry = c
                                    showCountryDialogFor = null
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(c.flag, fontSize = 20.sp)
                                Text(c.nameNp, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Text(c.code, color = SecondaryAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCountryDialogFor = null }) {
                    Text("बन्द गर्नुहोस्", color = TextSecondary)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Free AI Assistant Dialog
    if (showAiDialog) {
        AiAssistantDialog(onDismissRequest = { showAiDialog = false })
    }
}
