package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BazarRepository
import com.example.model.OrderStatus
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun OrdersCartScreen(
    onNavigateBackToMarket: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isNepali by BazarRepository.isNepaliLanguage.collectAsState()
    val ordersList by BazarRepository.orders.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("orders_cart_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
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
                                .background(PrimaryBlue.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (isNepali) "अर्डर तथा डेलिभरी ट्र्याकिङ" else "Orders & Delivery Tracking",
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = onNavigateBackToMarket,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isNepali) "बजार जानुहोस्" else "Shop Market",
                                color = DarkBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isNepali) "तपाईँका सक्रिय अर्डरहरू" else "Your Active Market Orders",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isNepali)
                            "काठमाडौँ उपत्यका तथा देशभर सुरक्षित डेलिभरी, इसेवा/खल्ती एस्क्रो र प्रत्यक्ष बिक्रेता सम्पर्क।"
                        else
                            "Nationwide tracked logistics, eSewa/Khalti escrow protection, and direct seller coordination.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        if (ordersList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isNepali) "कुनै अर्डर भेटिएन।" else "No active orders found.",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(ordersList) { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = order.id,
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            Box(
                                modifier = Modifier
                                    .background(
                                        when (order.status) {
                                            OrderStatus.DELIVERED -> AccentEmerald.copy(alpha = 0.15f)
                                            OrderStatus.SHIPPED -> PrimaryBlue.copy(alpha = 0.15f)
                                            else -> SecondaryAmber.copy(alpha = 0.15f)
                                        },
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (isNepali) order.status.titleNp else order.status.titleEn,
                                    color = when (order.status) {
                                        OrderStatus.DELIVERED -> AccentEmerald
                                        OrderStatus.SHIPPED -> PrimaryBlue
                                        else -> SecondaryAmber
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "मिति: ${order.orderDate} | भुक्तानी: ${order.paymentMethod}",
                            color = TextTertiary,
                            fontSize = 11.sp
                        )

                        Text(
                            text = "डेलिभरी गन्तव्य: ${order.deliveryAddress}",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Items
                        order.items.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "• ${item.title} (x${item.quantity})",
                                    color = TextPrimary,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "रु. ${NumberFormat.getNumberInstance(Locale.US).format(item.quantity * item.unitPrice)}",
                                    color = SecondaryAmber,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "कुल रकम: रु. ${NumberFormat.getNumberInstance(Locale.US).format(order.totalAmount)}",
                                color = AccentEmerald,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )

                            Button(
                                onClick = {
                                    Toast.makeText(context, "${order.id} को डेलिभरी स्थिति ट्र्याकिङ सक्रिय छ।", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (isNepali) "ट्र्याकिङ हेर्नुहोस्" else "Track Dispatch",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
