package com.example.data

import com.example.model.*

object PortfolioRepository {

    const val ENGINEER_NAME = "Surya Kafle"
    const val ENGINEER_EMAIL = "suryakafle860@gmail.com"
    const val ENGINEER_TITLE = "Senior Full-Stack & Mobile Systems Architect"
    const val ENGINEER_LOCATION = "Kathmandu, Nepal (Open to Worldwide Remote)"
    const val ENGINEER_BIO =
        "Passionate systems engineer specializing in high-throughput marketplaces, offline-first architectures, Kotlin & Jetpack Compose mobile apps, and robust serverless cloud backends. Architect and lead developer of Aajako Bazar — Nepal's premier digital commerce ecosystem."
    const val GITHUB_URL = "https://github.com/suryakafle"
    const val LINKEDIN_URL = "https://linkedin.com/in/surya-kafle"
    const val PROJECT_URL = "https://aajakobazar.com"

    val flagshipProject = PortfolioProject(
        id = "aajako-bazar",
        title = "Aajako Bazar (आजको बजार)",
        subtitle = "Hyperlocal & Nationwide High-Throughput Commerce Ecosystem",
        category = "Enterprise Marketplace & E-Commerce",
        description = "A full-scale modern marketplace platform connecting thousands of local producers, tech vendors, and buyers with real-time inventory, dynamic bargaining, and offline PWA resilience.",
        fullOverview = "Aajako Bazar was designed to bridge the digital commerce divide in emerging markets. Built with an offline-first mindset, it delivers sub-120ms load times even on spotty 3G cellular connections. The platform incorporates automated geo-clustering, a culture-aware bargaining engine, phone verification, and zero-trust Firestore security.",
        techStack = listOf(
            "Kotlin & Jetpack Compose",
            "Progressive Web App (PWA)",
            "Firebase Cloud Firestore",
            "Cloud Functions & Cloud Run",
            "Algolia Geo-Search",
            "Offline Service Worker & Room DB",
            "Google Cloud Storage",
            "eSewa / Khalti Payment Gateway"
        ),
        metrics = listOf(
            MetricStat("Active Listings", "52,400+", "+18% MoM", isPositive = true, "Across 7 Provinces"),
            MetricStat("Monthly Visitors", "148,000+", "+34% YoY", isPositive = true, "99.98% High Availability"),
            MetricStat("Avg Response Time", "118 ms", "-42 ms", isPositive = true, "Global Edge CDN Caching"),
            MetricStat("Transaction GMV", "$1.85M+", "+62% YoY", isPositive = true, "Secure escrow & direct barter")
        ),
        challenges = "Nepal's terrain and erratic cellular connectivity often lead to dropped connections and sync failures in standard e-commerce apps. Traditional apps also failed to accommodate local cultural bargaining practices and rural agricultural sellers.",
        solution = "Architected a dual-cache synchronization layer (Service Workers + Room DB) with optimistic local mutation queues. Built a culture-first interactive negotiation protocol and simplified lightweight image compression pipeline saving 78% bandwidth.",
        keyFeatures = listOf(
            "Offline-First Catalog & Optimistic Order Placement",
            "Real-Time Smart Bargaining Engine with Instant Counter-offers",
            "Hyperlocal Radius Search (Within 5km, 15km, 50km or Nationwide)",
            "Zero-Trust Cloud Firestore Security Rules for Escrow & Identity",
            "Direct Seller WhatsApp & Cellular Telephony Handoff",
            "Multi-lingual Support (Nepali & English with Devanagari Script indexing)",
            "Automated Image Optimization & Lazy WebP Transformations"
        ),
        isFlagship = true,
        githubUrl = "https://github.com/suryakafle/aajako-bazar",
        liveUrl = "https://aajakobazar.com"
    )

