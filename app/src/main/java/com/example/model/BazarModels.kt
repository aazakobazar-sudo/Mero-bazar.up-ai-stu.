package com.example.model

data class ProductComment(
    val id: String,
    val author: String,
    val text: String,
    val timeAgo: String,
    val rating: Int = 5
)

data class ProductItem(
    val id: String,
    val titleNp: String,
    val titleEn: String,
    val category: String,
    val retailPrice: Int,
    val wholesalePrice: Int,
    val minOrderQty: Int = 1,
    val stock: Int,
    val unit: String = "थान (Pcs)",
    val location: String,
    val rating: Float,
    val reviewCount: Int,
    val sellerName: String,
    val sellerPhone: String,
    val isVerifiedBusiness: Boolean = true,
    val isWholesaleAvailable: Boolean = true,
    val descriptionNp: String,
    val descriptionEn: String,
    val minBargainThreshold: Int,
    val tags: List<String>,
    // Requirement 1 & 2: Ads, Views, Comments, Shares, Growth
    val viewsCount: Int = 1420,
    val likesCount: Int = 98,
    val hasVideoAd: Boolean = true,
    val videoAdDuration: String = "0:45 HD",
    val comments: List<ProductComment> = listOf(
        ProductComment("c-1", "सुजन दाहाल", "गुणस्तर अत्यन्तै राम्रो छ, प्याकिङ पनि सुरक्षित थियो।", "२ घण्टा अगाडि", 5),
        ProductComment("c-2", "अनिता कार्की", "थोक दरमा समयमै डेलिभरी भयो। फेरि अर्डर गर्नेछु।", "१ दिन अगाडि", 5)
    ),
    // Requirement 6: Sales Growth percentage (Positive = growing, Negative = declining)
    val salesGrowthPercent: Double = 18.4,
    val monthlySoldUnits: Int = 340
)

data class MarketCommodityRate(
    val nameNp: String,
    val nameEn: String,
    val unit: String,
    val wholesalePrice: Int,
    val retailPrice: Int,
    val change: String,
    val isUp: Boolean
)

data class OrderItem(
    val productId: String,
    val title: String,
    val quantity: Int,
    val unitPrice: Int
)

data class OrderRecord(
    val id: String,
    val orderDate: String,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val items: List<OrderItem>,
    val totalAmount: Int,
    val status: OrderStatus,
    val isWholesale: Boolean = false,
    val paymentMethod: String = "Cash on Delivery",
    val generatedInvoice: DigitalInvoice? = null
)

enum class OrderStatus(val titleNp: String, val titleEn: String) {
    PENDING("प्रतीक्षारत (Pending)", "Pending"),
    PROCESSING("तयारी हुँदै (Processing)", "Processing"),
    SHIPPED("डेलिभरीमा (Shipped)", "Shipped"),
    DELIVERED("सम्पन्न (Delivered)", "Delivered")
}

// Requirement 4: Automated Official Digital Bill / Invoice
data class DigitalInvoice(
    val invoiceNumber: String,
    val orderId: String,
    val issueDate: String,
    val sellerName: String,
    val sellerPanVat: String = "301984521",
    val customerName: String,
    val customerPhone: String,
    val items: List<OrderItem>,
    val subtotal: Int,
    val vatAmount: Int, // 13% VAT
    val deliveryFee: Int = 0,
    val grandTotal: Int,
    val paymentStatus: String = "भुक्तानी सम्पन्न / क्यास अन डेलिभरी",
    val authorizedStampSignature: String = "OPEN BAZAR Verified Billing Gateway",
    val qrToken: String
)

// Requirement 5: Messenger / In-app Live Chat
data class ChatMessage(
    val id: String,
    val orderId: String,
    val senderName: String,
    val isMerchantSender: Boolean,
    val text: String,
    val timestamp: String,
    val attachedInvoiceNumber: String? = null
)

// Chart & Analytics: Daily Transaction Volume
data class DailyVolumeData(
    val dayNp: String,
    val dayEn: String,
    val volumeNpr: Int,
    val ordersCount: Int
)

