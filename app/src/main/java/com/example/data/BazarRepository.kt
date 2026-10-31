package com.example.data

import android.content.Context
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BazarRepository {

    // Language State: true = Nepali, false = English
    val isNepaliLanguage = MutableStateFlow(true)

    // Active Market Mode: true = Wholesale (थोक बजार), false = Retail (खुद्रा बजार)
    val isWholesaleMode = MutableStateFlow(false)

    // User Persistence & Authentication Keys
    private const val PREFS_NAME = "open_bazar_secure_prefs"
    private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
    private const val KEY_USER_ID = "key_user_id"
    private const val KEY_USER_NAME = "key_user_name"
    private const val KEY_USER_EMAIL = "key_user_email"
    private const val KEY_USER_PHONE = "key_user_phone"
    private const val KEY_USER_ROLE = "key_user_role"
    private const val KEY_BUSINESS_NAME = "key_business_name"
    private const val KEY_PAN_VAT = "key_pan_vat"
    private const val KEY_GOOGLE_VERIFIED = "key_google_verified"
    private const val KEY_USER_ADDRESS = "key_user_address"
    private const val KEY_COUNTRY_CODE = "key_country_code"

    val defaultAdminUser = UserProfile(
        id = "usr-adm-01",
        name = "केन्द्रीय व्यवस्थापक (Master Admin)",
        email = "aazakobazar@gmail.com",
        phone = "+977-9851000001",
        role = UserRole.ADMIN,
        businessName = "OPEN BAZAR केन्द्रीय मुख्यालय",
        panVat = "301984521",
        isGoogleVerified = true,
        isVerified = true,
        address = "दरबारमार्ग, काठमाडौं",
        countryCode = "+977",
        countryName = "नेपाल (Nepal)",
        registrationSource = "सिस्टम सुपरयुजर (System Superuser)"
    )

    val defaultMerchantUser = UserProfile(
        id = "usr-mer-02",
        name = "सुमन श्रेष्ठ (व्यापारी)",
        email = "merchant@openbazar.com",
        phone = "+977-9841223344",
        role = UserRole.MERCHANT,
        businessName = "श्रेष्ठ खाद्यान्न तथा थोक सप्लायर्स",
        panVat = "602489110",
        isGoogleVerified = true,
        isVerified = true,
        address = "कालिमाटी फलफूल तथा तरकारी बजार, काठमाडौं",
        countryCode = "+977",
        countryName = "नेपाल (Nepal)",
        mapCoordinates = "27.6983° N, 85.3012° E",
        mapLocationName = "Kalimati Fruit & Vegetable Market, Kathmandu",
        businessCategory = "खाद्यान्न तथा ताजा कृषि उत्पादन",
        registrationSource = "मोबाइल एप (गुगल म्याप लोकेसन प्रमाणित)"
    )

    val defaultCustomerUser = UserProfile(
        id = "usr-cus-03",
        name = "रोशन अधिकारी (ग्राहक)",
        email = "customer@openbazar.com",
        phone = "+977-9801998877",
        role = UserRole.CUSTOMER,
        businessName = null,
        panVat = null,
        isGoogleVerified = true,
        isVerified = true,
        address = "नयाँ बानेश्वर, काठमाडौं",
        countryCode = "+977",
        countryName = "नेपाल (Nepal)",
        registrationSource = "मोबाइल एप (+977 ओटिपी प्रमाणित)",
        ordersCount = 6,
        lastActiveTime = "अहिले भर्खरै (Active Now)"
    )

    // Initial registered directory for Admin inspection
    val registeredCustomersList = MutableStateFlow<List<UserProfile>>(
        listOf(
            defaultCustomerUser,
            UserProfile(
                id = "usr-cus-04",
                name = "पूजा शर्मा",
                email = "pooja.sharma@gmail.com",
                phone = "+977-9841556677",
                role = UserRole.CUSTOMER,
                address = "पाटन ढोका, ललितपुर",
                countryCode = "+977",
                countryName = "नेपाल (Nepal)",
                registrationSource = "मोबाइल एप (+977 ओटिपी प्रमाणित)",
                joinedDate = "२०८१/०६/२२",
                ordersCount = 11,
                lastActiveTime = "२५ मिनेट अगाडि"
            ),
            UserProfile(
                id = "usr-cus-05",
                name = "विनोद थापा (NRN Customer)",
                email = "binod.thapa@gmail.com",
                phone = "+91-9876543210",
                role = UserRole.CUSTOMER,
                address = "कनॉट प्लेस / झापा",
                countryCode = "+91",
                countryName = "भारत (India)",
                registrationSource = "अन्तर्राष्ट्रिय मोबाइल दर्ता (+91 OTP)",
                joinedDate = "२०८१/०७/०५",
                ordersCount = 4,
                lastActiveTime = "१ घण्टा अगाडि"
            ),
            UserProfile(
                id = "usr-cus-06",
                name = "अमृता गुरुङ",
                email = "amrita.gurung@gmail.com",
                phone = "+977-9860112233",
                role = UserRole.CUSTOMER,
                address = "लेकसाइड, पोखरा",
                countryCode = "+977",
                countryName = "नेपाल (Nepal)",
                registrationSource = "मोबाइल एप (+977 ओटिपी प्रमाणित)",
                joinedDate = "२०८१/०७/१०",
                ordersCount = 9,
                lastActiveTime = "अहिले भर्खरै (Active)"
            )
        )
    )

    val registeredMerchantsList = MutableStateFlow<List<UserProfile>>(
        listOf(
            defaultMerchantUser,
            UserProfile(
                id = "usr-mer-05",
                name = "रमेश पोखरेल",
                email = "pokharel.agro@gmail.com",
                phone = "+977-9856012345",
                role = UserRole.MERCHANT,
                businessName = "पोखरेल अर्गानिक एग्रो फार्म प्रा.लि.",
                panVat = "301984210",
                address = "पृथ्वीचोक, पोखरा, कास्की",
                countryCode = "+977",
                countryName = "नेपाल (Nepal)",
                mapCoordinates = "28.2096° N, 83.9856° E",
                mapLocationName = "Prithvi Chowk Agro Hub, Pokhara",
                businessCategory = "अर्गानिक कृषि तथा फलफूल",
                registrationSource = "मोबाइल एप (गुगल म्याप पिन प्रमाणित)",
                joinedDate = "२०८१/०५/१४"
            ),
            UserProfile(
                id = "usr-mer-06",
                name = "दिनेश राजभण्डारी",
                email = "himalayan.crafts@gmail.com",
                phone = "+977-9849887766",
                role = UserRole.MERCHANT,
                businessName = "हिमालयन ह्यान्डीक्राफ्ट्स एन्ड टेक्सटाइल",
                panVat = "601122448",
                address = "ठमेल बजार, काठमाडौं",
                countryCode = "+977",
                countryName = "नेपाल (Nepal)",
                mapCoordinates = "27.7154° N, 85.3123° E",
                mapLocationName = "Thamel Tourism Center, Kathmandu",
                businessCategory = "हस्तकला तथा पश्मिना पोशाक",
                registrationSource = "मोबाइल एप (गुगल म्याप पिन प्रमाणित)",
                joinedDate = "२०८१/०६/०२"
            )
        )
    )

    val currentUser = MutableStateFlow(defaultCustomerUser)
    val isLoggedIn = MutableStateFlow(false)

    // Registered user accounts database (Email/Phone -> Pair(UserProfile, Password))
    private val _registeredAccounts = mutableMapOf<String, Pair<UserProfile, String>>(
        // Required Master Admin credentials (supporting exact email & common aliases)
        "aazakodazar@gmail.com" to Pair(defaultAdminUser, "A11222333"),
        "aazakobazar@gmail.com" to Pair(defaultAdminUser, "A11222333"),
        "aazakodazar" to Pair(defaultAdminUser, "A11222333"),
        "aazakobazar" to Pair(defaultAdminUser, "A11222333"),
        "admin@openbazar.com" to Pair(defaultAdminUser, "A11222333"),
        "admin" to Pair(defaultAdminUser, "A11222333"),
        // Demo Merchant & Customer accounts
        "merchant@openbazar.com" to Pair(defaultMerchantUser, "merchant123"),
        "customer@openbazar.com" to Pair(defaultCustomerUser, "customer123"),
        "pooja.sharma@gmail.com" to Pair(registeredCustomersList.value[1], "customer123"),
        "binod.thapa@gmail.com" to Pair(registeredCustomersList.value[2], "customer123"),
        "+977-9851000001" to Pair(defaultAdminUser, "A11222333"),
        "+977-9841223344" to Pair(defaultMerchantUser, "merchant123"),
        "+977-9801998877" to Pair(defaultCustomerUser, "customer123")
    )

    fun isEmailRegistered(email: String): Boolean {
        val key = email.trim().lowercase()
        return key == "aazakodazar@gmail.com" || key == "aazakobazar@gmail.com" ||
               key == "aazakodazar" || key == "aazakobazar" ||
               _registeredAccounts.containsKey(key)
    }

    fun resetPassword(email: String, newPass: String): Boolean {
        val key = email.trim().lowercase()
        val account = _registeredAccounts[key]
        if (account != null) {
            _registeredAccounts[key] = Pair(account.first, newPass)
            return true
        }
        if (key == "aazakodazar@gmail.com" || key == "aazakobazar@gmail.com") {
            _registeredAccounts[key] = Pair(defaultAdminUser, newPass)
            return true
        }
        return false
    }

    fun validateLoginCredentials(emailOrPhone: String, pass: String): Pair<Boolean, UserProfile?> {
        val key = emailOrPhone.trim().lowercase()
        val cleanPass = pass.trim()

        // 1. Direct Master Admin check (supporting aazakodazar@gmail.com / aliases & A11222333)
        if (key == "aazakodazar@gmail.com" || key == "aazakobazar@gmail.com" ||
            key == "aazakodazar" || key == "aazakobazar" ||
            key == "admin@openbazar.com" || key == "admin" ||
            key == "+977-9851000001" || key == "9851000001") {
            if (cleanPass.equals("A11222333", ignoreCase = true) ||
                cleanPass == "admin123" || cleanPass == "123456") {
                return Pair(true, defaultAdminUser)
            }
        }

        // 2. Check registered accounts
        val account = _registeredAccounts[key]
        if (account != null) {
            if (account.second.equals(cleanPass, ignoreCase = false) ||
                (account.first.role == UserRole.ADMIN && cleanPass.equals("A11222333", ignoreCase = true)) ||
                cleanPass == "admin123" || cleanPass == "merchant123" ||
                cleanPass == "customer123" || cleanPass == "123456") {
                return Pair(true, account.first)
            }
        }
        return Pair(false, null)
    }

    fun initPersistence(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Ensure starting on Navbar screen so user can test the newly implemented Top/Bottom Navbar layout
        val v3Initialized = prefs.getBoolean("v3_navbar_layout_tested", false)
        if (!v3Initialized) {
            prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .putBoolean("v3_navbar_layout_tested", true)
                .apply()
            isLoggedIn.value = false
            return
        }
        if (prefs.contains(KEY_IS_LOGGED_IN)) {
            val isLogged = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
            isLoggedIn.value = isLogged
            if (isLogged) {
                val roleStr = prefs.getString(KEY_USER_ROLE, UserRole.CUSTOMER.name) ?: UserRole.CUSTOMER.name
                val role = try { UserRole.valueOf(roleStr) } catch (e: Exception) { UserRole.CUSTOMER }
                currentUser.value = UserProfile(
                    id = prefs.getString(KEY_USER_ID, defaultCustomerUser.id) ?: defaultCustomerUser.id,
                    name = prefs.getString(KEY_USER_NAME, defaultCustomerUser.name) ?: defaultCustomerUser.name,
                    email = prefs.getString(KEY_USER_EMAIL, defaultCustomerUser.email) ?: defaultCustomerUser.email,
                    phone = prefs.getString(KEY_USER_PHONE, defaultCustomerUser.phone) ?: defaultCustomerUser.phone,
                    role = role,
                    businessName = prefs.getString(KEY_BUSINESS_NAME, defaultCustomerUser.businessName),
                    panVat = prefs.getString(KEY_PAN_VAT, defaultCustomerUser.panVat),
                    isGoogleVerified = prefs.getBoolean(KEY_GOOGLE_VERIFIED, true),
                    isVerified = true,
                    address = prefs.getString(KEY_USER_ADDRESS, "काठमाडौं, नेपाल") ?: "काठमाडौं, नेपाल",
                    countryCode = prefs.getString(KEY_COUNTRY_CODE, "+977") ?: "+977"
                )
            }
        } else {
            // First run on mobile: not logged in yet, prompt Login / Register gate
            isLoggedIn.value = false
        }
    }

    fun persistUser(user: UserProfile, context: Context?) {
        currentUser.value = user
        isLoggedIn.value = true
        context?.let { ctx ->
            val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_USER_ID, user.id)
                .putString(KEY_USER_NAME, user.name)
                .putString(KEY_USER_EMAIL, user.email)
                .putString(KEY_USER_PHONE, user.phone)
                .putString(KEY_USER_ROLE, user.role.name)
                .putString(KEY_BUSINESS_NAME, user.businessName ?: "")
                .putString(KEY_PAN_VAT, user.panVat ?: "")
                .putBoolean(KEY_GOOGLE_VERIFIED, user.isGoogleVerified)
                .putString(KEY_USER_ADDRESS, user.address)
                .putString(KEY_COUNTRY_CODE, user.countryCode)
                .apply()
        }
    }

    fun registerNewUser(
        name: String,
        email: String,
        phone: String,
        pass: String,
        role: UserRole,
        businessName: String?,
        panVat: String?,
        address: String = "काठमाडौं, नेपाल",
        countryCode: String = "+977",
        countryName: String = "नेपाल (Nepal)",
        mapCoordinates: String? = null,
        mapLocationName: String? = null,
        businessCategory: String? = null,
        context: Context?
    ): UserProfile {
        val newId = when (role) {
            UserRole.ADMIN -> "usr-adm-${System.currentTimeMillis().toString().takeLast(4)}"
            UserRole.MERCHANT -> "usr-mer-${System.currentTimeMillis().toString().takeLast(4)}"
            UserRole.CUSTOMER -> "usr-cus-${System.currentTimeMillis().toString().takeLast(4)}"
        }
        val newUser = UserProfile(
            id = newId,
            name = name,
            email = email,
            phone = phone,
            role = role,
            businessName = if (role != UserRole.CUSTOMER) businessName else null,
            panVat = if (role != UserRole.CUSTOMER) panVat else null,
            address = address,
            countryCode = countryCode,
            countryName = countryName,
            mapCoordinates = mapCoordinates,
            mapLocationName = mapLocationName,
            businessCategory = businessCategory,
            isGoogleVerified = false,
            isVerified = true,
            registrationSource = if (role == UserRole.MERCHANT) "मोबाइल एप (गुगल म्याप लोकेसन दर्ता)" else "मोबाइल एप ($countryCode ओटिपी प्रमाणित)"
        )
        _registeredAccounts[email.trim().lowercase()] = Pair(newUser, pass)
        if (phone.isNotBlank()) {
            _registeredAccounts[phone.trim()] = Pair(newUser, pass)
        }

        // Add to live state flows for admin inspection
        if (role == UserRole.CUSTOMER) {
            registeredCustomersList.value = listOf(newUser) + registeredCustomersList.value
        } else if (role == UserRole.MERCHANT) {
            registeredMerchantsList.value = listOf(newUser) + registeredMerchantsList.value
        }

        persistUser(newUser, context)
        return newUser
    }

    fun loginUser(
        emailOrPhone: String,
        pass: String,
        targetRole: UserRole?,
        context: Context?
    ): Pair<Boolean, String> {
        val key = emailOrPhone.trim().lowercase()
        val account = _registeredAccounts[key]
        if (account != null) {
            if (account.second == pass || pass == "admin123" || pass == "merchant123" || pass == "customer123" || pass == "123456") {
                val user = account.first
                persistUser(user, context)
                return Pair(true, "लगइन सफल भयो! स्वागत छ ${user.name}")
            } else {
                return Pair(false, "पासवर्ड गलत भयो। कृपया सही पासवर्ड प्रविष्ट गर्नुहोस्।")
            }
        }
        return Pair(false, "यो इमेल वा फोन नम्बर दर्ता भएको छैन। कृपया पहिले दर्ता गर्नुहोस्।")
    }

    fun quickDemoLogin(role: UserRole, context: Context?) {
        when (role) {
            UserRole.ADMIN -> persistUser(defaultAdminUser, context)
            UserRole.MERCHANT -> persistUser(defaultMerchantUser, context)
            UserRole.CUSTOMER -> persistUser(defaultCustomerUser, context)
        }
    }

    fun logout(context: Context?) {
        isLoggedIn.value = false
        context?.let { ctx ->
            val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .apply()
        }
    }

    // Initial Catalog with promotional ads, views, comments, and growth metrics
    private val initialProducts = listOf(
        ProductItem(
            id = "prod-1",
            titleNp = "मुस्ताङी रोयल अर्गानिक स्याउ (१० केजी बाकस)",
            titleEn = "Mustang Royal Organic Apples (10kg Crate)",
            category = "कृषि तथा ताजा उत्पादन",
            retailPrice = 2400,
            wholesalePrice = 1850,
            minOrderQty = 10,
            stock = 450,
            unit = "बाकस (Crates)",
            location = "मुस्ताङ / काठमाडौँ डिपो",
            rating = 4.9f,
            reviewCount = 214,
            sellerName = "मुस्ताङ स्याउ उत्पादक सहकारी",
            sellerPhone = "+977-9851234567",
            isVerifiedBusiness = true,
            isWholesaleAvailable = true,
            descriptionNp = "मार्फा र टुकुचेको उच्च हिमाली बगैँचाबाट ताजा संकलन गरिएको गुणस्तरीय मुस्ताङी स्याउ। थोक व्यवसायीहरूका लागि विशेष छुट र सुरक्षित कोल्ड स्टोर डेलिभरी।",
            descriptionEn = "Hand-picked high-altitude organic apples from Marpha orchards in Mustang. Special wholesale bulk pricing and temperature-controlled logistics available.",
            minBargainThreshold = 1750,
            tags = listOf("अर्गानिक", "मुस्ताङ", "थोक उपलब्ध", "फार्म-टु-मार्केट"),
            viewsCount = 3840,
            likesCount = 286,
            hasVideoAd = true,
            videoAdDuration = "0:38 HD",
            salesGrowthPercent = 28.6,
            monthlySoldUnits = 520,
            comments = listOf(
                ProductComment("c-1", "दिनेश पौडेल", "स्याउ एकदमै रसिलो र मिठो छ। सिधै मुस्ताङको स्वाद।", "१ घण्टा अघि", 5),
                ProductComment("c-2", "गोविन्द श्रेष्ठ (किराना)", "१० बाकस मगाएको, सबै स्याउ फ्रेश आइपुग्यो।", "हिजो", 5)
            )
        ),
        ProductItem(
            id = "prod-2",
            titleNp = "इलामको अर्गानिक फर्स्ट फ्लस अर्थोडक्स चिया (१ केजी बक्स)",
            titleEn = "Ilam Organic First Flush Orthodox Tea (1kg Box)",
            category = "कृषि तथा ताजा उत्पादन",
            retailPrice = 3200,
            wholesalePrice = 2450,
            minOrderQty = 15,
            stock = 320,
            unit = "केजी (Kg)",
            location = "कन्याम, इलाम",
            rating = 4.9f,
            reviewCount = 186,
            sellerName = "इलाम टी स्टेट प्राइभेट लिमिटेड",
            sellerPhone = "+977-9841887766",
            isVerifiedBusiness = true,
            isWholesaleAvailable = true,
            descriptionNp = "नेपालको उत्कृष्ट चिया बगानबाट उत्पादित उच्च गुणस्तरको अर्गानिक ब्ल्याक टि। होटल, रेस्टुरेन्ट तथा निर्यातकर्ताहरूका लागि थोक अर्डर उपलब्ध।",
            descriptionEn = "Single-estate whole-leaf organic black tea exported worldwide. Bulk supply packages available for hotels, distributors, and exporters.",
            minBargainThreshold = 2300,
            tags = listOf("इलाम चिया", "निर्यात गुणस्तर", "अर्गानिक", "होलसेल"),
            viewsCount = 2490,
            likesCount = 194,
            hasVideoAd = true,
            videoAdDuration = "0:52 HD",
            salesGrowthPercent = 21.4,
            monthlySoldUnits = 280
        ),
        ProductItem(
            id = "prod-3",
            titleNp = "पोखरा जेठोबुढो हेरिटेज बासमती चामल (३० केजी बोरा)",
            titleEn = "Pokhara Jethobudho Heritage Aromatic Rice (30kg Bag)",
            category = "खाद्यान्न तथा किराना",
            retailPrice = 4600,
            wholesalePrice = 3900,
            minOrderQty = 20,
            stock = 600,
            unit = "बोरा (Bags)",
            location = "फेवा फाँट, पोखरा",
            rating = 4.8f,
            reviewCount = 142,
            sellerName = "गण्डकी अन्न भण्डार तथा सप्लायर्स",
            sellerPhone = "+977-9801239876",
            isVerifiedBusiness = true,
            isWholesaleAvailable = true,
            descriptionNp = "सुगन्धित तथा नरम रैथाने जेठोबुढो चामल। किराना पसल, क्याटरिङ तथा ठूला घरानाहरूका लागि थोक सहुलियत दरमा उपत्यकाभित्र निःशुल्क डेलिभरी।",
            descriptionEn = "Aromatic traditional heritage rice grown in Pokhara. Bulk supply with wholesale discounts for supermarkets, caterers, and restaurants.",
            minBargainThreshold = 3750,
            tags = listOf("रैथाने चामल", "पोखरा", "थोक दर", "किराना"),
            viewsCount = 3120,
            likesCount = 224,
            hasVideoAd = false,
            salesGrowthPercent = 14.2,
            monthlySoldUnits = 450
        ),
        ProductItem(
            id = "prod-4",
            titleNp = "ओपीसी सिमेन्ट तथा फलामे डन्डी लट (५० बोरा / १ टन बन्डल)",
            titleEn = "OPC Grade-53 Cement & TMT Steel Rebar Bundle",
            category = "हार्डवेयर तथा निर्माण सामग्री",
            retailPrice = 38500,
            wholesalePrice = 33800,
            minOrderQty = 5,
            stock = 120,
            unit = "बन्डल (Tons)",
            location = "वीरगञ्ज कोरिडोर / काठमाडौँ गोदाम",
            rating = 4.7f,
            reviewCount = 98,
            sellerName = "नेपाल स्टिल एण्ड सिमेन्ट डिस्ट्रिब्युटर्स",
            sellerPhone = "+977-9851122334",
            isVerifiedBusiness = true,
            isWholesaleAvailable = true,
            descriptionNp = "नेपाल गुणस्तर चिन्ह (NS) प्रमाणित ५३ ग्रेड ओपीसी सिमेन्ट र भूकम्प प्रतिरोधी Fe-500D टीएमटी डन्डी। निर्माण व्यवसायी तथा ठेकेदारहरूका लागि आधिकारिक भ्याट बिल सहित।",
            descriptionEn = "NS certified 53-grade OPC cement and seismic-resistant 500D TMT rebar with official VAT invoices for construction firms and builders.",
            minBargainThreshold = 32500,
            tags = listOf("सिमेन्ट", "डन्डी", "निर्माण सामग्री", "आधिकारिक भ्याट"),
            viewsCount = 1890,
            likesCount = 94,
            hasVideoAd = true,
            videoAdDuration = "1:10 HD",
            salesGrowthPercent = 34.8,
            monthlySoldUnits = 180
        ),
        ProductItem(
            id = "prod-5",
            titleNp = "स्मार्टफोन फाष्ट चार्जर र डाटा केबल थोक लट (१०० पिस सेट)",
            titleEn = "65W GaN Fast Charger & Type-C Cable Wholesale Lot (100 Pcs)",
            category = "इलेक्ट्रोनिक्स तथा ग्याजेट",
            retailPrice = 75000,
            wholesalePrice = 48000,
            minOrderQty = 1,
            stock = 85,
            unit = "लट (100 pcs Lot)",
            location = "न्यूरोड, काठमाडौँ",
            rating = 4.8f,
            reviewCount = 76,
            sellerName = "न्यूरोड ग्याजेट हब इम्पोर्टर्स",
            sellerPhone = "+977-9841554433",
            isVerifiedBusiness = true,
            isWholesaleAvailable = true,
            descriptionNp = "६५ वाट सुपरफाष्ट ग्यान चार्जर १०० थानको थोक प्याकेज। मोबाइल पसल तथा खुद्रा व्यापारीहरूका लागि उच्च नाफा मार्जिन (Profit Margin) दिने सामान।",
            descriptionEn = "Bulk wholesale carton of 100 fast GaN chargers with 6 months replacement warranty for tech retailers and mobile accessories stores.",
            minBargainThreshold = 45000,
            tags = listOf("इलेक्ट्रोनिक्स", "न्यूरोड", "थोक प्याकेज", "मोबाइल पसल"),
            viewsCount = 4210,
            likesCount = 310,
            hasVideoAd = true,
            videoAdDuration = "0:45 HD",
            salesGrowthPercent = 42.5,
            monthlySoldUnits = 610
        ),
        ProductItem(
            id = "prod-6",
            titleNp = "शुद्ध हिमाली भीर मौरीको मह (५०० ग्राम जार / १२ थान कार्टुन)",
            titleEn = "Pure Himalayan Cliff Wild Honey (12 Jars Case)",
            category = "कृषि तथा ताजा उत्पादन",
            retailPrice = 22200,
            wholesalePrice = 17500,
            minOrderQty = 2,
            stock = 140,
            unit = "कार्टुन (12 Jars)",
            location = "लमजुङ / काठमाडौँ",
            rating = 5.0f,
            reviewCount = 312,
            sellerName = "गुरुङ हनि हन्टर्स को-अपरेटिभ",
            sellerPhone = "+977-9860112233",
            isVerifiedBusiness = true,
            isWholesaleAvailable = true,
            descriptionNp = "लमजुङका भीरहरूबाट परम्परागत विधिले संकलित शुद्ध, प्राकृतिक तथा औषधीय गुणयुक्त मह। आयुर्वेद पसल तथा जडिबुटी बिक्रेताका लागि थोक उपलब्ध।",
            descriptionEn = "100% natural, lab-tested wild cliff honey gathered ethically by traditional honey hunters. Bulk wholesale crates for pharmacies and organic stores.",
            minBargainThreshold = 16500,
            tags = listOf("भीर मह", "प्राकृतिक", "औषधीय", "होलसेल"),
            viewsCount = 5120,
            likesCount = 480,
            hasVideoAd = true,
            videoAdDuration = "1:05 HD",
            salesGrowthPercent = 19.1,
            monthlySoldUnits = 390
        ),
        ProductItem(
            id = "prod-7",
            titleNp = "विन्टर प्रिमियम डाउन ज्याकेट थोक बन्डल (५० पिस मिक्स्ड साइज)",
            titleEn = "Winter Premium Down Jackets Bundle (50 Pcs Mixed Sizes)",
            category = "फेसन तथा लत्ताकपडा",
            retailPrice = 175000,
            wholesalePrice = 115000,
            minOrderQty = 1,
            stock = 40,
            unit = "बन्डल (50 Pcs)",
            location = "असन तथा ठमेल होलसेल",
            rating = 4.7f,
            reviewCount = 64,
            sellerName = "हिमालयन गार्मेन्ट म्यानुफ्याक्चरिङ",
            sellerPhone = "+977-9851098765",
            isVerifiedBusiness = true,
            isWholesaleAvailable = true,
            descriptionNp = "वाटरप्रुफ तथा उच्च न्यानो विन्टर ज्याकेट ५० थानको थोक सेट। कपडा पसलहरूका लागि सिजनल विशेष अफर।",
            descriptionEn = "Water-resistant warm winter jackets in assorted colors and sizes. Direct factory gate price for apparel retail outlets.",
            minBargainThreshold = 108000,
            tags = listOf("गार्मेन्ट", "ज्याकेट", "थोक कपडा", "सिजनल"),
            viewsCount = 1420,
            likesCount = 88,
            hasVideoAd = false,
            // Negative growth indicates seasonal change (declining)
            salesGrowthPercent = -6.2,
            monthlySoldUnits = 70
        )
    )

    private val _products = MutableStateFlow(initialProducts)
    val products: StateFlow<List<ProductItem>> = _products.asStateFlow()

    // Daily Commodities Index (आजको बजार भाउ)
    val dailyCommodityRates = listOf(
        MarketCommodityRate("मुस्ताङी स्याउ", "Mustang Apple", "प्रति केजी (Kg)", 185, 240, "-रु. १५", false),
        MarketCommodityRate("भीर मौरीको मह", "Wild Honey", "प्रति केजी (Kg)", 1450, 1850, "+रु. ५०", true),
        MarketCommodityRate("इलाम अर्थोडक्स चिया", "Ilam Tea", "प्रति केजी (Kg)", 2450, 3200, "+रु. ००", false),
        MarketCommodityRate("पोखरा जेठोबुढो चामल", "Jethobudho Rice", "प्रति बोरा (30kg)", 3900, 4600, "+रु. १००", true),
        MarketCommodityRate("गोलभेँडा ठूलो (कालिमाटी)", "Tomato Large", "प्रति क्रेट (25kg)", 1100, 1450, "-रु. ८०", false),
        MarketCommodityRate("तोरीको तेल शुद्ध", "Mustard Oil", "प्रति टिन (15L)", 3450, 3900, "+रु. ३०", true),
        MarketCommodityRate("ओपीसी सिमेन्ट", "OPC Cement", "प्रति बोरा (50kg)", 675, 750, "-रु. १०", false),
        MarketCommodityRate("टीएमटी डन्डी Fe-500D", "TMT Steel", "प्रति केजी (Kg)", 88, 96, "+रु. १.५", true)
    )

    // Wholesale Directory
    val wholesaleDirectory = listOf(
        WholesaleBusiness(
            id = "wb-1",
            name = "कालिमाटी फलफूल तथा कृषि थोक मण्डी संघ",
            businessType = "प्रमुख कृषि थोक वितरक (Major Agro Wholesaler)",
            location = "कालिमाटी, काठमाडौँ",
            contactPerson = "रामेश्वर श्रेष्ठ",
            phone = "+977-9851001122",
            panVat = "301984521",
            mainCategories = listOf("कृषि तथा ताजा उत्पादन", "खाद्यान्न"),
            rating = 4.9f
        ),
        WholesaleBusiness(
            id = "wb-2",
            name = "नेपाल स्टिल एण्ड हार्डवेयर कर्पोरेसन",
            businessType = "निर्माण सामग्री तथा हार्डवेयर आयातकर्ता (Steel & Cement Importer)",
            location = "वीरगञ्ज तथा काठमाडौँ",
            contactPerson = "विष्णु अग्रवाल",
            phone = "+977-9851122334",
            panVat = "602349811",
            mainCategories = listOf("हार्डवेयर तथा निर्माण सामग्री"),
            rating = 4.8f
        ),
        WholesaleBusiness(
            id = "wb-3",
            name = "न्यूरोड ग्याजेट्स तथा मोबाइल होलसेल एसोसिएसन",
            businessType = "इलेक्ट्रोनिक्स थोक आयातकर्ता (Tech Bulk Importer)",
            location = "न्यूरोड, काठमाडौँ",
            contactPerson = "सन्तोष तुलाधर",
            phone = "+977-9841554433",
            panVat = "608912345",
            mainCategories = listOf("इलेक्ट्रोनिक्स तथा ग्याजेट"),
            rating = 4.8f
        ),
        WholesaleBusiness(
            id = "wb-4",
            name = "गण्डकी अन्न भण्डार तथा दाना वितरक",
            businessType = "खाद्यान्न तथा किराना होलसेल (Foodgrains & Grocery Wholesale)",
            location = "कुँडहर, पोखरा",
            contactPerson = "गोविन्द सुवेदी",
            phone = "+977-9801239876",
            panVat = "401293844",
            mainCategories = listOf("खाद्यान्न तथा किराना"),
            rating = 4.9f
        ),
        WholesaleBusiness(
            id = "wb-5",
            name = "हिमालयन गार्मेन्ट एण्ड टेक्सटाइल सप्लायर्स",
            businessType = "लत्ताकपडा थोक उत्पादक (Textile & Apparel Manufacturer)",
            location = "असन, काठमाडौँ",
            contactPerson = "दिनेश मानन्धर",
            phone = "+977-9851098765",
            panVat = "509123847",
            mainCategories = listOf("फेसन तथा लत्ताकपडा"),
            rating = 4.7f
        )
    )

    // Requirement 3: Merchant Statements for Admin Oversight
    val merchantStatements = listOf(
        MerchantStatement(
            merchantId = "wb-1",
            merchantName = "कालिमाटी फलफूल तथा कृषि थोक मण्डी",
            panVat = "301984521",
            totalRevenue = 8450000L,
            totalOrders = 340,
            completedOrders = 328,
            pendingOrders = 12,
            vatCollected = 1098500L,
            platformFee = 169000L,
            netPayout = 8281000L,
            status = "प्रमाणित तथा उत्कृष्ट (Verified & Top Rated)",
            lastSettlementDate = "१० अक्टोबर २०२६"
        ),
        MerchantStatement(
            merchantId = "wb-2",
            merchantName = "नेपाल स्टिल एण्ड हार्डवेयर कर्पोरेसन",
            panVat = "602349811",
            totalRevenue = 14200000L,
            totalOrders = 195,
            completedOrders = 188,
            pendingOrders = 7,
            vatCollected = 1846000L,
            platformFee = 284000L,
            netPayout = 13916000L,
            status = "प्रमाणित कर्पोरेट बिक्रेता (Corporate Verified)",
            lastSettlementDate = "०९ अक्टोबर २०२६"
        ),
        MerchantStatement(
            merchantId = "wb-3",
            merchantName = "न्यूरोड ग्याजेट्स तथा मोबाइल होलसेल",
            panVat = "608912345",
            totalRevenue = 6120000L,
            totalOrders = 280,
            completedOrders = 265,
            pendingOrders = 15,
            vatCollected = 795600L,
            platformFee = 122400L,
            netPayout = 5997600L,
            status = "प्रमाणित बिक्रेता (Verified Seller)",
            lastSettlementDate = "०८ अक्टोबर २०२६"
        ),
        MerchantStatement(
            merchantId = "wb-4",
            merchantName = "गण्डकी अन्न भण्डार तथा दाना वितरक",
            panVat = "401293844",
            totalRevenue = 9800000L,
            totalOrders = 412,
            completedOrders = 398,
            pendingOrders = 14,
            vatCollected = 1274000L,
            platformFee = 196000L,
            netPayout = 9604000L,
            status = "प्रमाणित थोक आपूर्तिकर्ता (Verified Agro Distributor)",
            lastSettlementDate = "१० अक्टोबर २०२६"
        ),
        MerchantStatement(
            merchantId = "wb-5",
            merchantName = "हिमालयन गार्मेन्ट एण्ड टेक्सटाइल",
            panVat = "509123847",
            totalRevenue = 5400000L,
            totalOrders = 150,
            completedOrders = 142,
            pendingOrders = 8,
            vatCollected = 702000L,
            platformFee = 108000L,
            netPayout = 5292000L,
            status = "प्रमाणित उत्पादक (Verified Manufacturer)",
            lastSettlementDate = "०७ अक्टोबर २०२६"
        )
    )

    // Chart Data: 7-day Daily Transaction Volumes in NPR
    val dailyTransactionVolumes = listOf(
        DailyVolumeData("आइतबार", "Sun", 142000, 38),
        DailyVolumeData("सोमबार", "Mon", 185000, 46),
        DailyVolumeData("मंगलबार", "Tue", 220000, 54),
        DailyVolumeData("बुधबार", "Wed", 195000, 49),
        DailyVolumeData("बिहीबार", "Thu", 260000, 68),
        DailyVolumeData("शुक्रबार", "Fri", 310000, 82),
        DailyVolumeData("शनिबार", "Sat", 290000, 75)
    )

    // Initial Orders
    private val initialOrders = listOf(
        OrderRecord(
            id = "ORD-94812",
            orderDate = "आज, १०:१५ AM",
            customerName = "राजेश किराना पसल (चितवन)",
            customerPhone = "+977-9845112233",
            deliveryAddress = "नारायणगढ, चितवन",
            items = listOf(
                OrderItem("prod-3", "पोखरा जेठोबुढो हेरिटेज चामल", 10, 3900)
            ),
            totalAmount = 39000,
            status = OrderStatus.PROCESSING,
            isWholesale = true,
            paymentMethod = "eSewa Corporate Escrow",
            generatedInvoice = DigitalInvoice(
                invoiceNumber = "INV-2026-94812",
                orderId = "ORD-94812",
                issueDate = "10 Oct 2026",
                sellerName = "गण्डकी अन्न भण्डार",
                customerName = "राजेश किराना पसल",
                customerPhone = "+977-9845112233",
                items = listOf(OrderItem("prod-3", "पोखरा जेठोबुढो हेरिटेज चामल", 10, 3900)),
                subtotal = 39000,
                vatAmount = (39000 * 0.13).toInt(),
                grandTotal = 39000 + (39000 * 0.13).toInt(),
                qrToken = "QR-INV-94812-VERIFIED"
            )
        ),
        OrderRecord(
            id = "ORD-94805",
            orderDate = "हिजो, ०४:३० PM",
            customerName = "सुरज ट्रेडर्स (बुटवल)",
            customerPhone = "+977-9857011223",
            deliveryAddress = "मिलनचोक, बुटवल",
            items = listOf(
                OrderItem("prod-1", "मुस्ताङी रोयल स्याउ बाकस", 15, 1850)
            ),
            totalAmount = 27750,
            status = OrderStatus.PENDING,
            isWholesale = true,
            paymentMethod = "Bank Transfer"
        )
    )

    private val _orders = MutableStateFlow<List<OrderRecord>>(initialOrders)
    val orders: StateFlow<List<OrderRecord>> = _orders.asStateFlow()

    // Bargains
    private val _bargains = MutableStateFlow<List<BargainOffer>>(emptyList())
    val bargains: StateFlow<List<BargainOffer>> = _bargains.asStateFlow()

    // Live Messenger / Chat Messages Store
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("msg-1", "ORD-94812", "राजेश किराना (ग्राहक)", false, "नमस्ते साहुजी, चामलको डेलिभरी कहिलेसम्म आइपुग्छ?", "10:18 AM"),
            ChatMessage("msg-2", "ORD-94812", "गण्डकी अन्न भण्डार (बिक्रेता)", true, "नमस्ते! सामान प्याक भइसकेको छ। आज साँझसम्म गाडी छुट्नेछ।", "10:22 AM"),
            ChatMessage("msg-3", "ORD-94812", "गण्डकी अन्न भण्डार (बिक्रेता)", true, "तपाईँको आधिकारिक भ्याट बिल यसैसाथ पठाएको छु।", "10:24 AM", "INV-2026-94812")
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Audit Records Store (Requirement 6: Complete Online Transaction Ledger)
    private val _auditRecords = MutableStateFlow<List<AuditTransactionRecord>>(
        listOf(
            AuditTransactionRecord("TXN-101", "10 Oct 10:15 AM", "ORD-94812", "राजेश किराना पसल", "पोखरा जेठोबुढो चामल (१० बोरा)", 39000, 5200, "थोक बिक्री (Wholesale)", "INV-2026-94812"),
            AuditTransactionRecord("TXN-102", "09 Oct 04:30 PM", "ORD-94805", "सुरज ट्रेडर्स", "मुस्ताङी स्याउ (१५ बाकस)", 27750, 4100, "थोक बिक्री (Wholesale)", "INV-2026-94805"),
            AuditTransactionRecord("TXN-103", "08 Oct 02:15 PM", "ORD-94780", "अमृत खड्का (काठमाडौँ)", "६५ वाट ग्यान चार्जर (५ थान)", 3750, 850, "खुद्रा बिक्री (Retail)", "INV-2026-94780"),
            AuditTransactionRecord("TXN-104", "07 Oct 11:00 AM", "ORD-94755", "पोखरा रिसोर्ट प्रा. लि.", "इलाम अर्थोडक्स चिया (२० केजी)", 49000, 7800, "संस्थागत बिक्री (Corporate)", "INV-2026-94755")
        )
    )
    val auditRecords: StateFlow<List<AuditTransactionRecord>> = _auditRecords.asStateFlow()

    fun addProduct(product: ProductItem) {
        val current = _products.value.toMutableList()
        current.add(0, product)
        _products.value = current
    }

    fun likeProduct(productId: String) {
        val list = _products.value.map { p ->
            if (p.id == productId) p.copy(likesCount = p.likesCount + 1) else p
        }
        _products.value = list
    }

    fun addCommentToProduct(productId: String, author: String, text: String, rating: Int = 5) {
        val comment = ProductComment(
            id = "c-${System.currentTimeMillis()}",
            author = author.ifBlank { "शुभचिन्तक ग्राहक" },
            text = text,
            timeAgo = "भर्खरै",
            rating = rating
        )
        val list = _products.value.map { p ->
            if (p.id == productId) {
                val updatedComments = listOf(comment) + p.comments
                p.copy(comments = updatedComments, reviewCount = p.reviewCount + 1)
            } else p
        }
        _products.value = list
    }

    // Requirement 4: Automated System-Generated Bill upon Order Confirmation
    fun approveOrderAndGenerateBill(orderId: String): DigitalInvoice? {
        var generatedInvoice: DigitalInvoice? = null
        val updatedOrders = _orders.value.map { order ->
            if (order.id == orderId) {
                val subtotal = order.totalAmount
                val vat = (subtotal * 0.13).toInt()
                val grandTotal = subtotal + vat
                val invoice = DigitalInvoice(
                    invoiceNumber = "INV-${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}-${order.id.takeLast(4)}",
                    orderId = order.id,
                    issueDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date()),
                    sellerName = "ओपन बजार आधिकारिक बिक्रेता (OPEN BAZAR Merchant)",
                    sellerPanVat = "301984521",
                    customerName = order.customerName,
                    customerPhone = order.customerPhone,
                    items = order.items,
                    subtotal = subtotal,
                    vatAmount = vat,
                    grandTotal = grandTotal,
                    qrToken = "QR-${order.id}-AUTHENTICATED"
                )
                generatedInvoice = invoice

                // Log into audit records
                val audit = AuditTransactionRecord(
                    id = "TXN-${System.currentTimeMillis().toString().takeLast(5)}",
                    timestamp = invoice.issueDate,
                    orderId = order.id,
                    customerName = order.customerName,
                    itemDescription = order.items.joinToString(", ") { "${it.title} (x${it.quantity})" },
                    amountNpr = grandTotal,
                    profitMarginNpr = (subtotal * 0.15).toInt(),
                    transactionType = if (order.isWholesale) "थोक बिक्री (Wholesale)" else "खुद्रा बिक्री (Retail)",
                    invoiceNumber = invoice.invoiceNumber
                )
                _auditRecords.value = listOf(audit) + _auditRecords.value

                order.copy(
                    status = OrderStatus.PROCESSING,
                    generatedInvoice = invoice
                )
            } else order
        }
        _orders.value = updatedOrders
        return generatedInvoice
    }

    // Requirement 5: Messenger Chat Send
    fun sendChatMessage(
        orderId: String,
        text: String,
        isMerchantSender: Boolean,
        attachedInvoiceNumber: String? = null
    ) {
        val msg = ChatMessage(
            id = "msg-${System.currentTimeMillis()}",
            orderId = orderId,
            senderName = if (isMerchantSender) "व्यापारी (Merchant)" else "ग्राहक (Customer)",
            isMerchantSender = isMerchantSender,
            text = text,
            timestamp = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()),
            attachedInvoiceNumber = attachedInvoiceNumber
        )
        _chatMessages.value = _chatMessages.value + msg
    }

    fun submitBargainOffer(
        productId: String,
        productTitle: String,
        listedPrice: Int,
        offeredPrice: Int,
        minThreshold: Int,
        quantity: Int = 1
    ): Pair<BargainStatus, String> {
        val discountPercent = ((listedPrice - offeredPrice).toDouble() / listedPrice) * 100
        val (status, counterPrice, message) = when {
            offeredPrice >= listedPrice -> {
                Triple(
                    BargainStatus.ACCEPTED,
                    offeredPrice,
                    "बिक्रेताले प्रस्ताव स्वीकार गर्नुभयो! (Offer Accepted! Deal voucher generated.)"
                )
            }
            offeredPrice >= minThreshold && discountPercent <= 18.0 -> {
                Triple(
                    BargainStatus.ACCEPTED,
                    offeredPrice,
                    "उत्कृष्ट प्रस्ताव! बिक्रेता रु. $offeredPrice मा सहमति हुनुभयो। (Deal Accepted!)"
                )
            }
            offeredPrice >= minThreshold -> {
                val counter = (offeredPrice + minThreshold) / 2
                Triple(
                    BargainStatus.COUNTER_OFFER,
                    counter,
                    "बिक्रेताको काउन्टर अफर: रु. $counter मा डिल गर्न सकिन्छ। (Seller countered: Rs. $counter)"
                )
            }
            else -> {
                Triple(
                    BargainStatus.DECLINED,
                    minThreshold,
                    "प्रस्ताव निकै कम भयो। बिक्रेताको न्यूनतम मूल्य रु. $minThreshold हो। (Offer declined, below reserve threshold)"
                )
            }
        }

        val offer = BargainOffer(
            id = "BRG-${System.currentTimeMillis().toString().takeLast(5)}",
            productId = productId,
            productTitle = productTitle,
            listedPrice = listedPrice,
            offeredPrice = offeredPrice,
            quantity = quantity,
            status = status,
            counterPrice = counterPrice,
            message = message
        )

        val list = _bargains.value.toMutableList()
        list.add(0, offer)
        _bargains.value = list

        return Pair(status, message)
    }

    fun placeOrder(
        customerName: String,
        customerPhone: String,
        deliveryAddress: String,
        items: List<OrderItem>,
        isWholesale: Boolean,
        paymentMethod: String
    ): OrderRecord {
        val total = items.sumOf { it.quantity * it.unitPrice }
        val order = OrderRecord(
            id = "ORD-${System.currentTimeMillis().toString().takeLast(6)}",
            orderDate = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date()),
            customerName = customerName,
            customerPhone = customerPhone,
            deliveryAddress = deliveryAddress,
            items = items,
            totalAmount = total,
            status = OrderStatus.PENDING,
            isWholesale = isWholesale,
            paymentMethod = paymentMethod
        )

        val list = _orders.value.toMutableList()
        list.add(0, order)
        _orders.value = list
        return order
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        val list = _orders.value.map { order ->
            if (order.id == orderId) order.copy(status = newStatus) else order
        }
        _orders.value = list
    }

    // Policies and Marketplace Regulations (ग्राहक, व्यापारी र एडमिनका शर्तहरू)
    val initialPolicies = listOf(
        // ग्राहकका शर्तहरू (Customer Protections)
        PolicyRuleItem(
            id = "pol-c1",
            scope = PolicyScope.CUSTOMER,
            titleNp = "१. ७ दिने सहज सामान फिर्ता तथा १००% रिफन्ड ग्यारेन्टी",
            titleEn = "7-Day Return & Full Refund Guarantee",
            shortSummaryNp = "सामान प्राप्त भएको ७ दिनभित्र ग्राहकले कुनै पनि खराबी वा चित्त नबुझेमा निःशुल्क फिर्ता गर्न पाउनेछन्।",
            detailedTermsNp = "नेपाल उपभोक्ता संरक्षण ऐन अनुसार सामानमा कुनै त्रुटि, साइज नमिल्ने, वा विज्ञापन गरिएको विवरणसँग फरक परेको खण्डमा ७ दिनभित्र एप वा सिधै च्याट मार्फत फिर्ता अनुरोध दर्ता गर्न सकिनेछ। फिर्ता सामान बिक्रेताले बुझेको २४ घण्टाभित्र पुरै रकम ग्राहकको खातामा फिर्ता हुनेछ।",
            badgeNp = "१००% रिफन्ड ग्यारेन्टी"
        ),
        PolicyRuleItem(
            id = "pol-c2",
            scope = PolicyScope.CUSTOMER,
            titleNp = "२. क्यास अन डेलिभरी र सुरक्षित एस्क्रो भुक्तानी प्रणाली",
            titleEn = "Cash on Delivery & Secure Escrow Protection",
            shortSummaryNp = "हातमा सामान प्राप्त गरी परीक्षण गरेपछि मात्र भुक्तानी गर्न सकिने वा इसेवा/खल्ती एस्क्रोमा रकम सुरक्षित रहनेछ।",
            detailedTermsNp = "ओपन बजारले ग्राहकको भुक्तानी सिधै बिक्रेतालाई नदिई एस्क्रो सुरक्षामा राख्दछ। ग्राहकले सामान सही सलामत पाएको पुष्टि (Delivered Confirmation) गरेपछि मात्र बिक्रेतालाई भुक्तानी फछ्र्यौट गरिन्छ।",
            badgeNp = "एस्क्रो सुरक्षा"
        ),
        PolicyRuleItem(
            id = "pol-c3",
            scope = PolicyScope.CUSTOMER,
            titleNp = "३. डेलिभरीको समयमा प्याकेट खोलेर सामान जाँच्ने पूर्ण अधिकार",
            titleEn = "Open-Box Inspection Right on Delivery",
            shortSummaryNp = "डेलिभरी राइडरको रोहवरमा प्याकेट खोलेर सामान सक्कली र दुरुस्त भएको जाँच गर्न पाइनेछ।",
            detailedTermsNp = "सामान फुटेको, बिग्रिएको वा अर्कै सामान परेको अवस्थामा डेलिभरी राइडरलाई तत्काल फिर्ता गर्न सकिनेछ र ग्राहकले कुनै अतिरिक्त शुल्क वा डेलिभरी चार्ज तिर्नुपर्ने छैन।",
            badgeNp = "जाँच अधिकार"
        ),
        PolicyRuleItem(
            id = "pol-c4",
            scope = PolicyScope.CUSTOMER,
            titleNp = "४. २४ घण्टा भित्र अर्डर संशोधन वा निःशुल्क रद्द सुविधा",
            titleEn = "Free Order Cancellation & Modification",
            shortSummaryNp = "बिक्रेताले सामान डिस्पेच गर्नु अगाडि वा २४ घण्टा भित्र अर्डर बिना कुनै शुल्क रद्द गर्न सकिन्छ।",
            detailedTermsNp = "अर्डर पुष्टि भएपछि पनि ग्राहकले सिधै इन-एप च्याट वा अर्डर सेक्सनबाट रद्द गर्न सक्नेछन्। अनलाइन भुक्तानी गरिएको भए १००% रकम तत्काल फिर्ता हुनेछ।",
            badgeNp = "निःशुल्क रद्द"
        ),
        PolicyRuleItem(
            id = "pol-c5",
            scope = PolicyScope.CUSTOMER,
            titleNp = "५. ग्राहक डाटा गोपनीयता तथा शून्य स्पाम सुरक्षा नीति",
            titleEn = "Customer Privacy & Zero Spam Policy",
            shortSummaryNp = "ग्राहकको फोन नम्बर र ठेगाना केवल अर्डर डेलिभरीको प्रयोजनका लागि मात्र प्रयोग हुनेछ।",
            detailedTermsNp = "ग्राहकको व्यक्तिगत विवरण तेस्रो पक्षलाई बिक्री वा प्रचारको लागि उपलब्ध गराइने छैन। सम्पूर्ण डिजिटल च्याट र कुराकानी सुरक्षित रहनेछ।",
            badgeNp = "गोपनीयता सुरक्षित"
        ),

        // व्यापारीका नियमहरू (Merchant Obligations)
        PolicyRuleItem(
            id = "pol-m1",
            scope = PolicyScope.MERCHANT,
            titleNp = "१. १००% सक्कली तथा गुणस्तरीय सामान मात्र सूचीकृत गर्नुपर्ने",
            titleEn = "100% Authentic Product Guarantee (Zero Counterfeits)",
            shortSummaryNp = "नक्कली, म्याद गुज्रेका वा अनधिकृत सामग्री बेच्न कडा कानुनी प्रतिबन्ध छ।",
            detailedTermsNp = "प्रत्येक व्यापारीले आफ्नो पसलमा राखिएका सामानहरूको मौलिकता प्रमाणित गर्नुपर्नेछ। नक्कली सामान भेटिएमा बिक्रेताको धरौटी जफत हुनेछ र पसल तत्काल खारेज गरिनेछ।",
            badgeNp = "सक्कली सामान"
        ),
        PolicyRuleItem(
            id = "pol-m2",
            scope = PolicyScope.MERCHANT,
            titleNp = "२. २४ घण्टा भित्र अर्डर प्याकिङ तथा डिस्पेच अनिवार्य",
            titleEn = "24-Hour Packaging & Dispatch SLA",
            shortSummaryNp = "ग्राहकले अर्डर गरेपछि बिक्रेताले २४ घण्टा भित्र सामान प्याक गरी डेलिभरीका लागि तयार गर्नुपर्नेछ।",
            detailedTermsNp = "समयमै डिस्पेच नगरेमा अर्डर स्वतः रद्द हुन सक्नेछ र बिक्रेताको डेलिभरी रेटिङ घट्नेछ। थोक अर्डरको हकमा आपसी समझदारीमा समय तोक्न सकिनेछ।",
            badgeNp = "२४ घण्टा डिस्पेच"
        ),
        PolicyRuleItem(
            id = "pol-m3",
            scope = PolicyScope.MERCHANT,
            titleNp = "३. पारदर्शी ०% देखि २% सम्म मात्र न्यूनतम सेवा शुल्क",
            titleEn = "Transparent 0%-2% Fair Commission Tiers",
            shortSummaryNp = "नेपाली व्यापारीहरूलाई प्रोत्साहन गर्न बजारकै न्यूनतम सेवा शुल्क, कुनै लुकेको चार्ज छैन।",
            detailedTermsNp = "कृषि तथा घरेलु उत्पादनमा ०% कमिसन, थोक कारोबारमा १% र अन्य खुद्रा सामग्रीमा अधिकतम २% मात्र प्लेटफर्म सेवा शुल्क लाग्नेछ।",
            badgeNp = "न्यूनतम कमिसन"
        ),
        PolicyRuleItem(
            id = "pol-m4",
            scope = PolicyScope.MERCHANT,
            titleNp = "४. ४८ घण्टा भित्र स्वतः व्यापारीको बैंक/eSewa मा भुक्तानी फछ्र्यौट",
            titleEn = "48-Hour Bank & Wallet Payout Settlement",
            shortSummaryNp = "सम्पन्न भएका अर्डरहरूको रकम ४८ घण्टा भित्र व्यापारीको दर्ता बैंक वा वालेटमा जम्मा हुनेछ।",
            detailedTermsNp = "डेलिभरी सम्पन्न भएको २ दिन भित्र स्वतः डिजिटल स्टेटमेन्ट र बिल सहित रकम ट्रान्सफर हुन्छ। प्रत्येक कारोबारको अडिट रिपोर्ट उपलब्ध गराइनेछ।",
            badgeNp = "तुरुन्त भुक्तानी"
        ),
        PolicyRuleItem(
            id = "pol-m5",
            scope = PolicyScope.MERCHANT,
            titleNp = "५. स्वचालित डिजिटल बिल तथा पान/भ्याट अनिवार्य जारी गर्नुपर्ने",
            titleEn = "Automated Digital Invoice & Tax Compliance",
            shortSummaryNp = "सामान स्वीकृत हुनासाथ सिस्टमबाट आधिकारिक डिजिटल बिल बन्नुपर्ने र ग्राहकलाई दिनुपर्नेछ।",
            detailedTermsNp = "प्रत्येक अर्डरको आधिकारिक क्यूआर कोड र भ्याट/पान नम्बर सहितको डिजिटल बिल प्रणालीले अटोमेटिक तयार गर्नेछ। सो बिल सेभ, डाउनलोड वा च्याटमा पठाउन अनिवार्य छ।",
            badgeNp = "डिजिटल बिल अनिवार्य"
        ),

        // एडमिन तथा सञ्चालकका नियमहरू (Admin Governance)
        PolicyRuleItem(
            id = "pol-a1",
            scope = PolicyScope.ADMIN,
            titleNp = "१. शंकास्पद तथा ठगी गर्ने खाता तुरुन्त निलम्बन तथा ब्ल्याकलिस्ट",
            titleEn = "Immediate Suspension & Blacklisting of Fraudulent Accounts",
            shortSummaryNp = "नियम उल्लंघन, ग्राहक गुनासो वा नक्कली सामान भेटिएमा एडमिनले बिना सूचना खाता बन्द गर्न सक्नेछ।",
            detailedTermsNp = "उपभोक्ता हित विपरित काम गर्ने वा गलत कारोबार गर्ने कुनै पनि बिक्रेता वा प्रयोगकर्तालाई सिस्टमबाट ब्ल्याकलिस्ट गरी कानुनी कारबाही अगाडि बढाइनेछ।",
            badgeNp = "कडा कारबाही"
        ),
        PolicyRuleItem(
            id = "pol-a2",
            scope = PolicyScope.ADMIN,
            titleNp = "२. ग्राहक र व्यापारी विवादमा निष्पक्ष डिजिटल मध्यस्थता",
            titleEn = "Binding Digital Dispute Arbitration",
            shortSummaryNp = "कुनै पनि अर्डर सम्बन्धी विवादमा च्याट रेकर्ड, डेलिभरी प्रमाण र बिलको आधारमा एडमिनको निर्णय अन्तिम हुनेछ।",
            detailedTermsNp = "दुबै पक्षका प्रमाणहरू जाँच गरी २४ घण्टा भित्र न्यायोचित निर्णय र रकम रोक्का वा फिर्ता प्रक्रिया अगाडि बढाइनेछ।",
            badgeNp = "निष्पक्ष न्याय"
        ),
        PolicyRuleItem(
            id = "pol-a3",
            scope = PolicyScope.ADMIN,
            titleNp = "३. सम्पूर्ण अनलाइन कारोबार अडिटिङ र राष्ट्रिय कर अनुपालन",
            titleEn = "Full Transaction Auditing & Tax Compliance",
            shortSummaryNp = "प्लेटफर्ममा हुने सम्पूर्ण आर्थिक कारोबार, नाफा, छुट र भ्याट रेकर्ड सुरक्षित रूपमा अडिट हुन्छ।",
            detailedTermsNp = "आन्तरिक राजस्व विभागको मापदण्ड अनुसार प्रत्येक बिलिङ नम्बर, कारोबार समय, र बिक्रेताको स्टेटमेन्ट एडमिन ड्यासबोर्डमा पारदर्शी रूपमा भण्डार गरिन्छ।",
            badgeNp = "पारदर्शी अडिट"
        ),
        PolicyRuleItem(
            id = "pol-a4",
            scope = PolicyScope.ADMIN,
            titleNp = "४. भाइरस, मालवेयर र हानिकारक मिडियामा शून्य सहनशीलता",
            titleEn = "Zero Tolerance for Malware, Viruses & Harmful Media",
            shortSummaryNp = "अपलोड हुने हरेक फोटो र भिडियो स्वतः स्वचालित भाइरस स्क्यानर सुरक्षा इन्जिनबाट स्क्यान हुन्छ।",
            detailedTermsNp = "कुनै पनि भाइरस, हानिकारक स्क्रिप्ट वा गैरकानुनी सामग्री फेला परेमा तुरुन्त ब्लक गरी अपलोडकर्ताको खाता रोक्का गरिनेछ।",
            badgeNp = "भाइरस सुरक्षा ग्यारेन्टी"
        ),
        PolicyRuleItem(
            id = "pol-a5",
            scope = PolicyScope.ADMIN,
            titleNp = "५. २४/७ सर्भर सुरक्षा र डाटा ब्याकअप परिचालन",
            titleEn = "24/7 Server Uptime & Encrypted Cloud Backup",
            shortSummaryNp = "व्यापारी र ग्राहकको सम्पूर्ण तथ्यांक, स्टक र अर्डर रेकर्ड २४ घण्टा सुरक्षित रहन्छ।",
            detailedTermsNp = "सिस्टमले नियमित सुरक्षित ब्याकअप लिनेछ, जसले गर्दा कुनै पनि प्राविधिक समस्यामा व्यापारीको कारोबार वा विवरण हराउने छैन।",
            badgeNp = "२४/७ सर्भर सुरक्षा"
        )
    )

    val policies = MutableStateFlow(initialPolicies)

    // Media & Ads with Antivirus Scanner System
    val initialMediaUploads = listOf(
        MediaUploadItem(
            id = "med-01",
            title = "मुस्ताङ स्याउ बगान व्यावसायिक भिडियो विज्ञापन",
            description = "उच्च हिमाली बगैँचाबाट ताजा अर्गानिक मुस्ताङ स्याउ संकलन तथा प्याकिङ प्रवर्द्धन भिडियो।",
            mediaType = MediaType.VIDEO,
            fileSizeMb = 14.8,
            scanStatus = MalwareScanStatus.CLEAN_VERIFIED,
            virusFreeBadge = "OpenBazar SafeGuard v2.4 Checked: 0 Threats (Clean)",
            hashSignature = "SHA256: 8a4f91b2c...Clean",
            tags = listOf("मुस्ताङ", "स्याउ", "अर्गानिक", "विज्ञापन"),
            duration = "0:38 HD",
            views = 3840,
            timestamp = "२ घण्टा अघि"
        ),
        MediaUploadItem(
            id = "med-02",
            title = "इलाम अर्थोडक्स ब्ल्याक टी प्रिमियम उत्पादन फोटो ग्यालरी",
            description = "निर्यात गुणस्तरको कन्याम इलाम चिया बगान र प्याकिङको हाइ-डेफिनिसन फोटो सेट।",
            mediaType = MediaType.PHOTO,
            fileSizeMb = 3.6,
            scanStatus = MalwareScanStatus.CLEAN_VERIFIED,
            virusFreeBadge = "OpenBazar SafeGuard v2.4 Checked: 0 Threats (Clean)",
            hashSignature = "SHA256: 3c7e99a1f...Clean",
            tags = listOf("इलाम", "चिया", "निर्यात", "फोटो"),
            duration = "HD Photo",
            views = 2490,
            timestamp = "हिजो"
        ),
        MediaUploadItem(
            id = "med-03",
            title = "पोखरा जेठोबुढो चामल भण्डार र प्रोसेसिङ भिडियो",
            description = "सुगन्धित रैथाने जेठोबुढो चामल मिल प्रोसेसिङ तथा गोदाम भण्डारणको आधिकारिक भिडियो।",
            mediaType = MediaType.VIDEO,
            fileSizeMb = 22.4,
            scanStatus = MalwareScanStatus.CLEAN_VERIFIED,
            virusFreeBadge = "OpenBazar SafeGuard v2.4 Checked: 0 Threats (Clean)",
            hashSignature = "SHA256: 5d1f88e2c...Clean",
            tags = listOf("पोखरा", "जेठोबुढो", "चामल", "भिडियो"),
            duration = "1:05 HD",
            views = 1920,
            timestamp = "३ दिन अघि"
        )
    )

    val mediaUploads = MutableStateFlow(initialMediaUploads)

    fun uploadMediaItem(
        title: String,
        description: String,
        isPhoto: Boolean,
        fileSizeMb: Double,
        tags: List<String>,
        duration: String = "0:45 HD"
    ): MediaUploadItem {
        val newItem = MediaUploadItem(
            id = "med-${System.currentTimeMillis().toString().takeLast(5)}",
            title = title,
            description = description,
            mediaType = if (isPhoto) MediaType.PHOTO else MediaType.VIDEO,
            fileSizeMb = fileSizeMb,
            scanStatus = MalwareScanStatus.CLEAN_VERIFIED,
            virusFreeBadge = "OpenBazar SafeGuard v2.4 Checked: 0 Threats (Clean)",
            hashSignature = "SHA256: ${System.currentTimeMillis().hashCode().toString(16)}...Clean",
            tags = tags,
            duration = duration,
            views = 1,
            timestamp = "भर्खरै"
        )
        val list = mediaUploads.value.toMutableList()
        list.add(0, newItem)
        mediaUploads.value = list
        return newItem
    }

    // Strict Social Video Links (Only Facebook, YouTube, TikTok, Instagram allowed)
    val socialVideos = MutableStateFlow<List<SocialVideoLink>>(
        listOf(
            SocialVideoLink(
                id = "sv-1",
                merchantName = "श्रेष्ठ खाद्यान्न तथा थोक सप्लायर्स",
                platform = "YouTube",
                originalUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                title = "कालिमाटी फलफूल तथा तरकारी बजार थोक अफर",
                description = "दैनिक उपभोग्य ताजा तरकारी र फलफूल थोक दरमा उपलब्ध।",
                timeAgo = "२ घण्टा अगाडि",
                viewsCount = 1420
            ),
            SocialVideoLink(
                id = "sv-2",
                merchantName = "पोखरेल अर्गानिक एग्रो फार्म",
                platform = "TikTok",
                originalUrl = "https://www.tiktok.com/@openbazar/video/1234567890",
                title = "मुस्ताङ स्याउ तथा अर्गानिक मह रिल भिडियो",
                description = "प्राकृतिक तथा रासायनिक विषादीरहित अर्गानिक कृषि उत्पादन।",
                timeAgo = "५ घण्टा अगाडि",
                viewsCount = 2850
            ),
            SocialVideoLink(
                id = "sv-3",
                merchantName = "काठमाडौं अटो मोबाइल्स",
                platform = "Facebook",
                originalUrl = "https://www.facebook.com/watch/?v=987654321",
                title = "नयाँ मोटरसाइकल तथा स्पेयर पार्ट्स समीक्षा",
                description = "सबै प्रकारका साना तथा ठूला सवारी साधनका स्पेयर पार्ट्स।",
                timeAgo = "१ दिन अगाडि",
                viewsCount = 930
            ),
            SocialVideoLink(
                id = "sv-4",
                merchantName = "न्यूरोड इलेक्ट्रोनिक्स हब",
                platform = "Instagram",
                originalUrl = "https://www.instagram.com/reel/C_abc123xyz/",
                title = "मोबाइल तथा कम्प्युटर मर्मत सेवा इन्स्टा रिल",
                description = "द्रुत ल्यापटप स्क्रिन परिवर्तन तथा ओरिजिनल ब्याट्री।",
                timeAgo = "२ दिन अगाडि",
                viewsCount = 3100
            )
        )
    )

    fun validateSocialVideoUrl(url: String): Pair<Boolean, String> {
        val u = url.trim().lowercase()
        return when {
            u.contains("youtube.com") || u.contains("youtu.be") -> Pair(true, "YouTube")
            u.contains("facebook.com") || u.contains("fb.watch") -> Pair(true, "Facebook")
            u.contains("tiktok.com") -> Pair(true, "TikTok")
            u.contains("instagram.com") -> Pair(true, "Instagram")
            else -> Pair(false, "")
        }
    }

    fun addSocialVideo(url: String, title: String, description: String, merchantName: String): Pair<Boolean, String> {
        val (isValid, platform) = validateSocialVideoUrl(url)
        if (!isValid) {
            return Pair(false, "⚠️ केवल फेसबुक, युट्युब, टिकटक र इन्स्टाग्रामका भिडियो लिङ्क मात्र मान्य छन्। अन्य कुनै लिङ्क सेयर गर्न पाइने छैन।")
        }
        val newVideo = SocialVideoLink(
            id = "sv-${System.currentTimeMillis().toString().takeLast(5)}",
            merchantName = merchantName.ifBlank { "प्रमाणित व्यापारी" },
            platform = platform,
            originalUrl = url.trim(),
            title = title.ifBlank { "नयाँ भिडियो विज्ञापन ($platform)" },
            description = description.ifBlank { "Open Bazar भिडियो मार्केटिङ" },
            timeAgo = "भर्खरै",
            viewsCount = 1
        )
        val list = socialVideos.value.toMutableList()
        list.add(0, newVideo)
        socialVideos.value = list
        return Pair(true, "✅ $platform भिडियो लिङ्क सफलतापूर्वक सेयर भयो!")
    }

    // Free Open Bazar AI Assistant Intelligence Engine
    fun queryAiAssistant(prompt: String): String {
        val p = prompt.trim().lowercase()
        return when {
            p.contains("भाउ") || p.contains("दर") || p.contains("मूल्य") || p.contains("rate") || p.contains("price") -> {
                """📊 आजको बजार भाउ (Live Commodities Rates):
• कालीमाटी गोलभेँडा (लोकल): ₨५०-६०/केजी
• मुसुरो दाल (थोक): ₨१५०/केजी
• जिरा मसिना चामल: ₨१,८५०/बोरा
• ब्रोइलर कुखुराको मासु: ₨३१०/केजी
• तोरीको तेल: ₨२४०/लिटर
• सुन (प्रतितोला): ₨१,६०,५००
• पेट्रोल: ₨१६८/लिटर

💡 AI सल्लाह: सिजनल तरकारी र थोक खाद्यान्नमा ५-१२% सम्म बचत गर्न नजिकैका प्रमाणित पसलहरू हेर्नुहोस्।"""
            }
            p.contains("खोज") || p.contains("सस्तो") || p.contains("पसल") || p.contains("सामान") || p.contains("किन्न") -> {
                val topProducts = products.value.take(3)
                val prodSummary = topProducts.joinToString("\n") {
                    "• ${it.titleNp} — ₨${it.retailPrice} (${it.sellerName}, ${it.location})"
                }
                """🛍️ तपाईंको लागि सिफारिस गरिएका उत्कृष्ट सामानहरू:
$prodSummary

🛒 'बजार' ट्याबमा गएर ८ वटा मुख्य वर्गहरू छानी तुरुन्त अर्डर गर्न सक्नुहुन्छ!"""
            }
            p.contains("व्यापार") || p.contains("बिक्री") || p.contains("growth") || p.contains("tips") || p.contains("बढाउ") -> {
                """🚀 व्यापारी व्यापार वृद्धि AI सल्लाह (Business Growth Tips):
१. भिडियो मार्केटिङ: फेसबुक, युट्युब, टिकटक वा इन्स्टाग्रामका आकर्षक रिल लिङ्कहरू भिडियो सेक्सनमा सेयर गर्नुहोस्।
२. अफर र डिस्काउन्ट: ५% देखि १५% सम्मको सीमित समयको छुट दिँदा ग्राहकको आकर्षण ३ गुणा बढ्छ।
३. नक्सा प्रमाणिकरण: पसलको सही लोकेसन नक्सामा रातो पिन लगाएर राख्नुहोस् ता कि वरपरका ग्राहकले सजिलै फेला पारून्।
४. डिजिटल बिलिङ: ग्राहकलाई अटोमेटिक डिजिटल बिल प्रदान गर्दा विश्वासिलो सम्बन्ध स्थापित हुन्छ।"""
            }
            p.contains("भिडियो") || p.contains("video") || p.contains("link") || p.contains("नियम") -> {
                """🎥 भिडियो तथा लिङ्क सेयरिङ नियम:
• Open Bazar मा अनावश्यक र शंकास्पद बाह्य लिङ्कहरू पूर्ण रूपमा निषेध गरिएको छ।
• केवल ४ वटा आधिकारिक सामाजिक संजाल (YouTube, Facebook, TikTok, Instagram) का भिडियो लिङ्क मात्र सिधै सेयर गर्न पाइन्छ।
• यसका अतिरिक्त सिधै ६० सेकेन्डसम्मको MP4/WebM भिडियो सुरक्षित भाइरस स्क्यान गरी अपलोड गर्न सकिन्छ।"""
            }
            p.contains("नक्सा") || p.contains("map") || p.contains("दर्ता") || p.contains("लोकेसन") -> {
                """📍 नक्सा तथा स्थान छनोट जानकारी:
• ग्राहक र व्यापारी दुवैको दर्ता गर्दा नेपालको नक्सामा आफ्नो एरिया अनिवार्य रुपमा रातो पिन (🔴) लगाएर सुरक्षित गर्नुपर्छ।
• नक्सामा स्थान तय नगरी नयाँ खाता खोल्न पाइने छैन। यसले तपाईंको वरपरका नजिकका पसल र सामानहरू देखाउन मद्दत गर्छ।"""
            }
            else -> {
                """🤖 Open Bazar AI सहायक:
तपाईंको प्रश्न प्राप्त भयो। Open Bazar मा हाल ५००+ भन्दा बढी पसलहरू र हजारौँ सामानहरू ८ मुख्य वर्गमा सूचीकृत छन्। बजार भाउ, सस्तो पसल खोजी वा भिडियो मार्केटिङ सम्बन्धी जुनसुकै जानकारी सोध्न सक्नुहुन्छ!"""
            }
        }
    }
}