    val architectureLayers = listOf(
        ArchitectureLayer(
            title = "1. Client & Presentation Tier",
            role = "Universal Access & Edge PWA",
            description = "Delivers high-speed native Android experience through Jetpack Compose alongside an installable PWA for web browsers. Employs aggressive service worker caching and declarative UI reactivity.",
            techStack = listOf("Jetpack Compose", "PWA Service Worker", "M3 Design System", "StateFlow / Coroutines"),
            highlights = listOf(
                "Sub-1 second time-to-interactive",
                "Full catalog availability when completely offline",
                "Adaptive layout across phones, tablets & desktop viewports"
            )
        ),
        ArchitectureLayer(
            title = "2. Edge & Networking Tier",
            role = "Intelligent Routing & Geo-DNS",
            description = "Handles request distribution, SSL termination, static asset compression, and instant edge caching.",
            techStack = listOf("Cloudflare Edge CDN", "HTTP/3 & QUIC", "Brotli Compression", "Edge Cache Rules"),
            highlights = listOf(
                "98.4% static asset cache hit ratio",
                "Automated DDoS mitigation and bot scrubbing",
                "Zero-downtime blue/green deployment switching"
            )
        ),
        ArchitectureLayer(
            title = "3. Business Logic & Serverless Compute",
            role = "Micro-services & Event Triggers",
            description = "Decoupled cloud functions for price negotiation algorithms, fraud detection, real-time push alerts, and thumbnail generation.",
            techStack = listOf("Firebase Cloud Functions", "Node.js / TypeScript", "Cloud Tasks", "Algolia Webhooks"),
            highlights = listOf(
                "Auto-scaling from 0 to 10,000 req/sec during flash sales",
                "Asynchronous background image processing",
                "Fraud scoring on new seller registration"
            )
        ),
        ArchitectureLayer(
            title = "4. Persistent Storage & Realtime Data",
            role = "Distributed NoSQL & Blob Storage",
            description = "Multi-region Firestore document store with granular zero-trust security rules and sub-millisecond snapshot streams.",
            techStack = listOf("Google Cloud Firestore", "Cloud Storage Buckets", "IndexedDB", "Room Database"),
            highlights = listOf(
                "Real-time bid updates without polling overhead",
                "Automated multi-region replication and daily snapshots",
                "Zero data loss with client-side mutation queues"
            )
        )
    )

    val codeSnippets = listOf(
        CodeSnippet(
            id = "firestore-rules",
            title = "Zero-Trust Firestore Security Rules",
            language = "firestore.rules",
            description = "Enforces strict authorization: sellers can only edit their listings, orders require authenticated participant IDs, and price modifications are cryptographically validated.",
            code = """rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Helper validation functions
    function isAuthenticated() {
      return request.auth != null;
    }
    function isOwner(userId) {
      return isAuthenticated() && request.auth.uid == userId;
    }
    
    // Product Listings
    match /products/{productId} {
      allow read: if true; // Public marketplace catalog
      allow create: if isAuthenticated() 
        && request.resource.data.price > 0
        && request.resource.data.sellerId == request.auth.uid;
      allow update, delete: if isAuthenticated() 
        && resource.data.sellerId == request.auth.uid;
    }

    // Direct Bargaining & Bids
    match /bids/{bidId} {
      allow read, create: if isAuthenticated();
      allow update: if isAuthenticated() && (
        resource.data.buyerId == request.auth.uid ||
        resource.data.sellerId == request.auth.uid
      );
    }
  }
}"""
        ),
        CodeSnippet(
            id = "sw-cache",
            title = "PWA Service Worker Offline Strategy",
            language = "JavaScript / ServiceWorker",
            description = "Implements a Stale-While-Revalidate caching pattern for market listings and Cache-First for visual assets to guarantee offline operability.",
            code = """const CACHE_NAME = 'aajako-bazar-v3.2';
const STATIC_ASSETS = [
  '/', '/index.html', '/css/style.css', 
  '/js/app.js', '/assets/logo.png'
];

self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME).then(cache => cache.addAll(STATIC_ASSETS))
  );
  self.skipWaiting();
});

self.addEventListener('fetch', event => {
  if (event.request.url.includes('/api/products')) {
    // Network first with offline cache fallback
    event.respondWith(
      fetch(event.request)
        .then(response => {
          const clone = response.clone();
          caches.open('market-data').then(c => c.put(event.request, clone));
          return response;
        })
        .catch(() => caches.match(event.request))
    );
  }
});"""
        ),
        CodeSnippet(
            id = "bargain-engine",
            title = "Kotlin Smart Bargaining State Machine",
            language = "Kotlin",
            description = "Dynamic price negotiation algorithm calculating seller elasticity, counter-offers, and deal acceptance threshold.",
            code = """sealed interface BargainState {
    data class OfferPending(val proposedPrice: Int) : BargainState
    data class CounterOffer(val counterPrice: Int, val message: String) : BargainState
    data class Accepted(val finalPrice: Int, val orderToken: String) : BargainState
    data class Declined(val minimumAcceptable: Int) : BargainState
}

fun evaluateBargainOffer(
    listedPrice: Int,
    offerPrice: Int,
    minThreshold: Int
): BargainState {
    val discountPercent = ((listedPrice - offerPrice).toDouble() / listedPrice) * 100
    return when {
        offerPrice >= listedPrice -> BargainState.Accepted(offerPrice, "TK-948102")
        offerPrice >= minThreshold && discountPercent <= 15.0 -> BargainState.Accepted(offerPrice, "TK-948102")
        offerPrice >= minThreshold -> {
            val counter = (offerPrice + minThreshold) / 2
            BargainState.CounterOffer(counter, "Seller offered middle ground: Rs. " + counter)
        }
        else -> BargainState.Declined(minThreshold)
    }
}"""
        )
    )