// Requirement 6: Transaction Audit Ledger Record
data class AuditTransactionRecord(
    val id: String,
    val timestamp: String,
    val orderId: String,
    val customerName: String,
    val itemDescription: String,
    val amountNpr: Int,
    val profitMarginNpr: Int,
    val transactionType: String = "अनलाइन बिक्री (Online Sale)",
    val invoiceNumber: String
)

data class BargainOffer(
    val id: String,
    val productId: String,
    val productTitle: String,
    val listedPrice: Int,
    val offeredPrice: Int,
    val quantity: Int = 1,
    val status: BargainStatus,
    val counterPrice: Int = 0,
    val message: String = ""
)

enum class BargainStatus {
    PENDING, ACCEPTED, COUNTER_OFFER, DECLINED
}

data class WholesaleBusiness(
    val id: String,
    val name: String,
    val businessType: String,
    val location: String,
    val contactPerson: String,
    val phone: String,
    val panVat: String,
    val mainCategories: List<String>,
    val rating: Float,
    val verified: Boolean = true
)

enum class UserRole(val titleNp: String, val titleEn: String) {
    ADMIN("प्रशासक (Admin)", "Administrator"),
    MERCHANT("व्यापारी / बिक्रेता (Merchant)", "Merchant / Seller"),
    CUSTOMER("ग्राहक (Customer)", "Customer / Buyer")
}

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val businessName: String? = null,
    val panVat: String? = null,
    val isGoogleVerified: Boolean = false,
    val isVerified: Boolean = true,
    val joinedDate: String = "२०८१/०७/१५",
    val address: String = "काठमाडौं, नेपाल",
    val countryCode: String = "+977",
    val countryName: String = "नेपाल (Nepal)",
    val avatarUrl: String = "",
    val mapCoordinates: String? = null,
    val mapLocationName: String? = null,
    val businessCategory: String? = null,
    val registrationSource: String = "मोबाइल एप (+977 ओटिपी प्रमाणित)",
    val ordersCount: Int = 5,
    val lastActiveTime: String = "अहिले भर्खरै (Active Now)"
)

enum class MediaType(val titleNp: String, val titleEn: String) {
    PHOTO("फोटो (HD Photo)", "Photo"),
    VIDEO("भिडियो विज्ञापन (Video Ad)", "Video Ad")
}

enum class MalwareScanStatus(val labelNp: String, val isSafe: Boolean) {
    CLEAN_VERIFIED("भाइरस मुक्त प्रमाणित (Clean & Safe)", true),
    SCANNING("सुरक्षा स्क्यान हुँदैछ...", false),
    THREAT_DETECTED("खतरा फेला पर्यो (Threat Blocked)", false)
}

data class MediaUploadItem(
    val id: String,
    val title: String,
    val description: String,
    val mediaType: MediaType,
    val fileSizeMb: Double,
    val scanStatus: MalwareScanStatus = MalwareScanStatus.CLEAN_VERIFIED,
    val virusFreeBadge: String = "OpenBazar SafeGuard v2.4 Checked",
    val hashSignature: String,
    val tags: List<String>,
    val duration: String = "0:45 HD",
    val views: Int = 120,
    val timestamp: String
)

enum class PolicyScope(val titleNp: String, val titleEn: String) {
    CUSTOMER("ग्राहकका शर्तहरू", "Customer Protections"),
    MERCHANT("व्यापारीका नियमहरू", "Merchant Obligations"),
    ADMIN("एडमिन तथा सञ्चालक", "Admin Governance")
}

data class PolicyRuleItem(
    val id: String,
    val scope: PolicyScope,
    val titleNp: String,
    val titleEn: String,
    val shortSummaryNp: String,
    val detailedTermsNp: String,
    val badgeNp: String,
    val isCrucial: Boolean = true
)

data class MerchantStatement(
    val merchantId: String,
    val merchantName: String,
    val panVat: String,
    val totalRevenue: Long,
    val totalOrders: Int,
    val completedOrders: Int,
    val pendingOrders: Int,
    val vatCollected: Long,
    val platformFee: Long,
    val netPayout: Long,
    val status: String = "सक्रिय तथा प्रमाणित (Active & Verified)",
    val lastSettlementDate: String = "१० अक्टोबर २०२६"
)

