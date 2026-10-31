package com.example.model

data class MetricStat(
    val label: String,
    val value: String,
    val change: String,
    val isPositive: Boolean = true,
    val subtitle: String = ""
)

data class ArchitectureLayer(
    val title: String,
    val role: String,
    val description: String,
    val techStack: List<String>,
    val highlights: List<String>
)

data class CodeSnippet(
    val id: String,
    val title: String,
    val language: String,
    val description: String,
    val code: String
)

data class MarketProduct(
    val id: String,
    val title: String,
    val category: String,
    val priceNpr: Int,
    val originalPriceNpr: Int,
    val location: String,
    val rating: Float,
    val reviewsCount: Int,
    val sellerName: String,
    val verifiedSeller: Boolean,
    val condition: String, // e.g. "Brand New", "Certified Fresh", "Like New"
    val description: String,
    val bargainAcceptedMinPrice: Int,
    val tags: List<String>
)

data class PortfolioProject(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val description: String,
    val fullOverview: String,
    val techStack: List<String>,
    val metrics: List<MetricStat>,
    val challenges: String,
    val solution: String,
    val keyFeatures: List<String>,
    val isFlagship: Boolean = false,
    val githubUrl: String = "",
    val liveUrl: String = ""
)

data class SkillItem(
    val name: String,
    val level: Int, // 1 to 100
    val tag: String,
    val iconKey: String = "code",
    val yearsExp: String = "4+ yrs"
)

data class SkillGroup(
    val category: String,
    val skills: List<SkillItem>
)

data class WorkExperience(
    val role: String,
    val organization: String,
    val duration: String,
    val location: String,
    val description: String,
    val achievements: List<String>
)

data class Testimonial(
    val clientName: String,
    val role: String,
    val company: String,
    val quote: String,
    val rating: Int = 5
)