    val sampleMarketProducts = listOf(
        MarketProduct(
            id = "prod-1",
            title = "Mustang Organic Royal Apples (Box of 10kg)",
            category = "Krishi & Farm Fresh",
            priceNpr = 2400,
            originalPriceNpr = 2900,
            location = "Mustang -> Kathmandu Express",
            rating = 4.9f,
            reviewsCount = 184,
            sellerName = "Himalayan Orchards Co-Op",
            verifiedSeller = true,
            condition = "Certified Farm Fresh",
            description = "Crisp, sweet, pesticide-free high-altitude apples harvested directly from Marpha orchards in Mustang. Packed in eco-friendly protective crates.",
            bargainAcceptedMinPrice = 2150,
            tags = listOf("Organic", "Mustang", "Direct Farmer", "Express Delivery")
        ),
        MarketProduct(
            id = "prod-2",
            title = "Apple MacBook Pro 14\" M2 Pro (16GB / 512GB SSD)",
            category = "Electronics & Gadgets",
            priceNpr = 215000,
            originalPriceNpr = 245000,
            location = "New Road, Kathmandu",
            rating = 4.8f,
            reviewsCount = 92,
            sellerName = "TechnoHub Nepal (Surya Partner)",
            verifiedSeller = true,
            condition = "Pristine Like New (100% Battery)",
            description = "Space Gray, 100% battery health with only 18 cycles. Full box with original 67W MagSafe adapter and Apple warranty remaining.",
            bargainAcceptedMinPrice = 205000,
            tags = listOf("Apple", "M2 Pro", "Warranty", "Verified Tech")
        ),
        MarketProduct(
            id = "prod-3",
            title = "Pure Himalayan Cliff Wild Honey (500g Jar)",
            category = "Krishi & Farm Fresh",
            priceNpr = 1850,
            originalPriceNpr = 2200,
            location = "Lamjung Foothills",
            rating = 5.0f,
            reviewsCount = 310,
            sellerName = "Gurung Honey Hunters Clan",
            verifiedSeller = true,
            condition = "Raw & Unprocessed",
            description = "Ethically harvested rare mountain wild honey packed with natural medicinal pollen and rich floral aroma. Tested 100% pure.",
            bargainAcceptedMinPrice = 1700,
            tags = listOf("Raw Honey", "Wild Harvest", "Medicinal", "Handmade")
        ),
        MarketProduct(
            id = "prod-4",
            title = "Royal Enfield Classic 350 Reborn (2023 Stealth Black)",
            category = "Vehicles & Bikes",
            priceNpr = 485000,
            originalPriceNpr = 520000,
            location = "Patan, Lalitpur",
            rating = 4.7f,
            reviewsCount = 45,
            sellerName = "Rider Club Lalitpur",
            verifiedSeller = true,
            condition = "Single Owner (6,200 km)",
            description = "Fully serviced at authorized Royal Enfield service center. Dual-channel ABS, alloy wheels, crash guards, and fresh bluebook tax paid.",
            bargainAcceptedMinPrice = 465000,
            tags = listOf("Royal Enfield", "Single Hand", "Dual ABS", "Tax Cleared")
        ),
        MarketProduct(
            id = "prod-5",
            title = "Hand-woven Pure Cashmere Pashmina Shawl (Diamond Weave)",
            category = "Fashion & Handcrafts",
            priceNpr = 6500,
            originalPriceNpr = 8000,
            location = "Thamel Artisans Quarter",
            rating = 4.9f,
            reviewsCount = 128,
            sellerName = "Kathmandu Heritage Weavers",
            verifiedSeller = true,
            condition = "Authentic Chyangra Grade-A",
            description = "Feather-light 100% authentic Himalayan Chyangra cashmere hand-loomed by master artisans. Certified ring-test passed.",
            bargainAcceptedMinPrice = 5800,
            tags = listOf("Cashmere", "Heritage", "Artisan", "Handmade")
        ),
        MarketProduct(
            id = "prod-6",
            title = "Organic First Flush Ilam Orthodox Black Tea (1kg Wooden Box)",
            category = "Krishi & Farm Fresh",
            priceNpr = 3200,
            originalPriceNpr = 3800,
            location = "Kanyam, Ilam",
            rating = 4.9f,
            reviewsCount = 215,
            sellerName = "Ilam Tea Masters Estate",
            verifiedSeller = true,
            condition = "Spring First Flush 2026",
            description = "Hand-picked high-elevation whole leaf orthodox tea with natural muscatel and floral undertones. Packaged in sealed pine wood gift box.",
            bargainAcceptedMinPrice = 2900,
            tags = listOf("Ilam Tea", "First Flush", "Export Quality", "Wooden Box")
        )
    )

