package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testFlagshipProjectData() {
    val flagship = com.example.data.PortfolioRepository.flagshipProject
    assertEquals("Aajako Bazar (आजको बजार)", flagship.title)
    assertTrue(flagship.isFlagship)
    assertTrue(flagship.metrics.isNotEmpty())
    assertTrue(flagship.techStack.isNotEmpty())
  }

  @Test
  fun testSampleMarketProducts() {
    val products = com.example.data.PortfolioRepository.sampleMarketProducts
    assertTrue(products.size >= 5)
    products.forEach { product ->
      assertTrue(product.priceNpr > 0)
      assertTrue(product.bargainAcceptedMinPrice > 0)
      assertTrue(product.bargainAcceptedMinPrice <= product.priceNpr)
    }
  }

  @Test
  fun testHeroSectionDataDefaults() {
    val repo = com.example.data.PortfolioRepository
    assertEquals("Surya Kafle", repo.ENGINEER_NAME)
    assertEquals("suryakafle860@gmail.com", repo.ENGINEER_EMAIL)
    assertTrue(repo.ENGINEER_BIO.isNotEmpty())
    assertTrue(repo.ENGINEER_TITLE.isNotEmpty())
  }

  @Test
  fun testProjectSectionDataRequirements() {
    val flagship = com.example.data.PortfolioRepository.flagshipProject
    assertTrue(flagship.title.contains("Aajako Bazar"))
    assertTrue(flagship.description.isNotEmpty())
    assertTrue(flagship.techStack.size >= 4)
    assertTrue(flagship.techStack.any { it.contains("Kotlin") })
    assertTrue(flagship.techStack.any { it.contains("Firestore") })
    assertTrue(flagship.techStack.any { it.contains("PWA") })
  }

  @Test
  fun testBazarRepositoryProductsAndWholesale() {
    val products = com.example.data.BazarRepository.products.value
    assertTrue(products.size >= 5)
    products.forEach { prod ->
      assertTrue(prod.retailPrice > 0)
      assertTrue(prod.wholesalePrice > 0)
      assertTrue(prod.wholesalePrice <= prod.retailPrice)
      assertTrue(prod.minOrderQty >= 1)
      assertTrue(prod.stock > 0)
      assertTrue(prod.sellerPhone.isNotEmpty())
    }
  }

  @Test
  fun testBazarCommoditiesAndWholesaleDirectory() {
    val rates = com.example.data.BazarRepository.dailyCommodityRates
    assertTrue(rates.size >= 6)
    val directory = com.example.data.BazarRepository.wholesaleDirectory
    assertTrue(directory.size >= 4)
  }

  @Test
  fun testBargainAndOrderFlow() {
    val repo = com.example.data.BazarRepository
    val (status, msg) = repo.submitBargainOffer(
      productId = "test-1",
      productTitle = "Test Item",
      listedPrice = 1000,
      offeredPrice = 900,
      minThreshold = 850
    )
    assertTrue(msg.isNotEmpty())
    assertEquals(com.example.model.BargainStatus.ACCEPTED, status)

    val order = repo.placeOrder(
      customerName = "Test Buyer",
      customerPhone = "+977-9800000000",
      deliveryAddress = "Kathmandu",
      items = listOf(com.example.model.OrderItem("test-1", "Test Item", 2, 900)),
      isWholesale = false,
      paymentMethod = "Cash on Delivery"
    )
    assertEquals(1800, order.totalAmount)
    assertEquals(com.example.model.OrderStatus.PENDING, order.status)
  }
}