data class MainCategoryTier(
    val id: Int,
    val nameNp: String,
    val nameEn: String,
    val emoji: String,
    val subcategoriesNp: List<String>
)

val BAZAR_EIGHT_CATEGORIES = listOf(
    MainCategoryTier(
        id = 1,
        nameNp = "दैनिक घरेलु सामाग्रीहरु",
        nameEn = "Daily Household Goods",
        emoji = "🧺",
        subcategoriesNp = listOf(
            "किराना,तरकारी तथा फलफुल",
            "कृषि उत्पादन तथा डेरी",
            "कपडा तथा गिफ्ट",
            "श्रीङ्गार तथा कस्मेटीक",
            "भाडा, औजार तथा अन्य",
            "माछा/मासु पसल"
        )
    ),
    MainCategoryTier(
        id = 2,
        nameNp = "सवारी साधन",
        nameEn = "Vehicles & Automotive",
        emoji = "🚗",
        subcategoriesNp = listOf(
            "मोटरसाइकल, कार तथा साइकल नयाँ",
            "ठुला तथा अन्य सवारी साधनहरू",
            "साना सवारी साधनका पाठपुर्जाहरू",
            "ठूलो सवारी साधनका पाठपुर्जाहरू",
            "सेकेन्ड ह्यान्ड सवारी साधनहरू"
        )
    ),
    MainCategoryTier(
        id = 3,
        nameNp = "विद्युतीय तथा नेटवर्क सामग्री",
        nameEn = "Electronics & Network",
        emoji = "⚡",
        subcategoriesNp = listOf(
            "मोबाइल तथा कम्प्युटर नयाँ",
            "मोबाइल तथा कम्प्युटरको सर्भिस सेन्टर",
            "इलेक्ट्रोनिक सामग्रीहरू",
            "इलेक्ट्रोनिक मर्मत",
            "सेकेन्ड ह्यान्ड मोबाइल तथा कम्प्युटर"
        )
    ),
    MainCategoryTier(
        id = 4,
        nameNp = "शैक्षिक तथा स्वास्थ्य",
        nameEn = "Education & Health",
        emoji = "🩺",
        subcategoriesNp = listOf(
            "स्वास्थ्य केन्द्र",
            "स्वास्थ्य सामग्रीहरू",
            "शैक्षिक केन्द्र",
            "शैक्षिक सामग्रीहरू",
            "भेटनरी"
        )
    ),
    MainCategoryTier(
        id = 5,
        nameNp = "सामग्री तथा उत्पादनहरू",
        nameEn = "Products & Manufacturing",
        emoji = "🏭",
        subcategoriesNp = listOf(
            "मुख्य व्यवसायीको उत्पादक",
            "उत्पादक केन्द्रीय होलसेल विक्रेता",
            "घरेलु तथा साना उद्योगहरू"
        )
    ),
    MainCategoryTier(
        id = 6,
        nameNp = "वित्तीय संस्था",
        nameEn = "Financial Institutions",
        emoji = "🏦",
        subcategoriesNp = listOf(
            "बैंक",
            "अन्य"
        )
    ),
    MainCategoryTier(
        id = 7,
        nameNp = "निर्माण सामग्री",
        nameEn = "Construction Materials",
        emoji = "🏗️",
        subcategoriesNp = listOf(
            "हार्डवेयर",
            "निर्माण व्यवसायी",
            "जडित कार्य"
        )
    ),
    MainCategoryTier(
        id = 8,
        nameNp = "अन्य सामग्री तथा औजार",
        nameEn = "Other Goods & Tools",
        emoji = "🔧",
        subcategoriesNp = listOf(
            "संगीत सामग्री",
            "कृषि औजार",
            "अन्य"
        )
    )
)

data class SocialVideoLink(
    val id: String,
    val merchantName: String,
    val platform: String,
    val originalUrl: String,
    val title: String,
    val description: String,
    val timeAgo: String = "भर्खरै",
    val viewsCount: Int = 240
)