    val otherProjects = listOf(
        PortfolioProject(
            id = "himalaya-pos",
            title = "Himalaya Cloud POS",
            subtitle = "Distributed Retail ERP for High-Altitude & Offline Vendors",
            category = "Fintech & Enterprise Cloud",
            description = "Offline-first cloud point-of-sale system for retail stores across Nepal, featuring instant Bluetooth thermal receipt printing and automated tax reconciliation.",
            fullOverview = "Designed for stores with frequent electricity and network outages. Himalaya POS maintains a local SQLite database that seamlessly synchronizes with a multi-tenant backend upon reconnection.",
            techStack = listOf("Kotlin", "Jetpack Compose", "Room Database", "Bluetooth ESC/POS SDK", "Node.js Microservices", "PostgreSQL"),
            metrics = listOf(
                MetricStat("Store Deployments", "420+", "+15% QoQ", isPositive = true),
                MetricStat("Daily Invoices", "85,000+", "+28% YoY", isPositive = true),
                MetricStat("Offline Sync Rate", "100%", "Zero data collision", isPositive = true)
            ),
            challenges = "Eliminating dual-billing conflicts and ensuring thermal printers trigger instantaneously without blocking UI threads.",
            solution = "Developed an asynchronous coroutine spooler with vector timestamps (CRDT) for conflict-free multi-cashier sync.",
            keyFeatures = listOf(
                "Offline-First Invoicing with IRD Billing Certification",
                "Bluetooth & USB Thermal Receipt Engine",
                "Multi-Branch Stock Transfer & Expiry Alerts",
                "Real-time Profit & Margin Analytics"
            ),
            isFlagship = false
        ),
        PortfolioProject(
            id = "nepal-agri-connect",
            title = "Nepal AgriConnect",
            subtitle = "Direct Farm-to-Consumer Auction & Logistics Engine",
            category = "AgriTech & Supply Chain",
            description = "B2B agriculture marketplace connecting rural farmers in Dhading, Kavre, and Chitwan directly with Kathmandu grocers and bulk wholesalers.",
            fullOverview = "Bypasses middlemen cartels by introducing digital daily price boards and grouped shared cold-storage truck logistics.",
            techStack = listOf("React Native", "Android SDK", "Go (Golang)", "Redis Streams", "Firebase Cloud Messaging"),
            metrics = listOf(
                MetricStat("Farmer Earnings", "+32%", "Above traditional wholesale", isPositive = true),
                MetricStat("Produce Moved", "1,200 Tons", "Annual throughput", isPositive = true),
                MetricStat("Food Waste Reduction", "-24%", "Optimized route dispatch", isPositive = true)
            ),
            challenges = "Low smartphone literacy among elder rural farmers and unpredictable harvest schedules.",
            solution = "Created an intuitive icon-driven audio-assisted interface with IVR phone dialer integration and SMS price notifications.",
            keyFeatures = listOf(
                "Voice-guided Produce Listing",
                "Dynamic Daily Price Index (Kalimati Benchmark)",
                "Truck Cargo Aggregation & Shared Logistics",
                "Automated Farmer Bank Transfers"
            ),
            isFlagship = false
        ),
        PortfolioProject(
            id = "fin-nepal-microlend",
            title = "FinNepal MicroLend",
            subtitle = "Secure Community Ledger for Cooperative Finance",
            category = "Fintech & Security",
            description = "Paperless digital savings and micro-loan processing platform built for women-led cooperatives and savings groups across Nepal.",
            fullOverview = "Digitized over 30 traditional paper ledgers into an immutable audit-trailed system with biometric teller approvals.",
            techStack = listOf("Kotlin", "Spring Boot", "Docker", "AWS ECS", "PostgreSQL", "AES-256 Encryption"),
            metrics = listOf(
                MetricStat("Active Borrowers", "28,000+", "100% repayment tracking", isPositive = true),
                MetricStat("Audit Speed", "Instant", "Down from 3 weeks", isPositive = true),
                MetricStat("Data Integrity", "99.999%", "Cryptographic tamper logs", isPositive = true)
            ),
            challenges = "Regulatory compliance with Nepal Rastra Bank guidelines and safeguarding sensitive biometric records.",
            solution = "Enforced end-to-end envelope encryption, strict role-based access control, and daily off-site encrypted backups.",
            keyFeatures = listOf(
                "Biometric Agent Authentication",
                "Automated EMI Calculation & SMS Reminders",
                "Passbook NFC Scan Support",
                "Regulatory Compliance Export Engine"
            ),
            isFlagship = false
        )
    )

    val skillGroups = listOf(
        SkillGroup(
            category = "Mobile & Android Engineering",
            skills = listOf(
                SkillItem("Kotlin", 96, "Core"),
                SkillItem("Jetpack Compose", 94, "Modern UI"),
                SkillItem("Coroutines & Flow", 92, "Async"),
                SkillItem("Room DB & Offline Sync", 90, "Persistence"),
                SkillItem("Clean Architecture / MVI", 92, "Design Patterns"),
                SkillItem("Performance Profiling", 88, "Optimization")
            )
        ),
        SkillGroup(
            category = "Cloud, Serverless & Backend",
            skills = listOf(
                SkillItem("Firebase Firestore & Auth", 95, "Cloud NoSQL"),
                SkillItem("Cloud Functions & Cloud Run", 90, "Serverless"),
                SkillItem("Node.js & TypeScript", 88, "APIs"),
                SkillItem("PostgreSQL & SQL Tuning", 86, "RDBMS"),
                SkillItem("Algolia Search Integration", 88, "Indexing"),
                SkillItem("REST & GraphQL APIs", 92, "Protocols")
            )
        ),
        SkillGroup(
            category = "Web, PWA & Architecture",
            skills = listOf(
                SkillItem("Progressive Web Apps (PWA)", 94, "Service Workers"),
                SkillItem("System Design & High Concurrency", 90, "Architecture"),
                SkillItem("Zero-Trust Security & IAM", 88, "Infosec"),
                SkillItem("CI/CD & DevOps Automation", 85, "Tooling"),
                SkillItem("e-Commerce Payment Gateways", 92, "Fintech")
            )
        )
    )

    val skillsOrganizedByCategory = listOf(
        SkillGroup(
            category = "Languages",
            skills = listOf(
                SkillItem("Kotlin", 96, "Primary", "android", "6+ yrs"),
                SkillItem("TypeScript", 90, "Advanced", "code", "5+ yrs"),
                SkillItem("JavaScript (ES6+)", 92, "Core", "web", "6+ yrs"),
                SkillItem("SQL (PostgreSQL)", 88, "Advanced", "database", "5+ yrs"),
                SkillItem("Java", 86, "Proficient", "terminal", "6+ yrs"),
                SkillItem("HTML5 / CSS3", 94, "Expert", "web", "7+ yrs")
            )
        ),
        SkillGroup(
            category = "Frameworks & Libraries",
            skills = listOf(
                SkillItem("Jetpack Compose", 96, "Modern UI", "android", "4+ yrs"),
                SkillItem("Android SDK", 95, "Native", "phone", "6+ yrs"),
                SkillItem("Node.js & Express", 90, "Backend", "terminal", "5+ yrs"),
                SkillItem("Coroutines & StateFlow", 94, "Async", "sync", "5+ yrs"),
                SkillItem("Room DB & Offline Cache", 92, "Persistence", "storage", "5+ yrs"),
                SkillItem("PWA & Service Workers", 94, "Offline First", "web", "4+ yrs")
            )
        ),
        SkillGroup(
            category = "Tools & Cloud Infrastructure",
            skills = listOf(
                SkillItem("Firebase Cloud Firestore", 95, "Cloud NoSQL", "cloud", "5+ yrs"),
                SkillItem("Cloud Functions & Cloud Run", 90, "Serverless", "cloud", "4+ yrs"),
                SkillItem("Git & GitHub Actions", 92, "DevOps / CI", "git", "6+ yrs"),
                SkillItem("Algolia Search Engine", 88, "Geo-Search", "search", "3+ yrs"),
                SkillItem("Docker & Containers", 85, "DevOps", "container", "4+ yrs"),
                SkillItem("Zero-Trust Security & IAM", 90, "Infosec", "security", "4+ yrs")
            )
        )
    )

    val workExperiences = listOf(
        WorkExperience(
            role = "Lead Systems & Mobile Architect",
            organization = "Aajako Bazar Ecosystem",
            duration = "2023 - Present",
            location = "Kathmandu, Nepal",
            description = "Spearheaded end-to-end technical architecture, Android client development, and serverless cloud data layer for Nepal's fastest-growing multi-vendor marketplace.",
            achievements = listOf(
                "Scaled platform from zero to 148,000+ monthly active shoppers with 99.98% uptime.",
                "Engineered culture-first smart bargaining protocol reducing seller negotiation turnaround by 65%.",
                "Designed offline PWA & native Android sync architecture supporting sub-second transaction speeds on 3G."
            )
        ),
        WorkExperience(
            role = "Senior Full-Stack Engineer",
            organization = "Himalayan Tech Dynamics",
            duration = "2021 - 2023",
            location = "Kathmandu, Nepal",
            description = "Architected high-reliability retail point-of-sale applications, microservices, and payment gateway adapters.",
            achievements = listOf(
                "Deployed Himalaya POS to 420+ retail and hospitality outlets across 5 cities.",
                "Integrated eSewa, Khalti, and ConnectIPS payment protocols processing millions in NPR volume monthly.",
                "Mentored a cross-functional engineering team of 8 junior and mid-level developers."
            )
        ),
        WorkExperience(
            role = "Android Software Developer",
            organization = "Nepal Software Innovations",
            duration = "2019 - 2021",
            location = "Kathmandu, Nepal",
            description = "Built native Android apps for community banking, transit tracking, and logistics aggregators.",
            achievements = listOf(
                "Pioneered migration from legacy XML Views to Jetpack Compose with 40% reduction in code footprint.",
                "Achieved 4.8/5.0 average Play Store ratings across 4 published commercial applications."
            )
        )
    )

    val testimonials = listOf(
        Testimonial(
            clientName = "Bikash Shrestha",
            role = "Head of Digital Operations",
            company = "Himalaya Mart Co.",
            quote = "Surya is a rare engineer who blends deep technical mastery with sharp business instincts. His work on Aajako Bazar transformed how our merchants sell online. The offline resilience is pure genius.",
            rating = 5
        ),
        Testimonial(
            clientName = "Anjali Thapa",
            role = "Founder & CEO",
            company = "AgriNepal Supply",
            quote = "Surya delivered a flawless architecture on a tight timeline. His commitment to performance, clean Kotlin code, and data security made our platform resilient under peak harvest traffic spikes.",
            rating = 5
        ),
        Testimonial(
            clientName = "Prashant Karki",
            role = "VP of Engineering",
            company = "FinTech Himalaya",
            quote = "One of the best architects I have collaborated with. Surya’s understanding of zero-trust Firestore security rules and offline synchronization protocols set an engineering benchmark in our organization.",
            rating = 5
        )
    )
}
